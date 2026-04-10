package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseLibraryCartSnapshot
import com.hoabui.virtualbody3d.domain.model.exercise.LibraryExerciseLineDraft
import com.hoabui.virtualbody3d.domain.model.exercise.resolveActiveExerciseIdAfterRemoval
import javax.inject.Inject

sealed interface ExerciseLibraryCartCommand {
    data class Toggle(val exerciseId: String) : ExerciseLibraryCartCommand
    data class EnsureInCartAndFocus(val exerciseId: String) : ExerciseLibraryCartCommand
    data class Remove(val exerciseId: String) : ExerciseLibraryCartCommand
    data class SetActive(val exerciseId: String) : ExerciseLibraryCartCommand
    data object Clear : ExerciseLibraryCartCommand
}

/**
 * Cart membership, stable [ExerciseLibraryCartSnapshot.draftOrder], and active-line focus after mutations.
 */
class ToggleExerciseInCartUseCase @Inject constructor() {

    operator fun invoke(
        snapshot: ExerciseLibraryCartSnapshot,
        command: ExerciseLibraryCartCommand,
    ): ExerciseLibraryCartSnapshot =
        when (command) {
            is ExerciseLibraryCartCommand.Toggle -> toggle(snapshot, command.exerciseId)
            is ExerciseLibraryCartCommand.EnsureInCartAndFocus -> ensureInCartAndFocus(snapshot, command.exerciseId)
            is ExerciseLibraryCartCommand.Remove -> remove(snapshot, command.exerciseId)
            is ExerciseLibraryCartCommand.SetActive -> setActive(snapshot, command.exerciseId)
            ExerciseLibraryCartCommand.Clear -> ExerciseLibraryCartSnapshot(
                itemDrafts = emptyMap(),
                draftOrder = emptyList(),
                activeExerciseId = null,
            )
        }

    private fun toggle(snapshot: ExerciseLibraryCartSnapshot, exerciseId: String): ExerciseLibraryCartSnapshot {
        val drafts = snapshot.itemDrafts.toMutableMap()
        if (exerciseId !in drafts) {
            drafts[exerciseId] = LibraryExerciseLineDraft("", "")
            val nextOrder = if (exerciseId in snapshot.draftOrder) {
                snapshot.draftOrder
            } else {
                snapshot.draftOrder + exerciseId
            }
            return snapshot.copy(
                itemDrafts = drafts,
                draftOrder = nextOrder,
                activeExerciseId = exerciseId,
            )
        }
        drafts.remove(exerciseId)
        val orderBefore = snapshot.draftOrder
        val nextOrder = snapshot.draftOrder.filter { it != exerciseId }
        val nextActive = resolveActiveExerciseIdAfterRemoval(
            removedId = exerciseId,
            previousActive = snapshot.activeExerciseId,
            remainingDraftIds = drafts.keys,
            orderBeforeRemoval = orderBefore,
            newOrder = nextOrder,
        )
        return snapshot.copy(
            itemDrafts = drafts,
            draftOrder = nextOrder,
            activeExerciseId = nextActive,
        )
    }

    private fun ensureInCartAndFocus(snapshot: ExerciseLibraryCartSnapshot, exerciseId: String): ExerciseLibraryCartSnapshot {
        val drafts = snapshot.itemDrafts.toMutableMap()
        if (exerciseId !in drafts) {
            drafts[exerciseId] = LibraryExerciseLineDraft("", "")
            val nextOrder = if (exerciseId in snapshot.draftOrder) {
                snapshot.draftOrder
            } else {
                snapshot.draftOrder + exerciseId
            }
            return snapshot.copy(
                itemDrafts = drafts,
                draftOrder = nextOrder,
                activeExerciseId = exerciseId,
            )
        }
        return snapshot.copy(activeExerciseId = exerciseId)
    }

    private fun remove(snapshot: ExerciseLibraryCartSnapshot, exerciseId: String): ExerciseLibraryCartSnapshot {
        if (exerciseId !in snapshot.itemDrafts) return snapshot
        val drafts = snapshot.itemDrafts.toMutableMap()
        drafts.remove(exerciseId)
        val orderBefore = snapshot.draftOrder
        val nextOrder = snapshot.draftOrder.filter { it != exerciseId }
        val nextActive = resolveActiveExerciseIdAfterRemoval(
            removedId = exerciseId,
            previousActive = snapshot.activeExerciseId,
            remainingDraftIds = drafts.keys,
            orderBeforeRemoval = orderBefore,
            newOrder = nextOrder,
        )
        return snapshot.copy(
            itemDrafts = drafts,
            draftOrder = nextOrder,
            activeExerciseId = nextActive,
        )
    }

    private fun setActive(snapshot: ExerciseLibraryCartSnapshot, exerciseId: String): ExerciseLibraryCartSnapshot {
        if (exerciseId !in snapshot.itemDrafts) return snapshot
        return snapshot.copy(activeExerciseId = exerciseId)
    }
}
