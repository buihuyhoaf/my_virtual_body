package com.hoabui.virtualbody3d.ui.exerciselibrary.state.reducer

import com.hoabui.virtualbody3d.domain.usecase.CommitLibrarySessionBookingResult
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.Muscle
import com.hoabui.virtualbody3d.domain.model.exercise.MuscleDictionary
import com.hoabui.virtualbody3d.domain.model.exercise.RegionGroup
import com.hoabui.virtualbody3d.domain.model.exercise.RegionBodyMuscleSelectionMap
import com.hoabui.virtualbody3d.domain.model.exercise.RegionBody
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
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.collections.immutable.toImmutableMap
import javax.inject.Inject

class ExerciseLibraryReducer @Inject constructor(
    private val muscleDictionary: MuscleDictionary,
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
            is ExerciseLibraryIntent.ExerciseClicked -> state
            is ExerciseLibraryIntent.LibraryListToggle -> state
            is ExerciseLibraryIntent.DetailAddToCart -> state
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

            is ExerciseLibraryIntent.SelectExerciseForDetail ->
                state.copy(
                    chrome = state.chrome.copy(
                        mode = ExerciseLibraryChromeMode.DetailOpen(intent.exerciseId),
                    ),
                )

            ExerciseLibraryIntent.ClearExerciseDetail -> {
                if (state.chrome.mode is ExerciseLibraryChromeMode.DetailOpen) {
                    state.copy(chrome = state.chrome.copy(mode = ExerciseLibraryChromeMode.Idle))
                } else {
                    state
                }
            }

            ExerciseLibraryIntent.ToggleCartExpanded ->
                state.copy(cart = state.cart.copy(isCartExpanded = !state.cart.isCartExpanded))
            is ExerciseLibraryIntent.FocusStripQuadrantTapped -> state
            ExerciseLibraryIntent.ConfirmSelectionBarEdit -> state
            ExerciseLibraryIntent.CancelSelectionBarEdit -> state
            is ExerciseLibraryIntent.StartSelectionBarEditFromScheduleRow -> state
            is ExerciseLibraryIntent.ToggleFocusStripRegionGroup ->
                state.copy(
                    focusStripClickSelection = toggleRegionGroupSelection(
                        current = state.focusStripClickSelection,
                        regionGroup = intent.regionGroup,
                        muscleDictionary = muscleDictionary,
                    ),
                )
            is ExerciseLibraryIntent.ToggleFocusStripRegionBody ->
                state.copy(
                    focusStripClickSelection = toggleRegionBodySelection(
                        current = state.focusStripClickSelection,
                        regionGroup = intent.regionGroup,
                        regionBody = intent.regionBody,
                        muscleDictionary = muscleDictionary,
                    ),
                )
            is ExerciseLibraryIntent.ToggleFocusStripMuscle ->
                state.copy(
                    focusStripClickSelection = toggleMuscleSelection(
                        current = state.focusStripClickSelection,
                        regionGroup = intent.regionGroup,
                        regionBody = intent.regionBody,
                        muscle = intent.muscle,
                        muscleDictionary = muscleDictionary,
                    ),
                )
            ExerciseLibraryIntent.ClearFocusStripSelection ->
                state.copy(focusStripClickSelection = emptyMap())
        }
    }
}

private fun toggleRegionGroupSelection(
    current: RegionBodyMuscleSelectionMap,
    regionGroup: RegionGroup,
    muscleDictionary: MuscleDictionary,
): RegionBodyMuscleSelectionMap {
    val existingBodies = current[regionGroup].orEmpty()
    val allBodies = muscleDictionary.allBodies(regionGroup)
    if (allBodies.isEmpty()) return current
    val fullySelected = allBodies.all { body ->
        val selected = existingBodies[body].orEmpty()
        val required = muscleDictionary.allMuscles(regionGroup, body)
        required.isNotEmpty() && required.all { it in selected }
    }
    val next = current.toMutableMap()
    if (fullySelected) {
        next.remove(regionGroup)
    } else {
        next[regionGroup] = allBodies.associateWith { body ->
            muscleDictionary.allMuscles(regionGroup, body)
        }
    }
    return normalizeSelectionMap(next, muscleDictionary)
}

private fun toggleRegionBodySelection(
    current: RegionBodyMuscleSelectionMap,
    regionGroup: RegionGroup,
    regionBody: RegionBody,
    muscleDictionary: MuscleDictionary,
): RegionBodyMuscleSelectionMap {
    val expected = muscleDictionary.allMuscles(regionGroup, regionBody)
    if (expected.isEmpty()) return current
    val next = current.toMutableMap()
    val bodyMap = next[regionGroup].orEmpty().toMutableMap()
    val isFull = expected.all { it in bodyMap[regionBody].orEmpty() }
    if (isFull) {
        bodyMap.remove(regionBody)
    } else {
        bodyMap[regionBody] = expected
    }
    if (bodyMap.isEmpty()) {
        next.remove(regionGroup)
    } else {
        next[regionGroup] = bodyMap
    }
    return normalizeSelectionMap(next, muscleDictionary)
}

private fun toggleMuscleSelection(
    current: RegionBodyMuscleSelectionMap,
    regionGroup: RegionGroup,
    regionBody: RegionBody,
    muscle: Muscle,
    muscleDictionary: MuscleDictionary,
): RegionBodyMuscleSelectionMap {
    val expected = muscleDictionary.allMuscles(regionGroup, regionBody)
    if (muscle !in expected) {
        return current
    }
    val next = current.toMutableMap()
    val bodyMap = next[regionGroup].orEmpty().toMutableMap()
    val selected = bodyMap[regionBody].orEmpty().toMutableSet()
    if (!selected.add(muscle)) {
        selected.remove(muscle)
    }
    if (selected.isEmpty()) {
        bodyMap.remove(regionBody)
    } else {
        bodyMap[regionBody] = selected
    }
    if (bodyMap.isEmpty()) {
        next.remove(regionGroup)
    } else {
        next[regionGroup] = bodyMap
    }
    return normalizeSelectionMap(next, muscleDictionary)
}

private fun normalizeSelectionMap(
    selectionMap: RegionBodyMuscleSelectionMap,
    muscleDictionary: MuscleDictionary,
): RegionBodyMuscleSelectionMap {
    val normalized = linkedMapOf<RegionGroup, Map<RegionBody, Set<Muscle>>>()
    selectionMap.forEach { (group, bodies) ->
        val allowedBodies = muscleDictionary.allBodies(group)
        if (allowedBodies.isEmpty()) return@forEach
        val normalizedBodies = linkedMapOf<RegionBody, Set<Muscle>>()
        bodies.forEach { (body, selectedMuscles) ->
            if (body !in allowedBodies) return@forEach
            val allowedMuscles = muscleDictionary.allMuscles(group, body)
            val filtered = selectedMuscles.filterTo(linkedSetOf()) { it in allowedMuscles }
            if (filtered.isNotEmpty()) {
                normalizedBodies[body] = filtered
            }
        }
        if (normalizedBodies.isNotEmpty()) {
            normalized[group] = normalizedBodies
        }
    }
    return normalized
}
