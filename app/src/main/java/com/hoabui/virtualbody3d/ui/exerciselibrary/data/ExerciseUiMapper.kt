package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import com.hoabui.virtualbody3d.domain.model.Exercise
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseUiModel

/**
 * Maps domain [Exercise] to presentation [ExerciseUiModel].
 * Use [ExerciseDisplayResources] in composables to resolve labels from enums.
 */
fun Exercise.toExerciseUiModel(): ExerciseUiModel = ExerciseUiModel(
    id = id,
    name = name,
    imageResId = imageResId,
    difficulty = difficulty,
    bodyRegion = bodyRegion,
    primaryMuscles = primaryMuscles,
    secondaryMuscles = secondaryMuscles,
    equipment = equipment,
    description = description,
    safetyNotes = safetyNotes,
    lastWeightKg = lastWeightKg
)
