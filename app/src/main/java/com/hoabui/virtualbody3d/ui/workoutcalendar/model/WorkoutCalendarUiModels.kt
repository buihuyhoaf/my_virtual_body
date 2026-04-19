package com.hoabui.virtualbody3d.ui.workoutcalendar.model

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarDayCellStatus
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarDaySummary
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutIntensityLevel
import java.time.LocalDate
import java.time.YearMonth

@Immutable
data class WorkoutCalendarDayCellUiModel(
    val date: LocalDate?,
    val inCurrentMonth: Boolean,
    val cellStatus: WorkoutCalendarDayCellStatus,
    val totalCaloriesKcal: Float,
    val intensityLevel: WorkoutIntensityLevel?,
    val isSelected: Boolean,
    val isToday: Boolean,
)

internal fun buildMonthGridCells(
    yearMonth: YearMonth,
    selected: LocalDate,
    today: LocalDate,
    summaries: Map<Long, WorkoutCalendarDaySummary>,
): List<WorkoutCalendarDayCellUiModel> {
    val first = yearMonth.atDay(1)
    val padBefore = (first.dayOfWeek.value - 1) % 7
    val length = yearMonth.lengthOfMonth()
    val cells = mutableListOf<WorkoutCalendarDayCellUiModel>()
    repeat(padBefore) {
        cells.add(
            WorkoutCalendarDayCellUiModel(
                date = null,
                inCurrentMonth = false,
                cellStatus = WorkoutCalendarDayCellStatus.Empty,
                totalCaloriesKcal = 0f,
                intensityLevel = null,
                isSelected = false,
                isToday = false,
            ),
        )
    }
    for (dom in 1..length) {
        val date = yearMonth.atDay(dom)
        val epoch = date.toEpochDay()
        val summary = summaries[epoch]
        val status = summary?.cellStatus ?: WorkoutCalendarDayCellStatus.Empty
        cells.add(
            WorkoutCalendarDayCellUiModel(
                date = date,
                inCurrentMonth = true,
                cellStatus = status,
                totalCaloriesKcal = summary?.totalCaloriesKcal ?: 0f,
                intensityLevel = summary?.intensityLevel,
                isSelected = date == selected,
                isToday = date == today,
            ),
        )
    }
    while (cells.size % 7 != 0) {
        cells.add(
            WorkoutCalendarDayCellUiModel(
                date = null,
                inCurrentMonth = false,
                cellStatus = WorkoutCalendarDayCellStatus.Empty,
                totalCaloriesKcal = 0f,
                intensityLevel = null,
                isSelected = false,
                isToday = false,
            ),
        )
    }
    return cells
}
