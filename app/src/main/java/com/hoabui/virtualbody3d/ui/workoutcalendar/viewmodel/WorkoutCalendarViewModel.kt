package com.hoabui.virtualbody3d.ui.workoutcalendar.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.core.utils.Constants
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCaloriesVisualLevel
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarDaySummary
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarSessionBlock
import com.hoabui.virtualbody3d.domain.model.calendar.caloriesToVisualLevel
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutScheduleDeleteResult
import com.hoabui.virtualbody3d.domain.usecase.DeleteWorkoutScheduleUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetWorkoutDetailsUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveWorkoutCalendarMonthSummariesUseCase
import com.hoabui.virtualbody3d.domain.usecase.RestoreWorkoutScheduleDeleteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.roundToInt
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
    /** Session blocks grouped by sessionId for the selected day. */
    val sessionBlocks: List<WorkoutCalendarSessionBlock>,
    val dailyTotalCaloriesKcal: Int,
    val dailyCaloriesVisualLevel: WorkoutCaloriesVisualLevel,
)

data class WorkoutCalendarDeleteDialogState(
    val rowId: Long,
    val exerciseName: String,
)

sealed interface WorkoutCalendarEvent {
    data object None : WorkoutCalendarEvent

    /** Show snackbar with undo; strings come from UI [stringResource]. */
    data object ScheduleDeletedShowUndoSnackbar : WorkoutCalendarEvent

    /** Row was already gone or could not be removed; UI shows a localized short message. */
    data object DeleteScheduleFailed : WorkoutCalendarEvent

    data class TransientMessage(val message: String) : WorkoutCalendarEvent
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class WorkoutCalendarViewModel @Inject constructor(
    private val observeMonthSummaries: ObserveWorkoutCalendarMonthSummariesUseCase,
    private val getWorkoutDetails: GetWorkoutDetailsUseCase,
    private val deleteWorkoutSchedule: DeleteWorkoutScheduleUseCase,
    private val restoreWorkoutScheduleDelete: RestoreWorkoutScheduleDeleteUseCase,
    private val sharedPreferences: SharedPreferences,
) : UiStateViewModel<WorkoutCalendarContent, WorkoutCalendarEvent>() {

    private val today: LocalDate = LocalDate.now()
    private val _visibleMonth = MutableStateFlow(YearMonth.from(today))
    private val _selectedDate = MutableStateFlow(today)

    private val _openSwipeRowId = MutableStateFlow<Long?>(null)
    val openSwipeRowId = _openSwipeRowId.asStateFlow()

    private val _deleteDialog = MutableStateFlow<WorkoutCalendarDeleteDialogState?>(null)
    val deleteDialog = _deleteDialog.asStateFlow()

    private val _swipeHintSeen = MutableStateFlow(
        sharedPreferences.getBoolean(Constants.PREF_WORKOUT_CALENDAR_SWIPE_HINT_SEEN, false),
    )
    val swipeHintSeen = _swipeHintSeen.asStateFlow()

    private val _pendingSwipeCloseRowId = MutableStateFlow<Long?>(null)
    val pendingSwipeCloseRowId = _pendingSwipeCloseRowId.asStateFlow()

    private var pendingUndoDelete: WorkoutScheduleDeleteResult? = null

    val visibleMonthState = _visibleMonth.asStateFlow()
    val selectedDateState = _selectedDate.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                _visibleMonth.flatMapLatest { ym -> observeMonthSummaries(ym) },
                _selectedDate.flatMapLatest { d -> getWorkoutDetails(d) },
                _visibleMonth,
                _selectedDate,
            ) { summaries, sessionBlocks, vm, sd ->
                val dailyCaloriesKcal = sessionBlocks
                    .sumOf { it.totalCaloriesKcal.toDouble() }
                    .roundToInt()
                WorkoutCalendarContent(
                    visibleYearMonth = vm,
                    selectedDate = sd,
                    summariesByEpochDay = summaries,
                    sessionBlocks = sessionBlocks,
                    dailyTotalCaloriesKcal = dailyCaloriesKcal,
                    dailyCaloriesVisualLevel = caloriesToVisualLevel(dailyCaloriesKcal.toFloat()),
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

    fun onSwipeRowOpened(rowId: Long) {
        val previous = _openSwipeRowId.value
        if (previous != null && previous != rowId) {
            _pendingSwipeCloseRowId.value = previous
        }
        _openSwipeRowId.value = rowId
    }

    fun onSwipeRowSettledClosed(rowId: Long) {
        if (_openSwipeRowId.value == rowId) {
            _openSwipeRowId.value = null
        }
    }

    fun consumePendingSwipeCloseRow(rowId: Long) {
        if (_pendingSwipeCloseRowId.value == rowId) {
            _pendingSwipeCloseRowId.value = null
        }
    }

    fun onDeleteAffordanceClicked(rowId: Long, exerciseName: String) {
        _deleteDialog.value = WorkoutCalendarDeleteDialogState(rowId = rowId, exerciseName = exerciseName)
    }

    fun onEditAffordanceClicked(rowId: Long) {
        _pendingSwipeCloseRowId.value = rowId
    }

    fun onDeleteDialogDismiss() {
        val rowId = _deleteDialog.value?.rowId
        _deleteDialog.value = null
        if (rowId != null) {
            _pendingSwipeCloseRowId.value = rowId
        }
    }

    fun onDeleteConfirmed() {
        val state = _deleteDialog.value ?: return
        val rowId = state.rowId
        _deleteDialog.value = null
        _openSwipeRowId.value = null
        launchSafely {
            val deleted = deleteWorkoutSchedule(rowId)
            if (deleted != null) {
                pendingUndoDelete = deleted
                sendEvent(WorkoutCalendarEvent.ScheduleDeletedShowUndoSnackbar)
            } else {
                sendEvent(WorkoutCalendarEvent.DeleteScheduleFailed)
            }
        }
    }

    fun undoLastDelete() {
        val snapshot = pendingUndoDelete ?: return
        pendingUndoDelete = null
        launchSafely {
            restoreWorkoutScheduleDelete(snapshot)
        }
    }

    fun clearPendingUndoWithoutRestore() {
        pendingUndoDelete = null
    }

    fun markSwipeHintSeen() {
        sharedPreferences.edit().putBoolean(Constants.PREF_WORKOUT_CALENDAR_SWIPE_HINT_SEEN, true).apply()
        _swipeHintSeen.value = true
    }

    override fun onError(throwable: Throwable) {
        sendEvent(
            WorkoutCalendarEvent.TransientMessage(
                throwable.message ?: "Unknown error",
            ),
        )
    }
}
