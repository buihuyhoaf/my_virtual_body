package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.calendar.groupSchedulesToDaySummaries
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarDaySummary
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveWorkoutCalendarMonthSummariesUseCase @Inject constructor(
    private val workoutScheduleRepository: WorkoutScheduleRepository,
) {
    operator fun invoke(yearMonth: YearMonth): Flow<Map<Long, WorkoutCalendarDaySummary>> {
        val start = yearMonth.atDay(1).toEpochDay()
        val end = yearMonth.atEndOfMonth().toEpochDay()
        return workoutScheduleRepository.observeSchedulesInDayRange(start, end)
            .map { schedules -> groupSchedulesToDaySummaries(schedules) }
    }
}
