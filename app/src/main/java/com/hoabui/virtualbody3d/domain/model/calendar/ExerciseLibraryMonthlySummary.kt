package com.hoabui.virtualbody3d.domain.model.calendar

import java.time.YearMonth

/**
 * Distinct workout days vs rest days in a calendar month for the exercise library header.
 * [workoutDayCount] matches days with ≥1 schedule after [groupSchedulesToDaySummaries].
 */
data class ExerciseLibraryMonthlySummary(
    val yearMonth: YearMonth,
    val workoutDayCount: Int,
    val restDayCount: Int,
)
