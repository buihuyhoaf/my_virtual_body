package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable

@Immutable
data class LibraryChromeState(
    /** Library detail sheet: catalog id when non-null ([ExerciseLibraryListProjectionState.selectedExerciseForDetail] is derived in mapper). */
    val detailExerciseId: String? = null,
    /** When non-null, show add-success confirmation (cart already cleared in the same VM update). */
    val addExerciseSuccess: AddExerciseSuccessSummary? = null,
    /**
     * Session cumulative count of exercises successfully scheduled from this screen (increments on confirm).
     * Drives workout-plan FAB badge; not reset when clearing the cart.
     */
    val workoutPlanFabBadgeCount: Int = 0,
)
