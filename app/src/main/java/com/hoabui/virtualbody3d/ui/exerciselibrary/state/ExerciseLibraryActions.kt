package com.hoabui.virtualbody3d.ui.exerciselibrary.state

import com.hoabui.virtualbody3d.ui.exerciselibrary.ExerciseLibraryQuickChip
import java.time.LocalTime

/**
 * Hoisted callbacks for the Exercise Library flow so screens and organisms
 * pass a single stable object instead of many lambda parameters.
 */
data class ExerciseLibraryActions(
    val onQueryChange: (String) -> Unit,
    val onQuickChipSelect: (ExerciseLibraryQuickChip?) -> Unit,
    val onExerciseClick: (String) -> Unit,
    val onLibraryListToggle: (String) -> Unit,
    val onDetailAddToCart: (String) -> Unit,
    val onSelectCartItem: (String) -> Unit,
    val onRemoveCartItem: (String) -> Unit,
    val onClearCart: () -> Unit,
    val onCartDateSelected: (Long) -> Unit,
    val onCartTimeSelected: (LocalTime) -> Unit,
    val onActiveDraftChange: (sets: String, reps: String) -> Unit,
    val onConfirmCart: () -> Unit,
    val onClearExerciseDetail: () -> Unit,
    val onDismissAddExerciseSuccess: () -> Unit,
    val onOpenWorkoutPlan: () -> Unit,
)
