package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.normalizeDurationMinutesSeconds

/**
 * `true` when every cart draft is valid for booking confirm (strength: positive sets/reps;
 * duration: normalized total seconds > 0).
 */
fun ExerciseLibraryUiState.isCartDraftValidForSessionConfirm(): Boolean {
    if (cart.itemDrafts.isEmpty()) return false
    return cart.itemDrafts.all { (id, draft) ->
        val mode = libraryList.exerciseMeasurementById[id] ?: ExerciseMeasurementMode.Strength
        when (mode) {
            ExerciseMeasurementMode.Strength -> {
                val sets = draft.sets.trim().toIntOrNull()
                val reps = draft.reps.trim().toIntOrNull()
                sets != null && reps != null && sets > 0 && reps > 0
            }
            ExerciseMeasurementMode.Duration -> {
                val minutes = draft.sets.trim().toIntOrNull() ?: 0
                val seconds = draft.reps.trim().toIntOrNull() ?: 0
                normalizeDurationMinutesSeconds(minutes, seconds) > 0
            }
        }
    }
}
