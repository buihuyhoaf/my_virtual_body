package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import javax.inject.Inject

class AddWorkoutUseCase @Inject constructor(
    private val workoutScheduleRepository: WorkoutScheduleRepository
) {
    suspend operator fun invoke(schedule: WorkoutSchedule) {
        workoutScheduleRepository.saveWorkoutSchedule(schedule)
    }
}
