package com.hoabui.virtualbody3d.domain.model.exercise

/**
 * Outcome of deleting a schedule row, including optional session removed when it had no
 * remaining references (for undo).
 */
data class WorkoutScheduleDeleteResult(
    val schedule: WorkoutSchedule,
    val removedSession: WorkoutSession?,
)
