package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryBookingManager
import javax.inject.Inject

class OpenSessionBookingUseCase @Inject constructor(
    private val observeExerciseLibraryUiStateUseCase: ObserveExerciseLibraryUiStateUseCase,
    private val observeExerciseCatalogUseCase: ObserveExerciseCatalogUseCase,
    private val exerciseLibraryBookingManager: ExerciseLibraryBookingManager,
) {
    operator fun invoke() {
        exerciseLibraryBookingManager.openSessionBooking(
            observeExerciseLibraryUiStateUseCase.mergedUiState(),
            observeExerciseCatalogUseCase.catalogExercisesById.value,
        )
    }
}
