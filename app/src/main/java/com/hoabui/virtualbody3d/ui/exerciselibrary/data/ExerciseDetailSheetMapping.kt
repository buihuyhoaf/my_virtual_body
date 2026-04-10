package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import android.content.Context
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.toExerciseLibraryCardImage
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseDetailSheetUiModel

fun Exercise.toExerciseDetailSheetUiModel(context: Context): ExerciseDetailSheetUiModel {
    val regionLabel = context.getString(ExerciseDisplayResources.bodyRegionResId(bodyRegion))
    val equipLabel = context.getString(ExerciseDisplayResources.equipmentResId(equipment))
    return ExerciseDetailSheetUiModel(
        id = id,
        name = name,
        description = description,
        safetyNotes = safetyNotes,
        lastWeightKg = lastWeightKg,
        targetRegionLabel = regionLabel,
        equipmentLabel = equipLabel,
        heroImage = image.toExerciseLibraryCardImage(),
        heroContentDescription = name,
    )
}
