package com.hoabui.virtualbody3d.data.model

/**
 * Persisted workout session aggregate (timeline + facility).
 */
data class WorkoutSessionDto(
    val id: String,
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val locationId: String,
)
