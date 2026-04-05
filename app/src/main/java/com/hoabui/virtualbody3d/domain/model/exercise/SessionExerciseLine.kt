package com.hoabui.virtualbody3d.domain.model.exercise

/**
 * One exercise line committed as part of a [WorkoutSession] (sets/reps/duration only).
 */
data class SessionExerciseLine(
    val exerciseId: String,
    val sets: Int,
    val reps: Int,
    val weightKg: Double,
    val restSeconds: Int,
    val notes: String?,
    val measurementMode: ExerciseMeasurementMode,
    val durationSeconds: Int?,
    val orderIndex: Int,
    val exerciseImageResUrl: String? = null,
    val exerciseLocalImageName: String? = null,
)
