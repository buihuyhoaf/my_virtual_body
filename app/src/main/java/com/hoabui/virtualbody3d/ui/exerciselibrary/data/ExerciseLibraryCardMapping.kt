package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import android.content.Context
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.toExerciseLibraryCardImage
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryCatalogEntryUiModel
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseCardUiModel

/**
 * Maps domain [Exercise] to [GExerciseCardUiModel] for the exercise library list, resolving
 * strings via [Context]. Image uses [com.hoabui.virtualbody3d.ui.exerciselibrary.model.ExerciseLibraryCardImage]
 * for stable composition; resolve to Coil in the organism with [ResourceProvider].
 */
fun Exercise.toLibraryCardUiModel(
    context: Context,
    cartExerciseIds: Set<String>,
    activeExerciseId: String?,
): GExerciseCardUiModel {
    val inCart = id in cartExerciseIds
    return GExerciseCardUiModel(
        id = id,
        image = image.toExerciseLibraryCardImage(),
        title = name,
        subtitle = exerciseLibraryCardStaticSubtitle(context),
        badgeText = null,
        isSelected = inCart && id == activeExerciseId,
        isInCartInactive = inCart && id != activeExerciseId,
    )
}

fun ExerciseLibraryCatalogEntryUiModel.toGExerciseCardUiModel(
    cartExerciseIds: Set<String>,
    activeExerciseId: String?,
): GExerciseCardUiModel {
    val inCart = id in cartExerciseIds
    return GExerciseCardUiModel(
        id = id,
        image = image,
        title = name,
        subtitle = libraryCardStaticSubtitle,
        badgeText = null,
        isSelected = inCart && id == activeExerciseId,
        isInCartInactive = inCart && id != activeExerciseId,
    )
}

internal fun Exercise.exerciseLibraryCardStaticSubtitle(context: Context): String = libraryCardSubtitle(context)

private fun Exercise.libraryCardSubtitle(context: Context): String {
    val region = context.getString(ExerciseDisplayResources.bodyRegionResId(bodyRegion))
    if (equipment != null) {
        val equip = context.getString(ExerciseDisplayResources.equipmentResId(equipment))
        return context.getString(R.string.exercise_library_card_region_equipment, region, equip)
    }
    return region
}
