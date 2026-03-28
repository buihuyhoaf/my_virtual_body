package com.hoabui.virtualbody3d.ui.exerciselibrary

import androidx.annotation.StringRes
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState

/**
 * Quick-filter suggestions shown when the exercise search field is focused or has text.
 */
enum class ExerciseLibraryQuickChip(@StringRes val labelRes: Int) {
    Strength(R.string.exercise_category_strength),
    Mobility(R.string.exercise_category_mobility),
    Cardio(R.string.exercise_category_cardio),
    Bodyweight(R.string.exercise_equipment_bodyweight),
}

fun ExerciseLibraryUiState.selectedQuickChip(): ExerciseLibraryQuickChip? = when {
    selectedEquipment == EquipmentType.Bodyweight -> ExerciseLibraryQuickChip.Bodyweight
    selectedExerciseCategory == ExerciseCategory.Strength -> ExerciseLibraryQuickChip.Strength
    selectedExerciseCategory == ExerciseCategory.Mobility -> ExerciseLibraryQuickChip.Mobility
    selectedExerciseCategory == ExerciseCategory.Cardio -> ExerciseLibraryQuickChip.Cardio
    else -> null
}
