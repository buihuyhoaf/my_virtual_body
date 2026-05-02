package com.hoabui.virtualbody3d.ui.exerciselibrary.state.mvi

import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.AddExerciseSuccessSummary

sealed interface ExerciseLibraryUiEffect {
    data class ShowAddExerciseSuccess(val summary: AddExerciseSuccessSummary) : ExerciseLibraryUiEffect
}
