package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.normalizeDurationMinutesSeconds
import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryCartManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryChromeMode
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.isCartDraftValidForSessionConfirm
import javax.inject.Inject

class ConfirmSelectionBarEditUseCase @Inject constructor(
    private val observeExerciseLibraryUiStateUseCase: ObserveExerciseLibraryUiStateUseCase,
    private val getWorkoutScheduleByRowUseCase: GetWorkoutScheduleByRowUseCase,
    private val updateWorkoutScheduleFromCartDraftUseCase: UpdateWorkoutScheduleFromCartDraftUseCase,
    private val exerciseLibraryCartManager: ExerciseLibraryCartManager,
) {
    suspend operator fun invoke() {
        val chrome = exerciseLibraryCartManager.chromeMode.value as? ExerciseLibraryChromeMode.EditingScheduleRow
            ?: return

        val schedule = getWorkoutScheduleByRowUseCase(chrome.scheduleRowId)
        if (schedule == null) {
            exerciseLibraryCartManager.cancelSelectionBarEdit()
            return
        }

        val snapshot = observeExerciseLibraryUiStateUseCase.mergedUiState()
        val exerciseId = snapshot.draftOrder.firstOrNull() ?: return
        if (schedule.exerciseId != exerciseId) {
            exerciseLibraryCartManager.cancelSelectionBarEdit()
            return
        }

        if (
            !snapshot.isCartDraftValidForSessionConfirm(
                selectionBarMeasurementModeFallback = chrome.measurementModeFallback,
            )
        ) {
            return
        }

        val draft = snapshot.itemDrafts[exerciseId] ?: return
        val measurementMode =
            snapshot.libraryList.exerciseMeasurementById[exerciseId]
                ?: chrome.measurementModeFallback

        val updated = when (measurementMode) {
            ExerciseMeasurementMode.Strength -> {
                val sets = draft.setRows.size.coerceAtLeast(1)
                val row = draft.setRows.firstOrNull() ?: return
                updateWorkoutScheduleFromCartDraftUseCase(
                    rowId = chrome.scheduleRowId,
                    exerciseId = exerciseId,
                    measurementMode = measurementMode,
                    sets = sets,
                    reps = row.reps,
                    weightKg = row.weightKg,
                    durationSeconds = null,
                )
            }
            ExerciseMeasurementMode.Duration -> {
                val row = draft.setRows.firstOrNull() ?: return
                val durationSeconds = normalizeDurationMinutesSeconds(row.minutes, row.seconds)
                updateWorkoutScheduleFromCartDraftUseCase(
                    rowId = chrome.scheduleRowId,
                    exerciseId = exerciseId,
                    measurementMode = measurementMode,
                    sets = 1,
                    reps = 0,
                    weightKg = 0.0,
                    durationSeconds = durationSeconds,
                )
            }
        }

        if (updated) {
            exerciseLibraryCartManager.cancelSelectionBarEdit()
        }
    }
}
