package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.GymLocationCatalog
import com.hoabui.virtualbody3d.data.local.WorkoutScheduleLocalDataSource
import com.hoabui.virtualbody3d.data.local.WorkoutSessionLocalDataSource
import com.hoabui.virtualbody3d.data.mapper.toDto
import com.hoabui.virtualbody3d.data.model.WorkoutScheduleDto
import com.hoabui.virtualbody3d.data.model.WorkoutSessionDto
import com.hoabui.virtualbody3d.domain.model.exercise.InstantInterval
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_DURATION_MINUTES
import com.hoabui.virtualbody3d.domain.model.exercise.SessionExerciseLine
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSession
import com.hoabui.virtualbody3d.domain.model.exercise.instantIntervalFromStart
import com.hoabui.virtualbody3d.domain.model.exercise.isIntervalFreeForBooking
import com.hoabui.virtualbody3d.domain.repository.BookWorkoutSessionResult
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import com.hoabui.virtualbody3d.domain.repository.WorkoutSessionRepository
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutSessionRepositoryImpl @Inject constructor(
    private val scheduleLocalDataSource: WorkoutScheduleLocalDataSource,
    private val sessionLocalDataSource: WorkoutSessionLocalDataSource,
    private val workoutScheduleRepository: WorkoutScheduleRepository,
    private val gymLocationCatalog: GymLocationCatalog,
) : WorkoutSessionRepository {

    override fun observeGymLocations() = flow {
        emit(gymLocationCatalog.locations)
        awaitCancellation()
    }

    override fun observeBusyIntervals(
        date: LocalDate,
        zoneId: ZoneId,
        locationId: String,
    ): Flow<List<InstantInterval>> =
        combine(
            sessionLocalDataSource.sessions,
            scheduleLocalDataSource.schedules,
        ) { sessions, schedules ->
            buildBusyIntervalsForDay(
                date = date,
                zoneId = zoneId,
                locationId = locationId,
                sessionDtos = sessions,
                scheduleDtos = schedules,
            )
        }

    override suspend fun bookSession(
        session: WorkoutSession,
        lines: List<SessionExerciseLine>,
        zoneId: ZoneId,
    ): BookWorkoutSessionResult {
        if (lines.isEmpty()) return BookWorkoutSessionResult.InvalidDraft
        val proposed = InstantInterval(session.startInstant, session.endInstant)
        val sessions = sessionLocalDataSource.getAll()
        val schedules = scheduleLocalDataSource.getAll()
        val bookingDate = session.startInstant.atZone(zoneId).toLocalDate()
        val busy = buildBusyIntervalsForDay(
            date = bookingDate,
            zoneId = zoneId,
            locationId = session.locationId,
            sessionDtos = sessions,
            scheduleDtos = schedules,
        )
        if (!isIntervalFreeForBooking(proposed, busy)) {
            return BookWorkoutSessionResult.Conflict
        }
        sessionLocalDataSource.save(session.toDto())
        val scheduledAt = LocalDateTime.ofInstant(session.startInstant, zoneId)
        var count = 0
        for (line in lines) {
            workoutScheduleRepository.saveWorkoutSchedule(
                WorkoutSchedule(
                    id = UUID.randomUUID().toString(),
                    exerciseId = line.exerciseId,
                    scheduledAt = scheduledAt,
                    sets = line.sets,
                    reps = line.reps,
                    weightKg = line.weightKg,
                    restSeconds = line.restSeconds,
                    notes = line.notes,
                    measurementMode = line.measurementMode,
                    durationSeconds = line.durationSeconds,
                    sessionId = session.id,
                    locationId = session.locationId,
                ),
            )
            count++
        }
        return BookWorkoutSessionResult.Success(scheduledCount = count)
    }

    override suspend fun migrateLegacySchedulesIfNeeded(zoneId: ZoneId) {
        val legacy = scheduleLocalDataSource.getAll()
            .filter { it.sessionId == null }
            .sortedBy { it.scheduledAtEpochMillis }
        var idx = 0
        while (idx < legacy.size) {
            val first = legacy[idx]
            val locationId = first.locationId
            val clusterStartMillis = first.scheduledAtEpochMillis
            var j = idx + 1
            while (j < legacy.size) {
                val next = legacy[j]
                if (next.locationId != locationId) break
                if (next.scheduledAtEpochMillis <= clusterStartMillis + SESSION_BOOKING_DURATION_MINUTES * 60_000) {
                    j++
                } else {
                    break
                }
            }
            val cluster = legacy.subList(idx, j)
            val startMillis = cluster.minOf { it.scheduledAtEpochMillis }
            val start = Instant.ofEpochMilli(startMillis)
            val end = start.plusSeconds(SESSION_BOOKING_DURATION_MINUTES * 60)
            val sessionId = UUID.randomUUID().toString()
            sessionLocalDataSource.save(
                WorkoutSessionDto(
                    id = sessionId,
                    startEpochMillis = start.toEpochMilli(),
                    endEpochMillis = end.toEpochMilli(),
                    locationId = locationId,
                ),
            )
            for (dto in cluster) {
                scheduleLocalDataSource.save(dto.copy(sessionId = sessionId))
            }
            idx = j
        }
    }
}

internal fun buildBusyIntervalsForDay(
    date: LocalDate,
    zoneId: ZoneId,
    locationId: String,
    sessionDtos: List<WorkoutSessionDto>,
    scheduleDtos: List<WorkoutScheduleDto>,
): List<InstantInterval> {
    val dayStart = date.atStartOfDay(zoneId).toInstant()
    val dayEnd = date.plusDays(1).atStartOfDay(zoneId).toInstant()
    val out = mutableListOf<InstantInterval>()
    for (s in sessionDtos) {
        if (s.locationId != locationId) continue
        val iv = InstantInterval(
            start = Instant.ofEpochMilli(s.startEpochMillis),
            end = Instant.ofEpochMilli(s.endEpochMillis),
        )
        if (iv.intersects(dayStart, dayEnd)) out.add(iv)
    }
    for (sch in scheduleDtos) {
        if (sch.sessionId != null) continue
        if (sch.locationId != locationId) continue
        val start = Instant.ofEpochMilli(sch.scheduledAtEpochMillis)
        val iv = instantIntervalFromStart(start, SESSION_BOOKING_DURATION_MINUTES)
        if (iv.intersects(dayStart, dayEnd)) out.add(iv)
    }
    return out
}
