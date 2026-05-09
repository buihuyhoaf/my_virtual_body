package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import javax.inject.Inject

class GetWorkoutScheduleByRowUseCase @Inject constructor(
    private val workoutScheduleRepository: WorkoutScheduleRepository,
) {
    suspend operator fun invoke(rowId: Long) =
        workoutScheduleRepository.getWorkoutScheduleByRowId(rowId)
}
