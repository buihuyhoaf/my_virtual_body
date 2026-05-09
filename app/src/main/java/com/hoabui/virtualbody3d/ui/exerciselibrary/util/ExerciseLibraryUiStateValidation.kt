package com.hoabui.virtualbody3d.ui.exerciselibrary.util

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.normalizeDurationMinutesSeconds
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState

fun ExerciseLibraryUiState.isCartDraftValidForSessionConfirm(
    selectionBarMeasurementModeFallback: ExerciseMeasurementMode? = null,
): Boolean {
    if (itemDrafts.isEmpty()) return false
    return itemDrafts.all { (id, draft) ->
        val mode = libraryList.exerciseMeasurementById[id]
            ?: selectionBarMeasurementModeFallback
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
