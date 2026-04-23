package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import android.content.Context
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.toExerciseLibraryCardImage
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseDetailSheetUiModel

fun Exercise.toExerciseDetailSheetUiModel(context: Context): ExerciseDetailSheetUiModel {
    val regionLabel = context.getString(ExerciseDisplayResources.bodyRegionResId(bodyRegion))
    val equipLabel = context.getString(ExerciseDisplayResources.equipmentResId(equipment))
    val heroImage = image.toExerciseLibraryCardImage()
    val heroContentDescription = when (image) {
        is ImageSource.LocalResource ->
            context.getString(R.string.exercise_detail_hero_animation_cd, name)
        else -> name
    }
    return ExerciseDetailSheetUiModel(
        id = id,
        name = name,
        description = description,
        safetyNotes = safetyNotes,
        lastWeightKg = lastWeightKg,
        targetRegionLabel = regionLabel,
        equipmentLabel = equipLabel,
        heroImage = heroImage,
        heroContentDescription = heroContentDescription,
    )
}
