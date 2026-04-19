package com.hoabui.virtualbody3d.ui.exerciselibrary.state.reducer

import com.hoabui.virtualbody3d.domain.usecase.CommitLibrarySessionBookingResult
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.CommitLibrarySessionBookingSuccessUiMapper
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.toExerciseDraftForSelectionBarEdit
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.withCartSnapshot
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.LibraryCartState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SessionBookingSheetState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.mvi.ExerciseLibraryIntent
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.mvi.ExerciseLibraryUpdate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.collections.immutable.toImmutableMap
import javax.inject.Inject

class ExerciseLibraryReducer @Inject constructor(
    private val commitSuccessUiMapper: CommitLibrarySessionBookingSuccessUiMapper,
) {

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
            is ExerciseLibraryUpdate.WeeklyHeatmapLoaded ->
                state.copy(weeklyHeatmap = update.state)
            is ExerciseLibraryUpdate.SessionBookingOpened ->
                state.copy(sessionBooking = state.sessionBooking.copy(input = update.input))
            is ExerciseLibraryUpdate.SessionBookingPruned ->
                state.copy(sessionBooking = state.sessionBooking.copy(input = update.input))
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
                            pendingLongSessionWarning = false,
                            showSlotConflict = false,
                        ),
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
                    detailExerciseId = null,
                    addExerciseSuccess = null,
                    isSelectionBarEditMode = false,
                    editingScheduleRowId = null,
                    selectionBarEditBaselineCart = null,
                    isIsolatedScheduleRowSelectionEdit = false,
                    selectionBarEditMeasurementMode = null,
                ),
                sessionBooking = SessionBookingSheetState(),
            )
            is ExerciseLibraryUpdate.SelectionBarEditBegan -> state.copy(
                chrome = state.chrome.copy(
                    isSelectionBarEditMode = true,
                    editingScheduleRowId = update.scheduleRowId,
                    selectionBarEditBaselineCart = state.cart,
                    isIsolatedScheduleRowSelectionEdit = false,
                    selectionBarEditMeasurementMode = null,
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
                        isSelectionBarEditMode = true,
                        editingScheduleRowId = update.scheduleRowId,
                        selectionBarEditBaselineCart = cart,
                        isIsolatedScheduleRowSelectionEdit = true,
                        selectionBarEditMeasurementMode = update.schedule.measurementMode,
                    ),
                )
            }
            ExerciseLibraryUpdate.SelectionBarEditCancelled -> {
                val clearedChrome = state.chrome.copy(
                    isSelectionBarEditMode = false,
                    editingScheduleRowId = null,
                    selectionBarEditBaselineCart = null,
                    isIsolatedScheduleRowSelectionEdit = false,
                    selectionBarEditMeasurementMode = null,
                )
                if (state.chrome.isIsolatedScheduleRowSelectionEdit) {
                    state.copy(
                        cart = emptyLibraryCart(),
                        chrome = clearedChrome,
                    )
                } else {
                    val baseline = state.chrome.selectionBarEditBaselineCart ?: return@reduce state.copy(
                        chrome = clearedChrome,
                    )
                    state.copy(
                        cart = baseline.copy(isCartExpanded = false),
                        chrome = clearedChrome,
                    )
                }
            }
            ExerciseLibraryUpdate.SelectionBarEditFinished -> {
                val clearedChrome = state.chrome.copy(
                    isSelectionBarEditMode = false,
                    editingScheduleRowId = null,
                    selectionBarEditBaselineCart = null,
                    isIsolatedScheduleRowSelectionEdit = false,
                    selectionBarEditMeasurementMode = null,
                )
                if (state.chrome.isIsolatedScheduleRowSelectionEdit) {
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
                val inp = state.sessionBooking.input ?: return state
                state.copy(
                    sessionBooking = state.sessionBooking.copy(
                        input = inp.copy(pendingLongSessionWarning = true),
                    ),
                )
            }
            ExerciseLibraryUpdate.BookingConfirmation.PendingCommit -> {
                val inp = state.sessionBooking.input ?: return state
                state.copy(
                    sessionBooking = state.sessionBooking.copy(
                        input = inp.copy(
                            isConfirming = true,
                            showSlotConflict = false,
                            pendingLongSessionWarning = false,
                        ),
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
                                showSlotConflict = true,
                            ),
                        ),
                    )
                }
                CommitLibrarySessionBookingResult.InvalidDraft -> {
                    val inp = state.sessionBooking.input ?: return state
                    state.copy(
                        sessionBooking = state.sessionBooking.copy(
                            input = inp.copy(isConfirming = false),
                        ),
                    )
                }
                is CommitLibrarySessionBookingResult.Success -> {
                    val summary = commitSuccessUiMapper.toAddExerciseSuccessSummary(result)
                    state.copy(
                        cart = state.cart.copy(
                            itemDrafts = persistentMapOf(),
                            draftOrder = persistentListOf(),
                            activeExerciseId = null,
                            isCartExpanded = false,
                        ),
                        sessionBooking = SessionBookingSheetState(),
                        chrome = state.chrome.copy(
                            addExerciseSuccess = summary,
                            isSelectionBarEditMode = false,
                            editingScheduleRowId = null,
                            selectionBarEditBaselineCart = null,
                            isIsolatedScheduleRowSelectionEdit = false,
                            selectionBarEditMeasurementMode = null,
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
            is ExerciseLibraryIntent.SetSearchQuery ->
                state.copy(filters = state.filters.copy(searchQuery = intent.query))

            ExerciseLibraryIntent.DismissSessionBooking ->
                state.copy(sessionBooking = state.sessionBooking.copy(input = null))

            is ExerciseLibraryIntent.BookingDateSelected -> {
                val inp = state.sessionBooking.input ?: return state
                state.copy(
                    sessionBooking = state.sessionBooking.copy(
                        input = inp.copy(
                            selectedDateMillis = intent.dateMillis,
                            showSlotConflict = false,
                        ),
                    ),
                )
            }

            is ExerciseLibraryIntent.BookingLocationSelected -> {
                val inp = state.sessionBooking.input ?: return state
                state.copy(
                    sessionBooking = state.sessionBooking.copy(
                        input = inp.copy(
                            selectedLocationId = intent.locationId,
                            showSlotConflict = false,
                        ),
                    ),
                )
            }

            ExerciseLibraryIntent.BookingClearTimeSelection -> {
                val inp = state.sessionBooking.input ?: return state
                if (inp.selectedSlotStarts.isEmpty()) return state
                state.copy(
                    sessionBooking = state.sessionBooking.copy(
                        input = inp.copy(
                            selectedSlotStarts = persistentSetOf(),
                            longSessionAcknowledged = false,
                            pendingLongSessionWarning = false,
                            showSlotConflict = false,
                        ),
                    ),
                )
            }

            ExerciseLibraryIntent.LongSessionEdit -> {
                val inp = state.sessionBooking.input ?: return state
                state.copy(
                    sessionBooking = state.sessionBooking.copy(
                        input = inp.copy(pendingLongSessionWarning = false),
                    ),
                )
            }

            ExerciseLibraryIntent.LongSessionProceedAnyway -> {
                val inp = state.sessionBooking.input ?: return state
                state.copy(
                    sessionBooking = state.sessionBooking.copy(
                        input = inp.copy(
                            pendingLongSessionWarning = false,
                            longSessionAcknowledged = true,
                        ),
                    ),
                )
            }

            ExerciseLibraryIntent.DismissAddExerciseSuccess ->
                state.copy(chrome = state.chrome.copy(addExerciseSuccess = null))

            is ExerciseLibraryIntent.SelectExerciseForDetail ->
                state.copy(chrome = state.chrome.copy(detailExerciseId = intent.exerciseId))

            ExerciseLibraryIntent.ClearExerciseDetail ->
                state.copy(chrome = state.chrome.copy(detailExerciseId = null))

            ExerciseLibraryIntent.ToggleCartExpanded ->
                state.copy(cart = state.cart.copy(isCartExpanded = !state.cart.isCartExpanded))
        }
    }
}
