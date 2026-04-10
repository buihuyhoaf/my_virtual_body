package com.hoabui.virtualbody3d.ui.exerciselibrary.state.mvi

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseLibraryCartSnapshot
import com.hoabui.virtualbody3d.domain.usecase.CommitLibrarySessionBookingResult
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryCatalogState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.LibraryMonthlySummaryState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SessionBookingInput
import java.time.LocalTime

/**
 * All state transitions for the exercise library screen: user intents, domain cart snapshots,
 * booking workflow outcomes, and reactive booking input sync.
 */
sealed interface ExerciseLibraryUpdate {

    data class UserIntent(val intent: ExerciseLibraryIntent) : ExerciseLibraryUpdate

    data object LibraryCleared : ExerciseLibraryUpdate

    data class CartFromDomain(val snapshot: ExerciseLibraryCartSnapshot) : ExerciseLibraryUpdate

    data class CatalogLoaded(val catalog: ExerciseLibraryCatalogState) : ExerciseLibraryUpdate

    data class MonthlySummaryLoaded(val summary: LibraryMonthlySummaryState) : ExerciseLibraryUpdate

    data class SessionBookingOpened(val input: SessionBookingInput) : ExerciseLibraryUpdate

    data class SessionBookingPruned(val input: SessionBookingInput) : ExerciseLibraryUpdate

    data class SlotSelectionResolved(
        val selectedSlotStarts: Set<LocalTime>,
        val selectionChanged: Boolean,
    ) : ExerciseLibraryUpdate

    sealed interface BookingConfirmation : ExerciseLibraryUpdate {
        data object AwaitingLongSessionAck : BookingConfirmation
        data object PendingCommit : BookingConfirmation
        data class Completed(val result: CommitLibrarySessionBookingResult) : BookingConfirmation
    }
}
