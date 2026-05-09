package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryCartManager
import javax.inject.Inject

class CancelSelectionBarEditUseCase @Inject constructor(
    private val exerciseLibraryCartManager: ExerciseLibraryCartManager,
) {
    operator fun invoke() {
        exerciseLibraryCartManager.cancelSelectionBarEdit()
    }
}
