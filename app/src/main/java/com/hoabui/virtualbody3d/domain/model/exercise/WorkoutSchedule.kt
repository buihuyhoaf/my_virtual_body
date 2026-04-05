package com.hoabui.virtualbody3d.domain.model.exercise

import java.time.LocalDateTime

/**
 * Domain model for a scheduled workout entry (exercise + time + sets/reps or duration + weight/rest).
 */
data class WorkoutSchedule(
    /** Stable client-generated id (UUID); maps to Room [clientId]. */
    val id: String,
    /** Room row id when loaded from DB; null for new rows before insert. */
    val rowId: Long? = null,
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
    /** When set, this row belongs to an aggregated [WorkoutSession] and must not be counted again for busy slots. */
    val sessionId: String? = null,
    /** Facility used for conflict detection; legacy rows use [DEFAULT_SESSION_LOCATION_ID]. */
    val locationId: String = DEFAULT_SESSION_LOCATION_ID,
    val executionStatus: WorkoutExecutionStatus = WorkoutExecutionStatus.Scheduled,
    /** Denormalized snapshot from catalog at booking; see [toScheduleImageSnapshot]. */
    val exerciseImageResUrl: String? = null,
    val exerciseLocalImageName: String? = null,
)
