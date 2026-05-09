package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.CartSetField
import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryCartManager
import javax.inject.Inject

class StepCartFieldUseCase @Inject constructor(
    private val exerciseLibraryCartManager: ExerciseLibraryCartManager
) {
    operator fun invoke(exerciseId: String, setIndex: Int, field: CartSetField, delta: Int) {
        exerciseLibraryCartManager.stepCartField(exerciseId, setIndex, field, delta)
    }
}
