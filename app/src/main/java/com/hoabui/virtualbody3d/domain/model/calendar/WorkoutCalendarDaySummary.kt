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

enum class WorkoutCalendarVolumeLevel {
    None,
    ActiveRecovery,
    SolidWorkout,
    HighVolume,
}

data class WorkoutCalendarDaySummary(
    val epochDay: Long,
    val cellStatus: WorkoutCalendarDayCellStatus,
    val totalCaloriesKcal: Float,
    val dailyExerciseCount: Int,
) {
    val volumeLevel: WorkoutCalendarVolumeLevel
        get() = when {
            dailyExerciseCount in 1..2 -> WorkoutCalendarVolumeLevel.ActiveRecovery
            dailyExerciseCount in 3..5 -> WorkoutCalendarVolumeLevel.SolidWorkout
            dailyExerciseCount > 5 -> WorkoutCalendarVolumeLevel.HighVolume
            else -> WorkoutCalendarVolumeLevel.None
        }
}
