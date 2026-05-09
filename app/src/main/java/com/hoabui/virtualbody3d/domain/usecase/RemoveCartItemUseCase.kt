package com.hoabui.virtualbody3d.domain.usecase

import javax.inject.Inject

class RemoveCartItemUseCase @Inject constructor(
    private val exerciseLibraryCartManager: com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryCartManager
) {
    operator fun invoke(uiState: com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState, exerciseId: String) {
        exerciseLibraryCartManager.removeCartItem(uiState, exerciseId)
    }
}
