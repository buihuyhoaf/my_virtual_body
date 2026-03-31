package com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

/**
 * Picks [ExerciseLibraryUiState.activeExerciseId] after an item was removed from the cart.
 *
 * @param orderBeforeRemoval [ExerciseLibraryUiState.draftOrder] before the removal.
 * @param newOrder [draftOrder] after removing [removedId] (and after syncing with [newDrafts]).
 */
internal fun resolveActiveExerciseAfterRemoval(
    removedId: String,
    previousActive: String?,
    newDrafts: ImmutableMap<String, *>,
    orderBeforeRemoval: ImmutableList<String>,
    newOrder: ImmutableList<String>,
): String? {
    if (newDrafts.isEmpty()) return null
    val activeStillValid =
        previousActive != null && previousActive != removedId && previousActive in newDrafts
    if (activeStillValid) return previousActive
    val idx = orderBeforeRemoval.indexOf(removedId)
    if (idx < 0) return newOrder.firstOrNull()
    return newOrder.getOrNull(idx)
        ?: newOrder.getOrNull(idx - 1)
        ?: newOrder.firstOrNull()
}
