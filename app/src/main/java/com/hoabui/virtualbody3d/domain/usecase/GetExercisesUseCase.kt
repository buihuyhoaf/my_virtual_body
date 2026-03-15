package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.BodyRegion
import com.hoabui.virtualbody3d.domain.model.Exercise
import com.hoabui.virtualbody3d.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetExercisesUseCase @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) {
    /**
     * Returns exercises grouped by [BodyRegion], with regions in enum order.
     */
    operator fun invoke(): Flow<Map<BodyRegion, List<Exercise>>> =
        exerciseRepository.getAllExercises().map { exercises ->
            exercises.groupBy { it.bodyRegion }.let { grouped ->
                BodyRegion.entries.mapNotNull { region ->
                    grouped[region]?.let { list -> region to list }
                }.toMap()
            }
        }
}
