package com.hoabui.virtualbody3d.domain.model.calendar

/**
 * Aggregate status for a single calendar day (month grid / week strip).
 */
enum class WorkoutCalendarDayCellStatus {
    Empty,
    Scheduled,
    Completed,
    Missed,
    Mixed,
}

data class WorkoutCalendarDaySummary(
    val epochDay: Long,
    val cellStatus: WorkoutCalendarDayCellStatus,
)
