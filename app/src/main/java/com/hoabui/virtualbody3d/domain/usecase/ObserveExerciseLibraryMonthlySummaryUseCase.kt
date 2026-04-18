package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.calendar.ExerciseLibraryMonthlySummary
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveExerciseLibraryMonthlySummaryUseCase @Inject constructor(
    private val observeWorkoutCalendarMonthSummaries: ObserveWorkoutCalendarMonthSummariesUseCase,
) {
    operator fun invoke(yearMonth: YearMonth): Flow<ExerciseLibraryMonthlySummary> =
        observeWorkoutCalendarMonthSummaries(yearMonth).map { summariesByEpochDay ->
            val workoutDays = summariesByEpochDay.size
            val restDays = (yearMonth.lengthOfMonth() - workoutDays).coerceAtLeast(0)
            ExerciseLibraryMonthlySummary(
                yearMonth = yearMonth,
                workoutDayCount = workoutDays,
                restDayCount = restDays,
            )
        }
}
