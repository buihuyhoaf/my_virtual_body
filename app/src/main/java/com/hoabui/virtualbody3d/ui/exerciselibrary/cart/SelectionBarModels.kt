package com.hoabui.virtualbody3d.ui.exerciselibrary.cart

import androidx.compose.runtime.Immutable

@Immutable
data class ActiveExerciseInfo(
    val id: String,
    val title: String?,
    val draft: ExerciseDraft?,
    val measurementKind: SelectionBarExerciseMeasurementKind,
    val estimatedCalories: Float,
)
