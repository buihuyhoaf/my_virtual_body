package com.hoabui.virtualbody3d.ui.exerciselibrary.state

import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.AddExerciseSuccessSummary

sealed interface ExerciseLibraryUiEffect {
    data class ShowAddExerciseSuccess(val summary: AddExerciseSuccessSummary) : ExerciseLibraryUiEffect
}
