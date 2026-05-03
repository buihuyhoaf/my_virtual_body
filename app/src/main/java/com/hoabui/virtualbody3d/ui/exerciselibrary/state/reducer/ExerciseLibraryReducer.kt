package com.hoabui.virtualbody3d.ui.exerciselibrary.state.reducer

import com.hoabui.virtualbody3d.domain.usecase.CommitLibrarySessionBookingResult
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.toExerciseDraftForSelectionBarEdit
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.withCartSnapshot
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryChromeMode
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.LibraryCartState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SessionBookingSheetState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SessionBookingWorkflowPhase
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.mvi.ExerciseLibraryIntent
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.mvi.ExerciseLibraryUpdate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentSet
import javax.inject.Inject

class ExerciseLibraryReducer @Inject constructor() {

    fun reduce(
        state: ExerciseLibraryUiState,
        update: ExerciseLibraryUpdate,
    ): ExerciseLibraryUiState {
        return when (update) {
            is ExerciseLibraryUpdate.UserIntent -> reduceUserIntent(state, update.intent)
            is ExerciseLibraryUpdate.CartFromDomain -> state.withCartSnapshot(update.snapshot)
            is ExerciseLibraryUpdate.CatalogLoaded ->
                state.copy(catalog = update.catalog)
            is ExerciseLibraryUpdate.CartDraftUpdated -> {
                if (update.exerciseId !in state.cart.itemDrafts) return state
                state.copy(
                    cart = state.cart.copy(
                        itemDrafts = (state.cart.itemDrafts + (update.exerciseId to update.draft))
                            .toImmutableMap(),
                    ),
                )
            }
            is ExerciseLibraryUpdate.SessionBookingOpened ->
                state.copy(
                    sessionBooking = state.sessionBooking.copy(
                        input = update.input,
                        workflowPhase = SessionBookingWorkflowPhase.Idle,
                    ),
                )
            is ExerciseLibraryUpdate.SlotSelectionResolved -> {
                val inp = state.sessionBooking.input ?: return state
                state.copy(
                    sessionBooking = state.sessionBooking.copy(
                        input = inp.copy(
                            selectedSlotStarts = update.selectedSlotStarts.toPersistentSet(),
                            longSessionAcknowledged = if (update.selectionChanged) {
                                false
                            } else {
                                inp.longSessionAcknowledged
                            },
                        ),
                        workflowPhase = SessionBookingWorkflowPhase.Idle,
                    ),
                )
            }
            is ExerciseLibraryUpdate.BookingConfirmation ->
                reduceBookingConfirmation(state, update)
            ExerciseLibraryUpdate.LibraryCleared -> state.copy(
                cart = state.cart.copy(
                    itemDrafts = persistentMapOf(),
                    draftOrder = persistentListOf(),
                    activeExerciseId = null,
                    isCartExpanded = false,
                ),
                chrome = state.chrome.copy(
                    mode = ExerciseLibraryChromeMode.Idle,
                ),
                sessionBooking = SessionBookingSheetState(),
            )
            is ExerciseLibraryUpdate.SelectionBarEditBegan -> state.copy(
                chrome = state.chrome.copy(
                    mode = ExerciseLibraryChromeMode.EditingScheduleRow(
                        scheduleRowId = update.scheduleRowId,
                        baselineCart = state.cart,
                        isIsolatedScheduleRowSelectionEdit = false,
                        measurementMode = (state.cart.activeExerciseId ?: state.cart.draftOrder.firstOrNull())
                            ?.let { state.libraryList.exerciseMeasurementById[it] }
                            ?: ExerciseMeasurementMode.Strength,
                    ),
                ),
                cart = state.cart.copy(isCartExpanded = true),
            )
            is ExerciseLibraryUpdate.SelectionBarEditFromScheduleRowLoaded -> {
                val exerciseId = update.schedule.exerciseId
                val draft = update.schedule.toExerciseDraftForSelectionBarEdit()
                val cart = LibraryCartState(
                    itemDrafts = persistentMapOf(exerciseId to draft),
                    draftOrder = persistentListOf(exerciseId),
                    activeExerciseId = exerciseId,
                    isCartExpanded = true,
                )
                state.copy(
                    cart = cart,
                    chrome = state.chrome.copy(
                        mode = ExerciseLibraryChromeMode.EditingScheduleRow(
                            scheduleRowId = update.scheduleRowId,
                            baselineCart = cart,
                            isIsolatedScheduleRowSelectionEdit = true,
                            measurementMode = update.schedule.measurementMode,
                        ),
                    ),
                )
            }
            ExerciseLibraryUpdate.SelectionBarEditCancelled -> {
                val mode = state.chrome.mode as? ExerciseLibraryChromeMode.EditingScheduleRow ?: return state
                val clearedChrome = state.chrome.copy(mode = ExerciseLibraryChromeMode.Idle)
                if (mode.isIsolatedScheduleRowSelectionEdit) {
                    state.copy(
                        cart = emptyLibraryCart(),
                        chrome = clearedChrome,
                    )
                } else {
                    state.copy(
                        cart = mode.baselineCart.copy(isCartExpanded = false),
                        chrome = clearedChrome,
                    )
                }
            }
            ExerciseLibraryUpdate.SelectionBarEditFinished -> {
                val mode = state.chrome.mode as? ExerciseLibraryChromeMode.EditingScheduleRow ?: return state
                val clearedChrome = state.chrome.copy(mode = ExerciseLibraryChromeMode.Idle)
                if (mode.isIsolatedScheduleRowSelectionEdit) {
                    state.copy(
                        cart = emptyLibraryCart(),
                        chrome = clearedChrome,
                    )
                } else {
                    state.copy(
                        cart = state.cart.copy(isCartExpanded = false),
                        chrome = clearedChrome,
                    )
                }
            }
        }
    }

    private fun reduceBookingConfirmation(
        state: ExerciseLibraryUiState,
        update: ExerciseLibraryUpdate.BookingConfirmation,
    ): ExerciseLibraryUiState {
        return when (update) {
            ExerciseLibraryUpdate.BookingConfirmation.AwaitingLongSessionAck -> {
                state.copy(
                    sessionBooking = state.sessionBooking.copy(
                        workflowPhase = SessionBookingWorkflowPhase.AwaitingLongSessionAck,
                    ),
                )
            }
            ExerciseLibraryUpdate.BookingConfirmation.PendingCommit -> {
                val inp = state.sessionBooking.input ?: return state
                state.copy(
                    sessionBooking = state.sessionBooking.copy(
                        input = inp.copy(
                            isConfirming = true,
                        ),
                        workflowPhase = SessionBookingWorkflowPhase.Idle,
                    ),
                )
            }
            is ExerciseLibraryUpdate.BookingConfirmation.Completed -> when (val result = update.result) {
                CommitLibrarySessionBookingResult.Conflict -> {
                    val inp = state.sessionBooking.input ?: return state
                    state.copy(
                        sessionBooking = state.sessionBooking.copy(
                            input = inp.copy(
                                isConfirming = false,
                            ),
                            workflowPhase = SessionBookingWorkflowPhase.SlotConflict,
                        ),
                    )
                }
                CommitLibrarySessionBookingResult.InvalidDraft -> {
                    val inp = state.sessionBooking.input ?: return state
                    state.copy(
                        sessionBooking = state.sessionBooking.copy(
                            input = inp.copy(isConfirming = false),
                            workflowPhase = SessionBookingWorkflowPhase.Idle,
                        ),
                    )
                }
                is CommitLibrarySessionBookingResult.Success -> {
                    state.copy(
                        cart = state.cart.copy(
                            itemDrafts = persistentMapOf(),
                            draftOrder = persistentListOf(),
                            activeExerciseId = null,
                            isCartExpanded = false,
                        ),
                        sessionBooking = SessionBookingSheetState(),
                        chrome = state.chrome.copy(
                            mode = ExerciseLibraryChromeMode.Idle,
                        ),
                    )
                }
            }
        }
    }

    private fun emptyLibraryCart(): LibraryCartState = LibraryCartState(
        itemDrafts = persistentMapOf(),
        draftOrder = persistentListOf(),
        activeExerciseId = null,
        isCartExpanded = false,
    )

    private fun reduceUserIntent(
        state: ExerciseLibraryUiState,
        intent: ExerciseLibraryIntent,
    ): ExerciseLibraryUiState {
        return when (intent) {
            ExerciseLibraryIntent.OpenSessionBooking -> state
            is ExerciseLibraryIntent.SetSearchQuery ->
                state.copy(filters = state.filters.copy(searchQuery = intent.query))
            is ExerciseLibraryIntent.SetInitialExerciseCategoryFilter ->
                state.copy(
                    filters = state.filters.copy(
                        selectedExerciseCategory = intent.category,
                        selectedBodyRegions = null,
                    ),
                )
            is ExerciseLibraryIntent.SetInitialBodyRegionFilter ->
                state.copy(
                    filters = state.filters.copy(
                        selectedExerciseCategory = null,
                        selectedBodyRegions = intent.regions,
                    ),
                )
            is ExerciseLibraryIntent.CardSelectionToggled -> state
            is ExerciseLibraryIntent.SelectCartItem -> state
            is ExerciseLibraryIntent.RemoveCartItem -> state
            ExerciseLibraryIntent.ClearCart -> state
            is ExerciseLibraryIntent.StepCartField -> state
            is ExerciseLibraryIntent.SetCartFieldManual -> state

            ExerciseLibraryIntent.DismissSessionBooking ->
                state.copy(
                    sessionBooking = state.sessionBooking.copy(
                        input = null,
                        workflowPhase = SessionBookingWorkflowPhase.Idle,
                    ),
                )

            is ExerciseLibraryIntent.BookingDateSelected -> {
                val inp = state.sessionBooking.input ?: return state
                state.copy(
                    sessionBooking = state.sessionBooking.copy(
                        input = inp.copy(
                            selectedDateMillis = intent.dateMillis,
                        ),
                        workflowPhase = SessionBookingWorkflowPhase.Idle,
                    ),
                )
            }

            is ExerciseLibraryIntent.BookingLocationSelected -> {
                val inp = state.sessionBooking.input ?: return state
                state.copy(
                    sessionBooking = state.sessionBooking.copy(
                        input = inp.copy(
                            selectedLocationId = intent.locationId,
                        ),
                        workflowPhase = SessionBookingWorkflowPhase.Idle,
                    ),
                )
            }
            is ExerciseLibraryIntent.BookingSlotToggled -> state

            ExerciseLibraryIntent.BookingClearTimeSelection -> {
                val inp = state.sessionBooking.input ?: return state
                if (inp.selectedSlotStarts.isEmpty()) return state
                state.copy(
                    sessionBooking = state.sessionBooking.copy(
                        input = inp.copy(
                            selectedSlotStarts = persistentSetOf(),
                            longSessionAcknowledged = false,
                        ),
                        workflowPhase = SessionBookingWorkflowPhase.Idle,
                    ),
                )
            }
            ExerciseLibraryIntent.ConfirmSessionBooking -> state

            ExerciseLibraryIntent.LongSessionEdit -> {
                val inp = state.sessionBooking.input ?: return state
                state.copy(
                    sessionBooking = state.sessionBooking.copy(
                        input = inp,
                        workflowPhase = SessionBookingWorkflowPhase.Idle,
                    ),
                )
            }

            ExerciseLibraryIntent.LongSessionProceedAnyway -> {
                val inp = state.sessionBooking.input ?: return state
                state.copy(
                    sessionBooking = state.sessionBooking.copy(
                        input = inp.copy(
                            longSessionAcknowledged = true,
                        ),
                        workflowPhase = SessionBookingWorkflowPhase.Idle,
                    ),
                )
            }

            ExerciseLibraryIntent.DismissAddExerciseSuccess -> state

            ExerciseLibraryIntent.ToggleCartExpanded ->
                state.copy(cart = state.cart.copy(isCartExpanded = !state.cart.isCartExpanded))
            ExerciseLibraryIntent.ConfirmSelectionBarEdit -> state
            ExerciseLibraryIntent.CancelSelectionBarEdit -> state
            is ExerciseLibraryIntent.StartSelectionBarEditFromScheduleRow -> state
        }
    }
}
