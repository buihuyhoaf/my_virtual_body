package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.workoutlog.WorkoutLogSessionInput
import com.hoabui.virtualbody3d.domain.repository.WorkoutLogRepository
import javax.inject.Inject

class SaveWorkoutLogSessionUseCase @Inject constructor(
    private val workoutLogRepository: WorkoutLogRepository,
) {
    suspend operator fun invoke(session: WorkoutLogSessionInput) {
        workoutLogRepository.saveWorkoutLogSession(session)
    }
}
