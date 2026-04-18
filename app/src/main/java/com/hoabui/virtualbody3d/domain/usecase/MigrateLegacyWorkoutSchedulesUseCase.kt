package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.repository.WorkoutSessionRepository
import javax.inject.Inject

class MigrateLegacyWorkoutSchedulesUseCase @Inject constructor(
    private val workoutSessionRepository: WorkoutSessionRepository,
) {
    suspend operator fun invoke() {
        workoutSessionRepository.migrateLegacySchedulesIfNeeded()
    }
}
