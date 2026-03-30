package com.hoabui.virtualbody3d.domain.model.exercise

import java.time.LocalDateTime

/**
 * Domain model for a scheduled workout entry (exercise + time + sets/reps or duration + weight/rest).
 */
data class WorkoutSchedule(
    val id: String,
    val exerciseId: String,
    val scheduledAt: LocalDateTime,
    val sets: Int,
    val reps: Int,
    val weightKg: Double,
    val restSeconds: Int,
    val notes: String?,
    val measurementMode: ExerciseMeasurementMode = ExerciseMeasurementMode.Strength,
    /** Total work duration in seconds when [measurementMode] is [ExerciseMeasurementMode.Duration]; null for strength. */
    val durationSeconds: Int? = null,
)
