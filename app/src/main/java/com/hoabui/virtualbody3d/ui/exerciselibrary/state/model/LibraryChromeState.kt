package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable
@Immutable
data class LibraryChromeState(
    val mode: ExerciseLibraryChromeMode = ExerciseLibraryChromeMode.Idle,
)
