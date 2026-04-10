package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable

/** Per-exercise manual entry while building the library cart ([sets]/[reps] as raw text for empty-friendly UI). */
@Immutable
data class ExerciseDraft(
    val sets: String = "",
    val reps: String = "",
)
