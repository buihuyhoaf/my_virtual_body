package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import android.content.Context
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.toExerciseLibraryCardImage
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseCardUiModel

/**
 * Maps domain [Exercise] to [GExerciseCardUiModel] for the exercise library list, resolving
 * strings outside composables (Context). Image uses [com.hoabui.virtualbody3d.ui.exerciselibrary.model.ExerciseLibraryCardImage]
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
        subtitle = libraryCardSubtitle(context),
        badgeText = null,
        isSelected = inCart && id == activeExerciseId,
        isInCartInactive = inCart && id != activeExerciseId,
    )
}

private fun Exercise.libraryCardSubtitle(context: Context): String {
    if (primaryMuscles.isNotEmpty()) {
        return primaryMuscles.take(2).joinToString { muscle ->
            context.getString(ExerciseDisplayResources.muscleGroupResId(muscle))
        }
    }
    if (equipment != null) {
        return context.getString(ExerciseDisplayResources.equipmentResId(equipment))
    }
    return context.getString(R.string.exercise_library_card_subtitle_fallback)
}
