package com.hoabui.virtualbody3d.ui.body.state

import java.time.LocalDate
import java.time.YearMonth
import com.hoabui.virtualbody3d.ui.calendar.state.DailyItem

data class BodyScreenState(
    val uiState: BodyUiState = BodyUiState(),
    val dashboardUiState: BodyDashboardUiState = BodyDashboardUiState(),
    val selectedDate: LocalDate? = null,
    val calendarMonths: List<YearMonth> = emptyList(),
    val dailyItemsByDate: Map<LocalDate, List<DailyItem>> = emptyMap()
)
