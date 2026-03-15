package com.hoabui.virtualbody3d.data.model

/**
 * Data transfer object for WorkoutSchedule persistence.
 * Uses epoch millis for scheduledAt to avoid dependency on java.time in data layer if needed.
 */
data class WorkoutScheduleDto(
    val id: String,
    val exerciseId: String,
    val scheduledAtEpochMillis: Long,
    val sets: Int,
    val reps: Int,
    val weightKg: Double,
    val restSeconds: Int,
    val notes: String?
)
