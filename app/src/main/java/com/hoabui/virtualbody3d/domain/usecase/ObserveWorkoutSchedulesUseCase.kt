package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveWorkoutSchedulesUseCase @Inject constructor(
    private val workoutScheduleRepository: WorkoutScheduleRepository,
) {
    operator fun invoke(): Flow<List<WorkoutSchedule>> =
        workoutScheduleRepository.observeWorkoutSchedules()
}
