package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseLibraryCartSnapshot
import com.hoabui.virtualbody3d.domain.model.exercise.LibraryCartDraft
import com.hoabui.virtualbody3d.domain.model.exercise.LibraryExerciseLineDraft
import com.hoabui.virtualbody3d.domain.model.exercise.PendingSessionBooking
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SessionBookingInput
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap

fun ExerciseLibraryUiState.toCartSnapshot(): ExerciseLibraryCartSnapshot =
    ExerciseLibraryCartSnapshot(
        itemDrafts = cart.itemDrafts.mapValues { LibraryExerciseLineDraft(it.value.sets, it.value.reps) },
        draftOrder = cart.draftOrder.toList(),
        activeExerciseId = cart.activeExerciseId,
    )

fun ExerciseLibraryUiState.withCartSnapshot(snapshot: ExerciseLibraryCartSnapshot): ExerciseLibraryUiState =
    copy(
        cart = cart.copy(
            itemDrafts = snapshot.itemDrafts
                .mapValues { ExerciseDraft(sets = it.value.sets, reps = it.value.reps) }
                .toImmutableMap(),
            draftOrder = snapshot.draftOrder.toImmutableList(),
            activeExerciseId = snapshot.activeExerciseId,
        ),
    )

fun ExerciseLibraryUiState.toLibraryCartDraft(): LibraryCartDraft =
    LibraryCartDraft(
        draftOrder = cart.draftOrder,
        itemDrafts = cart.itemDrafts.mapValues { (_, d) ->
            LibraryExerciseLineDraft(sets = d.sets, reps = d.reps)
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
