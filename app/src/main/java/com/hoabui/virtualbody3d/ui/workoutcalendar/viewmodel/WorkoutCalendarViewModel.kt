package com.hoabui.virtualbody3d.ui.workoutcalendar.viewmodel

import androidx.lifecycle.viewModelScope
import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarDaySummary
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarExerciseLine
import com.hoabui.virtualbody3d.domain.usecase.ObserveWorkoutCalendarDayDetailUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveWorkoutCalendarMonthSummariesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

data class WorkoutCalendarContent(
    val visibleYearMonth: YearMonth,
    val selectedDate: LocalDate,
    val summariesByEpochDay: Map<Long, WorkoutCalendarDaySummary>,
    val dayLines: List<WorkoutCalendarExerciseLine>,
)

/** Reserved for one-shot UI events; none yet. */
sealed interface WorkoutCalendarEvent {
    data object None : WorkoutCalendarEvent
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class WorkoutCalendarViewModel @Inject constructor(
    private val observeMonthSummaries: ObserveWorkoutCalendarMonthSummariesUseCase,
    private val observeDayDetail: ObserveWorkoutCalendarDayDetailUseCase,
) : UiStateViewModel<WorkoutCalendarContent, WorkoutCalendarEvent>() {

    private val zoneId: ZoneId = ZoneId.systemDefault()
    private val today: LocalDate = LocalDate.now(zoneId)
    private val _visibleMonth = MutableStateFlow(YearMonth.from(today))
    private val _selectedDate = MutableStateFlow(today)

    val visibleMonthState = _visibleMonth.asStateFlow()
    val selectedDateState = _selectedDate.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                _visibleMonth.flatMapLatest { ym -> observeMonthSummaries(ym, zoneId) },
                _selectedDate.flatMapLatest { d -> observeDayDetail(d, zoneId) },
                _visibleMonth,
                _selectedDate,
            ) { summaries, lines, vm, sd ->
                WorkoutCalendarContent(
                    visibleYearMonth = vm,
                    selectedDate = sd,
                    summariesByEpochDay = summaries,
                    dayLines = lines,
                )
            }.collect { content ->
                setSuccess(content)
            }
        }
    }

    fun onVisibleMonthChanged(ym: YearMonth) {
        val dom = _selectedDate.value.dayOfMonth
        val lastDay = ym.lengthOfMonth()
        val clampedDom = dom.coerceAtMost(lastDay)
        val nextSelection = ym.atDay(clampedDom)
        _visibleMonth.value = ym
        _selectedDate.value = nextSelection
    }

    fun onDaySelected(day: LocalDate) {
        _selectedDate.value = day
        val monthOfDay = YearMonth.from(day)
        if (monthOfDay != _visibleMonth.value) {
            _visibleMonth.value = monthOfDay
        }
    }
}
