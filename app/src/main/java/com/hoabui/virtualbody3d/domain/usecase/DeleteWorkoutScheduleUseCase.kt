package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutScheduleDeleteResult
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import javax.inject.Inject

class DeleteWorkoutScheduleUseCase @Inject constructor(
    private val workoutScheduleRepository: WorkoutScheduleRepository,
) {
    suspend operator fun invoke(rowId: Long): WorkoutScheduleDeleteResult? =
        workoutScheduleRepository.deleteWorkoutScheduleByRowId(rowId)
}
