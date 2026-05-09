package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryBookingManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiEffect
import javax.inject.Inject

class RunExerciseLibraryBookingConfirmationUseCase @Inject constructor(
    private val observeExerciseLibraryUiStateUseCase: ObserveExerciseLibraryUiStateUseCase,
    private val observeExerciseCatalogUseCase: ObserveExerciseCatalogUseCase,
    private val observeSessionBookingEditorUiStateUseCase: ObserveSessionBookingEditorUiStateUseCase,
    private val bookingManager: ExerciseLibraryBookingManager,
) {
    suspend operator fun invoke(
        emitUiEffect: suspend (ExerciseLibraryUiEffect) -> Unit,
    ) {
        bookingManager.runBookingConfirmation(
            getMergedState = { observeExerciseLibraryUiStateUseCase.mergedUiState() },
            getSessionBookingInput = { bookingManager.sessionBookingInput.value },
            getCatalogExercisesById = { observeExerciseCatalogUseCase.catalogExercisesById.value },
            getGymLocations = { observeSessionBookingEditorUiStateUseCase.gymLocationsSnapshot() },
            emitUiEffect = emitUiEffect,
        )
    }
}
