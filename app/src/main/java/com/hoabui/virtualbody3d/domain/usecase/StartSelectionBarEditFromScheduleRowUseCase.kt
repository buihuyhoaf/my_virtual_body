package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryCartManager
import javax.inject.Inject

class StartSelectionBarEditFromScheduleRowUseCase @Inject constructor(
    private val getWorkoutScheduleByRowUseCase: GetWorkoutScheduleByRowUseCase,
    private val exerciseLibraryCartManager: ExerciseLibraryCartManager,
) {
    suspend operator fun invoke(rowId: Long) {
        val schedule = getWorkoutScheduleByRowUseCase(rowId) ?: return
        exerciseLibraryCartManager.enterScheduleRowSelectionBarEdit(schedule)
    }
}
