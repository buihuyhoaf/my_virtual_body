package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.FeedExercise
import com.hoabui.virtualbody3d.domain.repository.ExercisesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExercisesUseCase @Inject constructor(
    private val exercisesRepository: ExercisesRepository
) {
    operator fun invoke(): Flow<List<FeedExercise>> =
        exercisesRepository.getExercises()
}
