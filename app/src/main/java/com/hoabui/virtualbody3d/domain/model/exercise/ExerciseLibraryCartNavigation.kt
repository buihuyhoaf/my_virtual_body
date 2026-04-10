package com.hoabui.virtualbody3d.domain.model.exercise

/**
 * Chooses which cart line stays focused after [removedId] was removed.
 */
fun resolveActiveExerciseIdAfterRemoval(
    removedId: String,
    previousActive: String?,
    remainingDraftIds: Set<String>,
    orderBeforeRemoval: List<String>,
    newOrder: List<String>,
): String? {
    if (remainingDraftIds.isEmpty()) return null
    val activeStillValid =
        previousActive != null && previousActive != removedId && previousActive in remainingDraftIds
    if (activeStillValid) return previousActive
    val idx = orderBeforeRemoval.indexOf(removedId)
    if (idx < 0) return newOrder.firstOrNull()
    return newOrder.getOrNull(idx)
        ?: newOrder.getOrNull(idx - 1)
        ?: newOrder.firstOrNull()
}
