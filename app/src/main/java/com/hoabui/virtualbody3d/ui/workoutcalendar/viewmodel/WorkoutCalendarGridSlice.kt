package com.hoabui.virtualbody3d.ui.workoutcalendar.viewmodel

import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarDaySummary
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarSessionBlock
import java.time.LocalDate
import java.time.YearMonth

data class WorkoutCalendarGridSlice(
    val summariesByEpochDay: Map<Long, WorkoutCalendarDaySummary>,
    val sessionBlocks: List<WorkoutCalendarSessionBlock>,
    val visibleYearMonth: YearMonth,
    val selectedDate: LocalDate,
)
