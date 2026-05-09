package com.hoabui.virtualbody3d.domain.usecase

import javax.inject.Inject

class SetCartFieldManualUseCase @Inject constructor(
    private val exerciseLibraryCartManager: com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryCartManager
) {
    operator fun invoke(exerciseId: String, setIndex: Int, field: com.hoabui.virtualbody3d.ui.exerciselibrary.cart.CartSetField, value: String) {
        exerciseLibraryCartManager.setCartFieldManual(exerciseId, setIndex, field, value)
    }
}
