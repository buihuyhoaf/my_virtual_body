package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.calendar.ExerciseLibraryWeeklyDayItem
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Observes session counts for each day of the current week (Monday–Sunday), emitting
 * a fresh list whenever the underlying workout schedule data changes. Suitable for
 * powering a real-time weekly activity heatmap.
 */
class ObserveExerciseLibraryWeeklySummaryUseCase @Inject constructor(
    private val workoutScheduleRepository: WorkoutScheduleRepository,
) {
    operator fun invoke(today: LocalDate, zoneId: ZoneId): Flow<List<ExerciseLibraryWeeklyDayItem>> {
        val monday = today.with(DayOfWeek.MONDAY)
        val sunday = monday.plusDays(6)
        val startEpochDay = monday.toEpochDay()
        val endEpochDay = sunday.toEpochDay()
        return workoutScheduleRepository.observeSchedulesInDayRange(startEpochDay, endEpochDay)
            .map { schedules ->
                val countByDay = schedules
                    .groupBy { it.scheduledAt.atZone(zoneId).toLocalDate() }
                    .mapValues { (_, rows) -> rows.distinctBy { it.sessionId }.size }
                (0L..6L).map { offset ->
                    val date = monday.plusDays(offset)
                    ExerciseLibraryWeeklyDayItem(
                        date = date,
                        sessionCount = countByDay[date] ?: 0,
                        isToday = date == today,
                    )
                }
            }
    }
}
