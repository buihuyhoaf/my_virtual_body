package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseLibraryCartSnapshot
import javax.inject.Inject

/**
 * Updates sets/reps for the active cart line.
 */
class UpdateExerciseDraftUseCase @Inject constructor() {

    operator fun invoke(
        snapshot: ExerciseLibraryCartSnapshot,
        activeExerciseId: String?,
        sets: String,
        reps: String,
    ): ExerciseLibraryCartSnapshot {
        val id = activeExerciseId ?: return snapshot
        val current = snapshot.itemDrafts[id] ?: return snapshot
        val drafts = snapshot.itemDrafts.toMutableMap()
        drafts[id] = current.copy(sets = sets, reps = reps)
        return snapshot.copy(itemDrafts = drafts)
    }
}
