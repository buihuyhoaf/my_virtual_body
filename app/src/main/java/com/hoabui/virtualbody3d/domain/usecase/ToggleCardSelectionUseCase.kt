package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryCartManager
import javax.inject.Inject

class ToggleCardSelectionUseCase @Inject constructor(
    private val observeExerciseLibraryUiStateUseCase: ObserveExerciseLibraryUiStateUseCase,
    private val observeExerciseCatalogUseCase: ObserveExerciseCatalogUseCase,
    private val exerciseLibraryCartManager: ExerciseLibraryCartManager,
) {
    operator fun invoke(exerciseId: String) {
        val synthetic = observeExerciseLibraryUiStateUseCase.snapshotForCartActions()
        if (exerciseLibraryCartManager.toggleCardSelection(synthetic, exerciseId)) {
            exerciseLibraryCartManager.prefillFromHistory(
                exerciseId,
                observeExerciseCatalogUseCase.catalogExercisesById.value,
            )
        }
    }
}
