package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.local.db.WorkoutLogExerciseEntity
import com.hoabui.virtualbody3d.data.local.db.WorkoutLogExerciseWithDetails
import com.hoabui.virtualbody3d.data.local.db.WorkoutLogEnergyEntity
import com.hoabui.virtualbody3d.data.local.db.WorkoutLogSessionEntity
import com.hoabui.virtualbody3d.data.local.db.WorkoutLogSessionWithExercises
import com.hoabui.virtualbody3d.data.local.db.WorkoutLogSetEntity
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.workoutlog.WorkoutLogEnergyDetail
import com.hoabui.virtualbody3d.domain.model.workoutlog.WorkoutLogExerciseDetail
import com.hoabui.virtualbody3d.domain.model.workoutlog.WorkoutLogSessionDetail
import com.hoabui.virtualbody3d.domain.model.workoutlog.WorkoutLogSetDetail
import java.time.Instant
import java.util.Locale

internal fun WorkoutLogSessionWithExercises.toDomain(): WorkoutLogSessionDetail {
    return WorkoutLogSessionDetail(
        id = session.id,
        startInstant = Instant.ofEpochMilli(session.startEpochMillis),
        endInstant = Instant.ofEpochMilli(session.endEpochMillis),
        exercises = exercises.map { it.toDomain() },
    )
}

private fun WorkoutLogExerciseWithDetails.toDomain(): WorkoutLogExerciseDetail = WorkoutLogExerciseDetail(
    id = exercise.id,
    sessionId = exercise.sessionId,
    exerciseId = exercise.exerciseId,
    displayNameSnapshot = exercise.displayNameSnapshot,
    measurementMode = exercise.measurementMode.toMeasurementMode(),
    startInstant = Instant.ofEpochMilli(exercise.startTimeMillis),
    orderIndex = exercise.orderIndex,
    sets = sets.map { it.toDomain() },
    energy = energy.firstOrNull()?.toDomain(),
)

private fun WorkoutLogSetEntity.toDomain(): WorkoutLogSetDetail = WorkoutLogSetDetail(
    id = id,
    exerciseLogId = exerciseLogId,
    reps = reps,
    weightKg = weightKg,
    durationSeconds = durationSeconds,
    setIndex = setIndex,
)

private fun WorkoutLogEnergyEntity.toDomain(): WorkoutLogEnergyDetail = WorkoutLogEnergyDetail(
    exerciseLogId = exerciseLogId,
    kcal = kcal,
    bodyWeightUsed = bodyWeightUsed,
    metUsed = metUsed,
    epocFactorUsed = epocFactorUsed,
)

internal fun WorkoutLogSessionEntity.toDomain(): WorkoutLogSessionDetail = WorkoutLogSessionWithExercises(
    session = this,
    exercises = emptyList(),
).toDomain()

internal fun WorkoutLogExerciseEntity.toMeasurementMode(): ExerciseMeasurementMode =
    when (measurementMode.lowercase(Locale.ROOT)) {
    "duration" -> ExerciseMeasurementMode.Duration
    else -> ExerciseMeasurementMode.Strength
}

internal fun ExerciseMeasurementMode.toLogStorageValue(): String = when (this) {
    ExerciseMeasurementMode.Duration -> "duration"
    ExerciseMeasurementMode.Strength -> "strength"
}
