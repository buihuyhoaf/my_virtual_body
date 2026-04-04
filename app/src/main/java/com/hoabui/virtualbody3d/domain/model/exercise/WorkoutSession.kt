package com.hoabui.virtualbody3d.domain.model.exercise

import java.time.Instant

/**
 * Aggregated workout session: global time range and facility, independent of per-exercise parameters.
 */
data class WorkoutSession(
    val id: String,
    val startInstant: Instant,
    val endInstant: Instant,
    val locationId: String,
)
