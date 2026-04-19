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
    /**
     * Heatmap intensity thresholds are shared with exercise/session intensity mapping:
     * light < 100 kcal, moderate 100..250 kcal, high > 250 kcal.
     */
    val intensityLevel: WorkoutIntensityLevel?
        get() = when {
            totalCaloriesKcal <= 0f -> null
            totalCaloriesKcal < WORKOUT_INTENSITY_LIGHT_UPPER_BOUND_KCAL -> WorkoutIntensityLevel.Light
            totalCaloriesKcal <= WORKOUT_INTENSITY_MODERATE_UPPER_BOUND_KCAL -> WorkoutIntensityLevel.Moderate
            else -> WorkoutIntensityLevel.High
        }
}
