package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.WorkoutFeedItem
import com.hoabui.virtualbody3d.domain.repository.WorkoutFeedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for loading the workout feed.
 * Single entry point for ViewModel; encapsulates business logic.
 */
class GetWorkoutFeedUseCase @Inject constructor(
    private val workoutFeedRepository: WorkoutFeedRepository
) {

    operator fun invoke(): Flow<List<WorkoutFeedItem>> =
        workoutFeedRepository.getWorkoutFeed()
}
