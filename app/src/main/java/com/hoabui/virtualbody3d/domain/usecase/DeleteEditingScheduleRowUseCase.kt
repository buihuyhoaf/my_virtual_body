package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutScheduleDeleteResult
import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryCartManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryChromeMode
import javax.inject.Inject

class DeleteEditingScheduleRowUseCase @Inject constructor(
    private val deleteWorkoutScheduleUseCase: DeleteWorkoutScheduleUseCase,
    private val exerciseLibraryCartManager: ExerciseLibraryCartManager,
) {
    suspend operator fun invoke(): WorkoutScheduleDeleteResult? {
        val chrome =
            exerciseLibraryCartManager.chromeMode.value as? ExerciseLibraryChromeMode.EditingScheduleRow
                ?: return null
        val result = deleteWorkoutScheduleUseCase(chrome.scheduleRowId)
        if (result != null) {
            exerciseLibraryCartManager.cancelSelectionBarEdit()
        }
        return result
    }
}
