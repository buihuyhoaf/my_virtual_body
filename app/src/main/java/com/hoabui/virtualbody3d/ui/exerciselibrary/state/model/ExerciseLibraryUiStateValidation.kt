package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.normalizeDurationMinutesSeconds

/**
 * `true` when every cart draft is valid for booking confirm:
 *  - Strength: at least one set row with positive reps.
 *  - Duration: the first row's minutes+seconds normalise to > 0 total seconds.
 */
fun ExerciseLibraryUiState.isCartDraftValidForSessionConfirm(): Boolean {
    if (cart.itemDrafts.isEmpty()) return false
    return cart.itemDrafts.all { (id, draft) ->
        val mode = libraryList.exerciseMeasurementById[id]
            ?: chrome.selectionBarEditMeasurementMode
            ?: ExerciseMeasurementMode.Strength
        when (mode) {
            ExerciseMeasurementMode.Strength -> {
                draft.setRows.isNotEmpty() && draft.setRows.all { row -> row.reps > 0 }
            }
            ExerciseMeasurementMode.Duration -> {
                val row = draft.setRows.firstOrNull() ?: return@all false
                normalizeDurationMinutesSeconds(row.minutes, row.seconds) > 0
            }
        }
    }
}
