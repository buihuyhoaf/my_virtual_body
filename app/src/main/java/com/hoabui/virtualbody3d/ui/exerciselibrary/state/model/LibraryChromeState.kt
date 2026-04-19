package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable

@Immutable
data class LibraryChromeState(
    /** Library detail sheet: catalog id when non-null ([ExerciseLibraryListProjectionState.selectedExerciseForDetail] is derived in mapper). */
    val detailExerciseId: String? = null,
    /** When non-null, show add-success confirmation (cart already cleared in the same VM update). */
    val addExerciseSuccess: AddExerciseSuccessSummary? = null,
    /** Selection bar UI for editing an existing scheduled exercise (e.g. from calendar). */
    val isSelectionBarEditMode: Boolean = false,
    /** Room `workout_schedules.id` being edited when [isSelectionBarEditMode] is true. */
    val editingScheduleRowId: Long? = null,
    /** Cart snapshot when edit mode began; restored on cancel without persisting. */
    val selectionBarEditBaselineCart: LibraryCartState? = null,
)
