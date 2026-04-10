package com.hoabui.virtualbody3d.domain.model.exercise

import java.time.LocalTime

/**
 * Domain representation of the library cart while booking a session.
 */
data class LibraryExerciseLineDraft(
    val sets: String,
    val reps: String,
)

data class LibraryCartDraft(
    val draftOrder: List<String>,
    val itemDrafts: Map<String, LibraryExerciseLineDraft>,
)

fun LibraryCartDraft.isValidForSessionConfirm(
    exerciseMeasurementById: Map<String, ExerciseMeasurementMode>,
): Boolean {
    if (itemDrafts.isEmpty()) return false
    return itemDrafts.all { (id, draft) ->
        val mode = exerciseMeasurementById[id] ?: ExerciseMeasurementMode.Strength
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

/**
 * Pending time/location/slots for confirming a workout from the exercise library.
 */
data class PendingSessionBooking(
    val selectedDateMillis: Long,
    val selectedLocationId: String,
    val selectedSlotStarts: List<LocalTime>,
    val longSessionAcknowledged: Boolean,
    val isConfirming: Boolean,
)
