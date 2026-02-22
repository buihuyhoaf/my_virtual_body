package com.hoabui.virtualbody3d.ui.body.viewmodel

import androidx.lifecycle.ViewModel
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.usecase.GetBodyDashboardDataUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetBodyMetricsUseCase
import com.hoabui.virtualbody3d.ui.body.state.BodyScreenState
import com.hoabui.virtualbody3d.ui.calendar.state.DailyItem
import com.hoabui.virtualbody3d.ui.calendar.state.DailyItemType
import com.hoabui.virtualbody3d.ui.body.state.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.YearMonth

@HiltViewModel
class BodyViewModel @Inject constructor(
    getBodyMetricsUseCase: GetBodyMetricsUseCase,
    getBodyDashboardDataUseCase: GetBodyDashboardDataUseCase
) : ViewModel() {

    private val _screenState = MutableStateFlow(
        BodyScreenState(
            uiState = getBodyMetricsUseCase().toUiState(),
            dashboardUiState = getBodyDashboardDataUseCase().toUiState()
        )
    )
    val screenState: StateFlow<BodyScreenState> = _screenState.asStateFlow()

    init {
        if (_screenState.value.calendarMonths.isEmpty()) {
            loadMoreCalendarMonths()
        }
    }

    fun onDateSelected(date: LocalDate) {
        _screenState.value = _screenState.value.copy(selectedDate = date)
    }

    fun loadMoreCalendarMonths() {
        val currentState = _screenState.value
        val nextStartMonth = currentState.calendarMonths.lastOrNull()?.plusMonths(1)
            ?: YearMonth.now()
        val newMonths = List(CalendarPaging.monthBatchSize) { index ->
            nextStartMonth.plusMonths(index.toLong())
        }
        val appendedMap = buildMap {
            putAll(currentState.dailyItemsByDate)
            newMonths.forEach { month ->
                putAll(generateItemsForMonth(month))
            }
        }
        _screenState.value = currentState.copy(
            calendarMonths = currentState.calendarMonths + newMonths,
            dailyItemsByDate = appendedMap
        )
    }

    private fun generateItemsForMonth(month: YearMonth): Map<LocalDate, List<DailyItem>> {
        return (1..month.lengthOfMonth()).associate { day ->
            val date = month.atDay(day)
            date to createItemsForDate(date)
        }
    }

    private fun createItemsForDate(date: LocalDate): List<DailyItem> {
        val bucket = (date.dayOfMonth + date.monthValue) % 5
        if (bucket == 0) return emptyList()

        val items = mutableListOf<DailyItem>()
        items += DailyItem(
            id = "meal-${date}-0",
            title = "Meal plan",
            type = DailyItemType.Meal,
            thumbnailResId = R.drawable.muscles
        )
        if (bucket % 2 == 0) {
            items += DailyItem(
                id = "activity-${date}-0",
                title = "Cardio session",
                type = DailyItemType.Activity,
                thumbnailResId = R.drawable.muscles
            )
        }
        if (bucket >= 3) {
            items += DailyItem(
                id = "meal-${date}-1",
                title = "Snack log",
                type = DailyItemType.Meal,
                thumbnailResId = R.drawable.muscles
            )
        }
        return items
    }
}

private object CalendarPaging {
    const val monthBatchSize: Int = 3
}
