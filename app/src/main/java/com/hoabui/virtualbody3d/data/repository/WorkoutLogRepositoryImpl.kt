package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.ProgressTimelineLocalDataSource
import com.hoabui.virtualbody3d.data.local.WorkoutLogLocalDataSource
import com.hoabui.virtualbody3d.data.local.db.WorkoutLogEnergyEntity
import com.hoabui.virtualbody3d.data.local.db.WorkoutLogExerciseEntity
import com.hoabui.virtualbody3d.data.local.db.WorkoutLogSessionEntity
import com.hoabui.virtualbody3d.data.local.db.WorkoutLogSetEntity
import com.hoabui.virtualbody3d.data.mapper.toDomain
import com.hoabui.virtualbody3d.data.mapper.toLogStorageValue
import com.hoabui.virtualbody3d.di.IoDispatcher
import com.hoabui.virtualbody3d.domain.model.workoutlog.WorkoutLogSessionDetail
import com.hoabui.virtualbody3d.domain.model.workoutlog.WorkoutLogSessionInput
import com.hoabui.virtualbody3d.domain.repository.WorkoutLogRepository
import com.hoabui.virtualbody3d.domain.util.CaloriesCalculator
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Singleton
class WorkoutLogRepositoryImpl @Inject constructor(
    private val localDataSource: WorkoutLogLocalDataSource,
    private val progressTimelineLocalDataSource: ProgressTimelineLocalDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : WorkoutLogRepository {
    override fun observeWorkoutLogsByDay(dayKey: String): Flow<List<WorkoutLogSessionDetail>> =
        localDataSource.observeSessionsByDay(dayKey)
            .map { sessions -> sessions.map { it.toDomain() } }

    override suspend fun saveWorkoutLogSession(session: WorkoutLogSessionInput) = withContext(ioDispatcher) {
        val zoneId = session.zoneId
        val dayKey = session.startInstant.atZone(zoneId).toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val snapshotWeight = progressTimelineLocalDataSource
            .getLatestSnapshotOnOrBefore(dayKey)
            ?.weightKg
            ?.toDouble()
        val bodyWeightUsed = (snapshotWeight ?: DEFAULT_BODY_WEIGHT_KG).takeIf { it > 0.0 } ?: DEFAULT_BODY_WEIGHT_KG
        val sessionEntity = WorkoutLogSessionEntity(
            id = session.id,
            startEpochMillis = session.startInstant.toEpochMilli(),
            endEpochMillis = session.endInstant.toEpochMilli(),
            dayKey = dayKey,
            zoneId = zoneId.id,
        )
        val exerciseEntities = mutableListOf<WorkoutLogExerciseEntity>()
        val setEntities = mutableListOf<WorkoutLogSetEntity>()
        val energyEntities = mutableListOf<WorkoutLogEnergyEntity>()
        session.exercises.forEach { exercise ->
            val exerciseLogId = UUID.randomUUID().toString()
            exerciseEntities += WorkoutLogExerciseEntity(
                id = exerciseLogId,
                sessionId = session.id,
                exerciseId = exercise.exerciseId,
                displayNameSnapshot = exercise.displayNameSnapshot,
                measurementMode = exercise.measurementMode.toLogStorageValue(),
                startTimeMillis = exercise.startInstant.toEpochMilli(),
                orderIndex = exercise.orderIndex,
            )
            val sets = exercise.sets.sortedBy { it.setIndex }
            sets.forEach { set ->
                setEntities += WorkoutLogSetEntity(
                    id = UUID.randomUUID().toString(),
                    exerciseLogId = exerciseLogId,
                    reps = set.reps,
                    weightKg = set.weightKg,
                    durationSeconds = set.durationSeconds,
                    setIndex = set.setIndex,
                )
            }
            val totalReps = sets.sumOf { it.reps.coerceAtLeast(0) }
            val averageLoadKg = sets.mapNotNull { it.weightKg.takeIf { w -> w > 0.0 } }
                .let { loads -> if (loads.isEmpty()) 0.0 else loads.average() }
            val totalDurationSeconds = sets.sumOf { it.durationSeconds ?: 0 }
            val durationMinutes = totalDurationSeconds / 60.0
            val estimate = CaloriesCalculator.estimateCaloriesWithMetadata(
                exerciseId = exercise.exerciseId,
                measurementMode = exercise.measurementMode,
                durationMinutes = durationMinutes,
                totalReps = totalReps,
                averageLoadKg = averageLoadKg,
                bodyWeightKg = bodyWeightUsed,
                leanBodyMassKg = null,
            )
            energyEntities += WorkoutLogEnergyEntity(
                exerciseLogId = exerciseLogId,
                kcal = estimate.calories,
                bodyWeightUsed = bodyWeightUsed,
                metUsed = estimate.met,
                epocFactorUsed = estimate.epocFactor,
            )
        }
        localDataSource.insertFullSession(
            session = sessionEntity,
            exercises = exerciseEntities,
            sets = setEntities,
            energy = energyEntities,
        )
    }
}

private const val DEFAULT_BODY_WEIGHT_KG = 70.0
