package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SetRowDraft
import kotlinx.collections.immutable.toImmutableList

/**
 * Builds a cart [ExerciseDraft] from a persisted schedule row so the selection bar matches Room
 * before the user edits (aligned with [com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel.ExerciseLibraryViewModel.onConfirmSelectionBarEdit] mapping).
 */
fun WorkoutSchedule.toExerciseDraftForSelectionBarEdit(): ExerciseDraft =
    when (measurementMode) {
        ExerciseMeasurementMode.Strength -> {
            val setCount = sets.coerceAtLeast(1)
            ExerciseDraft(
                setRows = List(setCount) {
                    SetRowDraft(
                        reps = reps.coerceAtLeast(0),
                        weightKg = weightKg.coerceAtLeast(0.0),
                        minutes = 0,
                        seconds = 0,
                    )
                }.toImmutableList(),
            )
        }
        ExerciseMeasurementMode.Duration -> {
            val total = durationSeconds?.coerceAtLeast(0) ?: 0
            val minutes = total / 60
            val seconds = total % 60
            ExerciseDraft(
                setRows = listOf(
                    SetRowDraft(
                        reps = 0,
                        weightKg = 0.0,
                        minutes = minutes,
                        seconds = seconds,
                    ),
                ).toImmutableList(),
            )
        }
    }
