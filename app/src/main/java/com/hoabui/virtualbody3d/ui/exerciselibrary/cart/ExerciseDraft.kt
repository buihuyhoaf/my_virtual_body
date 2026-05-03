package com.hoabui.virtualbody3d.ui.exerciselibrary.cart

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Per-exercise draft while building the library cart. Holds a list of [SetRowDraft] items,
 * one per set. Stepper UI drives all mutations.
 */
@Immutable
data class SetRowDraft(
    val reps: Int = 10,
    val weightKg: Double = 0.0,
    val minutes: Int = 0,
    val seconds: Int = 30,
)

@Immutable
data class ExerciseDraft(
    val setRows: ImmutableList<SetRowDraft> = persistentListOf(SetRowDraft()),
) {
    companion object {
        val Default: ExerciseDraft = ExerciseDraft()
    }
}
