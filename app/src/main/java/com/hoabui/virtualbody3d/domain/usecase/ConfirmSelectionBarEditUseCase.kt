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
    private val cartManager: ExerciseLibraryCartManager,
) {
    suspend operator fun invoke() {
        val s = observeExerciseLibraryUiStateUseCase.mergedUiState()
        val chromeMode = cartManager.chromeMode.value
        val editMode = chromeMode as? ExerciseLibraryChromeMode.EditingScheduleRow ?: return
        val rowId = editMode.scheduleRowId
        if (!s.isCartDraftValidForSessionConfirm(chromeMode)) return
        val exerciseId = s.activeExerciseId ?: s.draftOrder.firstOrNull() ?: return
        val draft = s.itemDrafts[exerciseId] ?: return
        val mode = s.libraryList.exerciseMeasurementById[exerciseId]
            ?: editMode.measurementMode
            ?: getWorkoutScheduleByRowUseCase(rowId)?.measurementMode
            ?: ExerciseMeasurementMode.Strength
        val sets: Int
        val reps: Int
        val weightKg: Double
        val durationSeconds: Int?
        when (mode) {
            ExerciseMeasurementMode.Strength -> {
                if (draft.setRows.any { it.reps <= 0 }) return
                sets = draft.setRows.size
                reps = draft.setRows.first().reps
                weightKg = draft.setRows.first().weightKg
                durationSeconds = null
            }
            ExerciseMeasurementMode.Duration -> {
                val row = draft.setRows.firstOrNull() ?: return
                val sec = normalizeDurationMinutesSeconds(row.minutes, row.seconds)
                if (sec <= 0) return
                sets = 1
                reps = 0
                weightKg = 0.0
                durationSeconds = sec
            }
        }
        val ok = updateWorkoutScheduleFromCartDraftUseCase(
            rowId = rowId,
            exerciseId = exerciseId,
            measurementMode = mode,
            sets = sets,
            reps = reps,
            weightKg = weightKg,
            durationSeconds = durationSeconds,
        )
        if (ok) {
            val m = cartManager.chromeMode.value as? ExerciseLibraryChromeMode.EditingScheduleRow ?: return
            cartManager.setChromeIdle()
            if (m.isIsolatedScheduleRowSelectionEdit) {
                cartManager.clearCartForIsolatedSelectionEdit()
            } else {
                cartManager.setCartExpanded(false)
            }
        }
    }
}
