package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.repository.ExercisesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * First [PREVIEW_COUNT] exercises from the library for the home “upcoming” strip.
 * Replace with schedule-backed data when product-ready.
 */
class GetDashboardUpcomingExercisesUseCase @Inject constructor(
    private val exercisesRepository: ExercisesRepository,
) {
    operator fun invoke(): Flow<List<Exercise>> =
        exercisesRepository.getAllExercises().map { list -> list.take(PREVIEW_COUNT) }

    companion object {
        private const val PREVIEW_COUNT = 3
    }
}
