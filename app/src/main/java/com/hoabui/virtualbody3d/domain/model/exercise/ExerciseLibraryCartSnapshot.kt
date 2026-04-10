package com.hoabui.virtualbody3d.domain.model.exercise

/**
 * Domain snapshot of the exercise library cart (ordering, line drafts, focus).
 * Used by [com.hoabui.virtualbody3d.domain.usecase.ToggleExerciseInCartUseCase] and related mutations.
 */
data class ExerciseLibraryCartSnapshot(
    val itemDrafts: Map<String, LibraryExerciseLineDraft>,
    val draftOrder: List<String>,
    val activeExerciseId: String?,
)
