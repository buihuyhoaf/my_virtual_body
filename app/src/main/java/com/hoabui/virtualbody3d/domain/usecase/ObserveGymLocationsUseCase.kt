package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import com.hoabui.virtualbody3d.domain.repository.WorkoutSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveGymLocationsUseCase @Inject constructor(
    private val workoutSessionRepository: WorkoutSessionRepository,
) {
    operator fun invoke(): Flow<List<GymLocation>> = workoutSessionRepository.observeGymLocations()
}
