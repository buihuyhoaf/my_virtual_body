package com.hoabui.virtualbody3d.domain.model.workoutlog

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import java.time.Instant

data class WorkoutLogSessionInput(
    val id: String,
    val startInstant: Instant,
    val endInstant: Instant,
    val exercises: List<WorkoutLogExerciseInput>,
)

data class WorkoutLogExerciseInput(
    val exerciseId: String,
    val displayNameSnapshot: String,
    val measurementMode: ExerciseMeasurementMode,
    val startInstant: Instant,
    val orderIndex: Int,
    val sets: List<WorkoutLogSetInput>,
)

data class WorkoutLogSetInput(
    val reps: Int,
    val weightKg: Double,
    val durationSeconds: Int?,
    val setIndex: Int,
)

data class WorkoutLogSessionDetail(
    val id: String,
    val startInstant: Instant,
    val endInstant: Instant,
    val exercises: List<WorkoutLogExerciseDetail>,
)

data class WorkoutLogExerciseDetail(
    val id: String,
    val sessionId: String,
    val exerciseId: String,
    val displayNameSnapshot: String,
    val measurementMode: ExerciseMeasurementMode,
    val startInstant: Instant,
    val orderIndex: Int,
    val sets: List<WorkoutLogSetDetail>,
    val energy: WorkoutLogEnergyDetail?,
)

data class WorkoutLogSetDetail(
    val id: String,
    val exerciseLogId: String,
    val reps: Int,
    val weightKg: Double,
    val durationSeconds: Int?,
    val setIndex: Int,
)

data class WorkoutLogEnergyDetail(
    val exerciseLogId: String,
    val kcal: Float,
    val bodyWeightUsed: Double,
    val metUsed: Double,
    val epocFactorUsed: Double,
)
