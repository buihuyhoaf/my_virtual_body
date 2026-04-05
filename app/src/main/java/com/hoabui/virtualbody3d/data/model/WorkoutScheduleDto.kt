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
    val notes: String?,
    val measurementMode: String = "strength",
    val durationSeconds: Int? = null,
    val sessionId: String? = null,
    /** Must match [com.hoabui.virtualbody3d.domain.model.exercise.DEFAULT_SESSION_LOCATION_ID] for legacy rows. */
    val locationId: String = "default",
    val exerciseImageResUrl: String? = null,
    val exerciseLocalImageName: String? = null,
)
