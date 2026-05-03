package com.hoabui.virtualbody3d.ui.exerciselibrary.cart

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode

@Immutable
data class ActiveExerciseInfo(
    val id: String,
    val title: String?,
    val draft: ExerciseDraft?,
    val measurementMode: ExerciseMeasurementMode,
    val estimatedCalories: Float,
)
