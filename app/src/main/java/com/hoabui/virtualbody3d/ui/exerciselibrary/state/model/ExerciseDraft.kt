package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Per-exercise draft while building the library cart. Holds a list of [SetRowDraft] items,
 * one per set. Stepper UI drives all mutations; [sets] and [reps] are derived for backward
 * compatibility with the domain booking flow.
 */
@Immutable
data class ExerciseDraft(
    val setRows: ImmutableList<SetRowDraft> = persistentListOf(SetRowDraft()),
) {
    /**
     * Derived: number of sets as a string (used by domain booking validation / summary).
     * For duration exercises this maps to the number of rows, which is always 1.
     */
    val sets: String get() = setRows.size.toString()

    /**
     * Derived: reps of the first set row as a string (used by domain booking validation / summary
     * for strength exercises). Not meaningful for cardio – use [SetRowDraft.minutes]/[SetRowDraft.seconds].
     */
    val reps: String get() = setRows.firstOrNull()?.reps?.toString() ?: ""

    companion object {
        /** Default single-row strength draft. */
        val Default: ExerciseDraft = ExerciseDraft()
    }
}
