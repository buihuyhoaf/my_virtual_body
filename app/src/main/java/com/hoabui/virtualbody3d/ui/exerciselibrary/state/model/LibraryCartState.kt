package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

@Immutable
data class LibraryCartState(
    /**
     * Draft lines keyed by exercise id (cart). Source of truth for membership.
     * Order of items in the bar is [draftOrder], not key iteration order.
     */
    val itemDrafts: ImmutableMap<String, ExerciseDraft> = persistentMapOf(),
    /**
     * Stable left-to-right cart order. Invariant: same multiset of ids as [itemDrafts].keys
     * (each id exactly once). Kept in sync with [itemDrafts] on every cart mutation in the screen ViewModel.
     */
    val draftOrder: ImmutableList<String> = persistentListOf(),
    /** Which cart line is being edited in the console. */
    val activeExerciseId: String? = null,
)
