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
    val totalCaloriesKcal: Float,
) {
    val intensityLevel: WorkoutIntensityLevel?
        get() = when {
            totalCaloriesKcal <= 0f -> null
            totalCaloriesKcal < 100f -> WorkoutIntensityLevel.Light
            totalCaloriesKcal <= 250f -> WorkoutIntensityLevel.Moderate
            else -> WorkoutIntensityLevel.High
        }
}
