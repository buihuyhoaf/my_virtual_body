package com.hoabui.virtualbody3d.domain.model.exercise

/**
 * Per-row execution state for a scheduled workout line (persisted in Room).
 */
enum class WorkoutExecutionStatus {
    Scheduled,
    Completed,
    Missed,
    Skipped,
}
