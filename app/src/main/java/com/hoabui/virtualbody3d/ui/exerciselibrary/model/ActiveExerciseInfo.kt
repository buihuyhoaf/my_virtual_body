package com.hoabui.virtualbody3d.ui.exerciselibrary.model

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseDraft

@Immutable
data class ActiveExerciseInfo(
    val id: String,
    val title: String?,
    val draft: ExerciseDraft?,
    val measurementMode: ExerciseMeasurementMode,
)
