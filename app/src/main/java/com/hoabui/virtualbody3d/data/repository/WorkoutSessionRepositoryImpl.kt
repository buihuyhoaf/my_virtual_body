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
import com.hoabui.virtualbody3d.data.model.WorkoutSessionDto
import com.hoabui.virtualbody3d.di.IoDispatcher
import com.hoabui.virtualbody3d.domain.model.exercise.SESSION_BOOKING_DURATION_MINUTES
import com.hoabui.virtualbody3d.domain.model.exercise.SessionExerciseLine
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSession
import com.hoabui.virtualbody3d.domain.repository.BookWorkoutSessionResult
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import com.hoabui.virtualbody3d.domain.repository.WorkoutSessionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
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

    override suspend fun bookSession(
        session: WorkoutSession,
        lines: List<SessionExerciseLine>,
    ): BookWorkoutSessionResult {
        val zoneId = ZoneId.systemDefault()
        if (lines.isEmpty()) {
            Log.w(WORKOUT_DB_TRACE_LOG_TAG, "bookSession: InvalidDraft — lines.isEmpty() sessionId=${session.id}")
            return BookWorkoutSessionResult.InvalidDraft
        }
        Log.d(
            WORKOUT_DB_TRACE_LOG_TAG,
            "bookSession: persisting sessionId=${session.id} lines=${lines.size} " +
                "start=${session.startInstant} end=${session.endInstant}",
        )
        val scheduledAt = LocalDateTime.ofInstant(session.startInstant, zoneId)
        val now = System.currentTimeMillis()
        val dayKey = session.startInstant.atZone(zoneId).toLocalDate().toEpochDay()
        val startMs = session.startInstant.toEpochMilli()
        val endMs = session.endInstant.toEpochMilli()
        return withContext(ioDispatcher) {
            try {
                val result = virtualBodyDatabase.withTransaction {
                    val existing = workoutSessionDao.findByLocationDayAndInterval(
                        locationId = session.locationId,
                        dayKey = dayKey,
                        startEpochMillis = startMs,
                        endEpochMillis = endMs,
                    )
                    if (existing != null) {
                        // Find-or-merge: drop incoming lines whose exerciseId is already on this session.
                        val existingExerciseIds =
                            workoutScheduleLocalDataSource.listDistinctExerciseIdsForSession(existing.id).toSet()
                        val linesToInsert = lines.filter { it.exerciseId !in existingExerciseIds }
                        val resolved = existing.toDomain()
                        for (line in linesToInsert) {
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
                                sessionId = resolved.id,
                                locationId = session.locationId,
                                exerciseImageResUrl = line.exerciseImageResUrl,
                                exerciseLocalImageName = line.exerciseLocalImageName,
                            ).toEntity(now)
                            workoutScheduleLocalDataSource.upsert(entity)
                        }
                        BookWorkoutSessionResult.Success(
                            scheduledCount = linesToInsert.size,
                            resolvedSession = resolved,
                        )
                    } else {
                        val sessionEntity = session.toDto().toEntity()
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
                                exerciseImageResUrl = line.exerciseImageResUrl,
                                exerciseLocalImageName = line.exerciseLocalImageName,
                            ).toEntity(now)
                            workoutScheduleLocalDataSource.upsert(entity)
                        }
                        BookWorkoutSessionResult.Success(
                            scheduledCount = lines.size,
                            resolvedSession = session,
                        )
                    }
                }
                if (BuildConfig.DEBUG) {
                    Log.d(
                        WORKOUT_DB_TRACE_LOG_TAG,
                        "bookSession transaction ok sessionId=${result.resolvedSession.id} scheduled=${result.scheduledCount}",
                    )
                }
                result
            } catch (t: Throwable) {
                Log.e(WORKOUT_DB_TRACE_LOG_TAG, "bookSession transaction failed", t)
                BookWorkoutSessionResult.Conflict
            }
        }
    }

    override suspend fun migrateLegacySchedulesIfNeeded() {
        val zoneId = ZoneId.systemDefault()
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
            )
            for (sch in cluster) {
                workoutScheduleRepository.saveWorkoutSchedule(
                    sch.copy(sessionId = sessionId),
                )
            }
            idx = j
        }
    }
}
