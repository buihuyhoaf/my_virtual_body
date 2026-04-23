package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import android.content.Context
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.calendar.caloriesToVisualLevel
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.util.CaloriesCalculator
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
    val uptoKcal = CaloriesCalculator.estimateLibraryUptoKcal(id, measurementMode)
    return GExerciseCardUiModel(
        id = id,
        image = image.toExerciseLibraryCardImage(),
        title = name,
        subtitle = context.getString(
            R.string.exercise_library_card_upto_kcal,
            uptoKcal,
        ),
        libraryUptoKcal = uptoKcal,
        subtitleCaloriesVisualLevel = caloriesToVisualLevel(uptoKcal.toFloat()),
        badgeText = null,
        isSelected = inCart && id == activeExerciseId,
        isInCartInactive = inCart && id != activeExerciseId,
    )
}

fun ExerciseLibraryCatalogEntryUiModel.toGExerciseCardUiModel(
    context: Context,
    cartExerciseIds: Set<String>,
    activeExerciseId: String?,
): GExerciseCardUiModel {
    val inCart = id in cartExerciseIds
    val uptoKcal = CaloriesCalculator.estimateLibraryUptoKcal(id, measurementMode)
    return GExerciseCardUiModel(
        id = id,
        image = image,
        title = name,
        subtitle = context.getString(
            R.string.exercise_library_card_upto_kcal,
            uptoKcal,
        ),
        libraryUptoKcal = uptoKcal,
        subtitleCaloriesVisualLevel = caloriesToVisualLevel(uptoKcal.toFloat()),
        badgeText = null,
        isSelected = inCart && id == activeExerciseId,
        isInCartInactive = inCart && id != activeExerciseId,
    )
}
