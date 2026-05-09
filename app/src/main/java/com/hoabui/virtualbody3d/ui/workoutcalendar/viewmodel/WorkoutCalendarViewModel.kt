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
import com.hoabui.virtualbody3d.domain.usecase.CancelSelectionBarEditUseCase
import com.hoabui.virtualbody3d.domain.usecase.ConfirmSelectionBarEditUseCase
import com.hoabui.virtualbody3d.domain.usecase.DeleteEditingScheduleRowUseCase
import com.hoabui.virtualbody3d.domain.usecase.DeleteWorkoutScheduleUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetWorkoutDetailsUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveExerciseCatalogUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveExerciseLibraryChromeModeUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveExerciseLibraryUiStateUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveWorkoutCalendarMonthSummariesUseCase
import com.hoabui.virtualbody3d.domain.usecase.RestoreWorkoutScheduleDeleteUseCase
import com.hoabui.virtualbody3d.domain.usecase.SelectCartItemUseCase
import com.hoabui.virtualbody3d.domain.usecase.SetCartFieldManualUseCase
import com.hoabui.virtualbody3d.domain.usecase.StartSelectionBarEditFromScheduleRowUseCase
import com.hoabui.virtualbody3d.domain.usecase.StepCartFieldUseCase
import com.hoabui.virtualbody3d.domain.usecase.ToggleCartExpandedUseCase
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseCardUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.ActiveExerciseInfo
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.CartSetField
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.isCartDraftValidForSessionConfirm
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.SelectionBarExerciseMeasurementKind
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.toSelectionBarExerciseMeasurementKind
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryChromeMode
import com.hoabui.virtualbody3d.ui.workoutcalendar.mapper.EditExerciseBarUiMapper
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.roundToInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.collections.immutable.ImmutableList

data class WorkoutCalendarContent(
    val visibleYearMonth: YearMonth,
    val selectedDate: LocalDate,
    val summariesByEpochDay: Map<Long, WorkoutCalendarDaySummary>,
    /** Session blocks grouped by sessionId for the selected day. */
    val sessionBlocks: List<WorkoutCalendarSessionBlock>,
    val dailyTotalCaloriesKcal: Int,
    val dailyCaloriesVisualLevel: WorkoutCaloriesVisualLevel,
    /** Thumbnails for the inline schedule edit strip (cart draft order). */
    val editBarItems: ImmutableList<GExerciseCardUiModel>,
    val isEditBarVisible: Boolean,
    val editBarActiveExerciseInfo: ActiveExerciseInfo?,
    val editBarIsCartExpanded: Boolean,
    val editBarSaveEnabled: Boolean,
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
    private val observeExerciseCatalogUseCase: ObserveExerciseCatalogUseCase,
    private val observeExerciseLibraryUiStateUseCase: ObserveExerciseLibraryUiStateUseCase,
    private val observeExerciseLibraryChromeModeUseCase: ObserveExerciseLibraryChromeModeUseCase,
    private val editExerciseBarUiMapper: EditExerciseBarUiMapper,
    private val startSelectionBarEditFromScheduleRowUseCase: StartSelectionBarEditFromScheduleRowUseCase,
    private val confirmSelectionBarEditUseCase: ConfirmSelectionBarEditUseCase,
    private val cancelSelectionBarEditUseCase: CancelSelectionBarEditUseCase,
    private val deleteEditingScheduleRowUseCase: DeleteEditingScheduleRowUseCase,
    private val selectCartItemUseCase: SelectCartItemUseCase,
    private val stepCartFieldUseCase: StepCartFieldUseCase,
    private val setCartFieldManualUseCase: SetCartFieldManualUseCase,
    private val toggleCartExpandedUseCase: ToggleCartExpandedUseCase,
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

    private val calendarGridSliceFlow =
        combine(
            _visibleMonth.flatMapLatest { ym -> observeMonthSummaries(ym) },
            _selectedDate.flatMapLatest { d -> getWorkoutDetails(d) },
            _visibleMonth,
            _selectedDate,
        ) { summaries, sessionBlocks, visibleYm, selectedDay ->
            WorkoutCalendarGridSlice(
                summariesByEpochDay = summaries,
                sessionBlocks = sessionBlocks,
                visibleYearMonth = visibleYm,
                selectedDate = selectedDay,
            )
        }


    init {
        observeExerciseCatalogUseCase.startCollection(viewModelScope) { throwable ->
            sendEvent(
                WorkoutCalendarEvent.TransientMessage(
                    throwable.message ?: "Unknown error",
                ),
            )
        }
        val libraryFlow = observeExerciseLibraryUiStateUseCase.observe(viewModelScope)
        val chromeFlow = observeExerciseLibraryChromeModeUseCase.chromeMode
        viewModelScope.launch {
            combine(
                calendarGridSliceFlow,
                libraryFlow,
                chromeFlow,
            ) { grid, libraryState, chrome ->
                val dailyCaloriesKcal = grid.sessionBlocks
                    .sumOf { it.totalCaloriesKcal.toDouble() }
                    .roundToInt()
                val chromeEditing = chrome as? ExerciseLibraryChromeMode.EditingScheduleRow
                val isEditBarVisible = chromeEditing != null
                val editBarItems =
                    if (isEditBarVisible) {
                        editExerciseBarUiMapper.draftThumbnailCards(libraryState)
                    } else {
                        editExerciseBarUiMapper.emptyDraftThumbnails()
                    }
                val measurementFallbackKind =
                    chromeEditing?.measurementModeFallback?.toSelectionBarExerciseMeasurementKind()
                        ?: SelectionBarExerciseMeasurementKind.Strength
                val editBarActiveExerciseInfo =
                    if (isEditBarVisible) {
                        editExerciseBarUiMapper.activeExerciseForEditBar(
                            libraryState,
                            editBarItems,
                            measurementFallbackKind,
                        )
                    } else {
                        null
                    }
                val editBarSaveEnabled =
                    isEditBarVisible &&
                        libraryState.isCartDraftValidForSessionConfirm(
                            selectionBarMeasurementModeFallback = chromeEditing?.measurementModeFallback,
                        )
                val editBarIsCartExpanded =
                    isEditBarVisible && libraryState.isCartExpanded
                WorkoutCalendarContent(
                    visibleYearMonth = grid.visibleYearMonth,
                    selectedDate = grid.selectedDate,
                    summariesByEpochDay = grid.summariesByEpochDay,
                    sessionBlocks = grid.sessionBlocks,
                    dailyTotalCaloriesKcal = dailyCaloriesKcal,
                    dailyCaloriesVisualLevel = caloriesToVisualLevel(dailyCaloriesKcal.toFloat()),
                    editBarItems = editBarItems,
                    isEditBarVisible = isEditBarVisible,
                    editBarActiveExerciseInfo = editBarActiveExerciseInfo,
                    editBarIsCartExpanded = editBarIsCartExpanded,
                    editBarSaveEnabled = editBarSaveEnabled,
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

    fun startSelectionBarEditFromScheduleRow(rowId: Long) {
        launchSafely {
            startSelectionBarEditFromScheduleRowUseCase(rowId)
        }
    }

    fun onEditBarConfirm() {
        launchSafely {
            confirmSelectionBarEditUseCase()
        }
    }

    fun onEditBarDelete() {
        launchSafely {
            val deleted = deleteEditingScheduleRowUseCase()
            if (deleted != null) {
                pendingUndoDelete = deleted
                _openSwipeRowId.value = null
                sendEvent(WorkoutCalendarEvent.ScheduleDeletedShowUndoSnackbar)
            } else {
                sendEvent(WorkoutCalendarEvent.DeleteScheduleFailed)
            }
        }
    }

    fun dismissEditBar() {
        cancelSelectionBarEditUseCase()
    }

    fun selectCartItem(exerciseId: String) {
        selectCartItemUseCase(observeExerciseLibraryUiStateUseCase.snapshotForCartActions(), exerciseId)
    }

    fun stepCartField(exerciseId: String, setIndex: Int, field: CartSetField, delta: Int) {
        stepCartFieldUseCase(exerciseId, setIndex, field, delta)
    }

    fun setCartFieldManual(exerciseId: String, setIndex: Int, field: CartSetField, value: String) {
        setCartFieldManualUseCase(exerciseId, setIndex, field, value)
    }

    fun toggleCartExpandedForSelectionBar() {
        toggleCartExpandedUseCase()
    }

    override fun onError(throwable: Throwable) {
        sendEvent(
            WorkoutCalendarEvent.TransientMessage(
                throwable.message ?: "Unknown error",
            ),
        )
    }
}
