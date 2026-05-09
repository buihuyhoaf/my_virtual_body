package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryCartManager
import javax.inject.Inject

class StartSelectionBarEditFromScheduleRowUseCase @Inject constructor(
    private val getWorkoutScheduleByRowUseCase: GetWorkoutScheduleByRowUseCase,
    private val exerciseLibraryCartManager: ExerciseLibraryCartManager,
) {
    suspend operator fun invoke(scheduleRowId: Long) {
        exerciseLibraryCartManager.setSelectionBarEditMode(
            scheduleRowId = scheduleRowId,
            measurementMode = ExerciseMeasurementMode.Strength,
        )
        val schedule = getWorkoutScheduleByRowUseCase(scheduleRowId) ?: return
        exerciseLibraryCartManager.startSelectionBarEditFromScheduleRow(
            scheduleRowId = scheduleRowId,
            schedule = schedule,
        )
    }
}
