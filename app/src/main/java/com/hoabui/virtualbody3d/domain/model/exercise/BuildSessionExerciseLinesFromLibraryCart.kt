package com.hoabui.virtualbody3d.domain.model.exercise

/**
 * Builds [SessionExerciseLine] rows from library cart drafts (sets/reps text).
 */
fun buildSessionExerciseLinesFromLibraryCart(
    cart: LibraryCartDraft,
    exercisesById: Map<String, Exercise>,
): List<SessionExerciseLine> {
    val lines = mutableListOf<SessionExerciseLine>()
    for ((idx, exerciseId) in cart.draftOrder.withIndex()) {
        val draft = cart.itemDrafts[exerciseId] ?: continue
        val ex = exercisesById[exerciseId] ?: continue
        when (ex.measurementMode) {
            ExerciseMeasurementMode.Strength -> {
                val sets = draft.sets.trim().toIntOrNull() ?: continue
                val reps = draft.reps.trim().toIntOrNull() ?: continue
                if (sets <= 0 || reps <= 0) continue
                val (snapUrl, snapLocal) = ex.image.toScheduleImageSnapshot()
                lines.add(
                    SessionExerciseLine(
                        exerciseId = exerciseId,
                        sets = sets,
                        reps = reps,
                        weightKg = ex.lastWeightKg ?: 0.0,
                        restSeconds = 90,
                        notes = null,
                        measurementMode = ExerciseMeasurementMode.Strength,
                        durationSeconds = null,
                        orderIndex = idx,
                        exerciseImageResUrl = snapUrl,
                        exerciseLocalImageName = snapLocal,
                    ),
                )
            }
            ExerciseMeasurementMode.Duration -> {
                val minutes = draft.sets.trim().toIntOrNull() ?: 0
                val seconds = draft.reps.trim().toIntOrNull() ?: 0
                val total = normalizeDurationMinutesSeconds(minutes, seconds)
                if (total <= 0) continue
                val (snapUrl, snapLocal) = ex.image.toScheduleImageSnapshot()
                lines.add(
                    SessionExerciseLine(
                        exerciseId = exerciseId,
                        sets = 1,
                        reps = 0,
                        weightKg = ex.lastWeightKg ?: 0.0,
                        restSeconds = 90,
                        notes = null,
                        measurementMode = ExerciseMeasurementMode.Duration,
                        durationSeconds = total,
                        orderIndex = idx,
                        exerciseImageResUrl = snapUrl,
                        exerciseLocalImageName = snapLocal,
                    ),
                )
            }
        }
    }
    return lines
}
