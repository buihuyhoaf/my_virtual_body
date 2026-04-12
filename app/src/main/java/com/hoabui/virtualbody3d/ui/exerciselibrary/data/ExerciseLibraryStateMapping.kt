package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseLibraryCartSnapshot
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.LibraryCartDraft
import com.hoabui.virtualbody3d.domain.model.exercise.LibraryExerciseLineDraft
import com.hoabui.virtualbody3d.domain.model.exercise.PendingSessionBooking
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SessionBookingInput
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SetRowDraft
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap

fun ExerciseLibraryUiState.toCartSnapshot(): ExerciseLibraryCartSnapshot =
    ExerciseLibraryCartSnapshot(
        itemDrafts = cart.itemDrafts.mapValues { (id, draft) ->
            val mode = libraryList.exerciseMeasurementById[id] ?: ExerciseMeasurementMode.Strength
            val row = draft.setRows.firstOrNull() ?: SetRowDraft()
            when (mode) {
                ExerciseMeasurementMode.Strength -> LibraryExerciseLineDraft(
                    sets = draft.sets,
                    reps = draft.reps,
                )
                ExerciseMeasurementMode.Duration -> LibraryExerciseLineDraft(
                    sets = row.minutes.toString(),
                    reps = row.seconds.toString(),
                )
            }
        },
        draftOrder = cart.draftOrder.toList(),
        activeExerciseId = cart.activeExerciseId,
    )

fun ExerciseLibraryUiState.withCartSnapshot(snapshot: ExerciseLibraryCartSnapshot): ExerciseLibraryUiState =
    copy(
        cart = cart.copy(
            itemDrafts = snapshot.itemDrafts
                .mapValues { (id, lineDraft) ->
                    // Preserve existing draft rows/weights when already present in the cart;
                    // create a fresh single-row draft only when the exercise is newly added.
                    cart.itemDrafts[id] ?: defaultDraftFromLineDraft(lineDraft)
                }
                .toImmutableMap(),
            draftOrder = snapshot.draftOrder.toImmutableList(),
            activeExerciseId = snapshot.activeExerciseId,
        ),
    )

fun ExerciseLibraryUiState.toLibraryCartDraft(): LibraryCartDraft =
    LibraryCartDraft(
        draftOrder = cart.draftOrder,
        itemDrafts = cart.itemDrafts.mapValues { (id, d) ->
            val mode = libraryList.exerciseMeasurementById[id] ?: ExerciseMeasurementMode.Strength
            val row = d.setRows.firstOrNull() ?: SetRowDraft()
            when (mode) {
                ExerciseMeasurementMode.Strength -> LibraryExerciseLineDraft(
                    sets = d.sets,
                    reps = d.reps,
                )
                ExerciseMeasurementMode.Duration -> LibraryExerciseLineDraft(
                    sets = row.minutes.toString(),
                    reps = row.seconds.toString(),
                )
            }
        },
    )

fun SessionBookingInput.toPendingSessionBooking(): PendingSessionBooking =
    PendingSessionBooking(
        selectedDateMillis = selectedDateMillis,
        selectedLocationId = selectedLocationId,
        selectedSlotStarts = selectedSlotStarts.toList(),
        longSessionAcknowledged = longSessionAcknowledged,
        isConfirming = isConfirming,
    )

/**
 * Creates a default [ExerciseDraft] for a newly added exercise from its domain [LibraryExerciseLineDraft].
 * Uses the raw sets/reps strings when non-empty, otherwise falls back to sensible defaults.
 */
private fun defaultDraftFromLineDraft(lineDraft: LibraryExerciseLineDraft): ExerciseDraft {
    val reps = lineDraft.reps.trim().toIntOrNull()?.coerceAtLeast(0) ?: 10
    val minutes = lineDraft.sets.trim().toIntOrNull()?.coerceAtLeast(0) ?: 0
    val seconds = lineDraft.reps.trim().toIntOrNull()?.coerceAtLeast(0) ?: 30
    return ExerciseDraft(
        setRows = listOf(
            SetRowDraft(
                reps = reps,
                weightKg = 0.0,
                minutes = minutes,
                seconds = seconds,
            ),
        ).toImmutableList(),
    )
}
