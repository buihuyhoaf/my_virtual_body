package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutScheduleDeleteResult
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import javax.inject.Inject

class RestoreWorkoutScheduleDeleteUseCase @Inject constructor(
    private val workoutScheduleRepository: WorkoutScheduleRepository,
) {
    suspend operator fun invoke(result: WorkoutScheduleDeleteResult) {
        workoutScheduleRepository.restoreWorkoutScheduleDelete(result)
    }
}
