package com.hoabui.virtualbody3d.domain.model

import java.time.LocalDateTime

/**
 * Domain model for a scheduled workout entry (exercise + time + sets/reps/weight/rest).
 */
data class WorkoutSchedule(
    val id: String,
    val exerciseId: String,
    val scheduledAt: LocalDateTime,
    val sets: Int,
    val reps: Int,
    val weightKg: Double,
    val restSeconds: Int,
    val notes: String?
)
