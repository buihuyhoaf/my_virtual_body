package com.hoabui.virtualbody3d.ui.exerciselibrary.cart

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

/**
 * Immutable cart snapshot captured when entering selection-bar edit (cancel restores this baseline).
 */
@Immutable
data class SelectionBarCartBaseline(
    val itemDrafts: ImmutableMap<String, ExerciseDraft>,
    val draftOrder: ImmutableList<String>,
    val activeExerciseId: String?,
    val isCartExpanded: Boolean,
)
