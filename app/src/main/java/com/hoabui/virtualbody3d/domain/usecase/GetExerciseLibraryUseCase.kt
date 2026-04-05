package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.repository.ExercisesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetExerciseLibraryUseCase @Inject constructor(
    private val exercisesRepository: ExercisesRepository
) {
    /**
     * Returns exercises grouped by [BodyRegion], with regions in enum order.
     */
    operator fun invoke(): Flow<Map<BodyRegion, List<Exercise>>> =
        exercisesRepository.getAllExercises().map { exercises ->
            exercises.groupBy { it.bodyRegion }.let { grouped ->
                BodyRegion.entries.mapNotNull { region ->
                    grouped[region]?.let { list -> region to list }
                }.toMap()
            }
        }
}
