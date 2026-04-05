package com.hoabui.virtualbody3d.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.hoabui.virtualbody3d.BuildConfig
import com.hoabui.virtualbody3d.data.local.GymLocationCatalog
import com.hoabui.virtualbody3d.data.local.WorkoutSessionLocalDataSource
import com.hoabui.virtualbody3d.data.local.db.WORKOUT_DB_TRACE_LOG_TAG
import com.hoabui.virtualbody3d.data.local.WorkoutScheduleLocalDataSource
import com.hoabui.virtualbody3d.data.local.db.VirtualBodyDatabase
import com.hoabui.virtualbody3d.data.local.db.WorkoutSessionDao
import com.hoabui.virtualbody3d.data.mapper.toDomain
import com.hoabui.virtualbody3d.data.mapper.toDto
import com.hoabui.virtualbody3d.data.mapper.toEntity
import com.hoabui.virtualbody3d.data.model.WorkoutScheduleDto
import com.hoabui.virtualbody3d.data.model.WorkoutSessionDto
import com.hoabui.virtualbody3d.di.IoDispatcher
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutSessionRepositoryImpl @Inject constructor(
    private val sessionLocalDataSource: WorkoutSessionLocalDataSource,
    private val workoutScheduleRepository: WorkoutScheduleRepository,
    private val gymLocationCatalog: GymLocationCatalog,
    private val virtualBodyDatabase: VirtualBodyDatabase,
    private val workoutScheduleLocalDataSource: WorkoutScheduleLocalDataSource,
    private val workoutSessionDao: WorkoutSessionDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : WorkoutSessionRepository {

    override fun observeGymLocations() = flow {
        emit(gymLocationCatalog.locations)
        awaitCancellation()
    }

    override fun observeWorkoutSessions(): Flow<List<WorkoutSession>> =
        workoutSessionDao.observeAllSessions()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)

    override fun observeBusyIntervals(
        date: LocalDate,
        zoneId: ZoneId,
        locationId: String,
    ): Flow<List<InstantInterval>> =
        combine(
            sessionLocalDataSource.observeSessionDtos(),
            workoutScheduleRepository.observeWorkoutSchedules(),
        ) { sessions, schedules ->
            buildBusyIntervalsForDay(
                date = date,
                zoneId = zoneId,
                locationId = locationId,
                sessionDtos = sessions,
                schedules = schedules,
            )
        }

    override suspend fun bookSession(
        session: WorkoutSession,
        lines: List<SessionExerciseLine>,
        zoneId: ZoneId,
    ): BookWorkoutSessionResult {
        if (lines.isEmpty()) return BookWorkoutSessionResult.InvalidDraft
        val proposed = InstantInterval(session.startInstant, session.endInstant)
        val sessions = sessionLocalDataSource.getAllDtos()
        val schedules = workoutScheduleRepository.getAllSchedules()
        val bookingDate = session.startInstant.atZone(zoneId).toLocalDate()
        val busy = buildBusyIntervalsForDay(
            date = bookingDate,
            zoneId = zoneId,
            locationId = session.locationId,
            sessionDtos = sessions,
            schedules = schedules,
        )
        if (!isIntervalFreeForBooking(proposed, busy)) {
            return BookWorkoutSessionResult.Conflict
        }
        val sessionEntity = session.toDto().toEntity(zoneId)
        val scheduledAt = LocalDateTime.ofInstant(session.startInstant, zoneId)
        val now = System.currentTimeMillis()
        return withContext(ioDispatcher) {
            try {
                virtualBodyDatabase.withTransaction {
                    workoutSessionDao.insertSession(sessionEntity)
                    for (line in lines) {
                        val entity = WorkoutSchedule(
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
                        ).toEntity(zoneId, now)
                        workoutScheduleLocalDataSource.upsert(entity)
                    }
                }
                if (BuildConfig.DEBUG) {
                    Log.d(WORKOUT_DB_TRACE_LOG_TAG, "bookSession transaction ok sessionId=${session.id}")
                }
                BookWorkoutSessionResult.Success(scheduledCount = lines.size)
            } catch (t: Throwable) {
                Log.e(WORKOUT_DB_TRACE_LOG_TAG, "bookSession transaction failed", t)
                BookWorkoutSessionResult.Conflict
            }
        }
    }

    override suspend fun migrateLegacySchedulesIfNeeded(zoneId: ZoneId) {
        val legacy = workoutScheduleRepository.getAllSchedules()
            .filter { it.sessionId == null }
            .sortedBy { it.scheduledAt.atZone(zoneId).toInstant().toEpochMilli() }
        var idx = 0
        while (idx < legacy.size) {
            val first = legacy[idx]
            val locationId = first.locationId
            val clusterStartMillis = first.scheduledAt.atZone(zoneId).toInstant().toEpochMilli()
            var j = idx + 1
            while (j < legacy.size) {
                val next = legacy[j]
                if (next.locationId != locationId) break
                if (next.scheduledAt.atZone(zoneId).toInstant().toEpochMilli() <=
                    clusterStartMillis + SESSION_BOOKING_DURATION_MINUTES * 60_000
                ) {
                    j++
                } else {
                    break
                }
            }
            val cluster = legacy.subList(idx, j)
            val startMillis = cluster.minOf { it.scheduledAt.atZone(zoneId).toInstant().toEpochMilli() }
            val start = Instant.ofEpochMilli(startMillis)
            val end = start.plusSeconds(SESSION_BOOKING_DURATION_MINUTES * 60L)
            val sessionId = UUID.randomUUID().toString()
            sessionLocalDataSource.insertSession(
                WorkoutSessionDto(
                    id = sessionId,
                    startEpochMillis = start.toEpochMilli(),
                    endEpochMillis = end.toEpochMilli(),
                    locationId = locationId,
                ),
                zoneId,
            )
            for (sch in cluster) {
                workoutScheduleRepository.saveWorkoutSchedule(
                    sch.copy(sessionId = sessionId),
                    planZoneId = zoneId,
                )
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
    schedules: List<WorkoutSchedule>,
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
    for (sch in schedules) {
        if (sch.sessionId != null) continue
        if (sch.locationId != locationId) continue
        val start = sch.scheduledAt.atZone(zoneId).toInstant()
        val iv = instantIntervalFromStart(start, SESSION_BOOKING_DURATION_MINUTES)
        if (iv.intersects(dayStart, dayEnd)) out.add(iv)
    }
    return out
}

/** Kept for tests / callers that still use DTO lists. */
internal fun buildBusyIntervalsForDayFromScheduleDtos(
    date: LocalDate,
    zoneId: ZoneId,
    locationId: String,
    sessionDtos: List<WorkoutSessionDto>,
    scheduleDtos: List<WorkoutScheduleDto>,
): List<InstantInterval> {
    val schedules = scheduleDtos.map { dto -> dto.toDomain() }
    return buildBusyIntervalsForDay(date, zoneId, locationId, sessionDtos, schedules)
}
