package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.FavoriteExercise
import com.hoabui.virtualbody3d.domain.repository.FavoriteExerciseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteExercisesUseCase @Inject constructor(
    private val favoriteExerciseRepository: FavoriteExerciseRepository
) {
    operator fun invoke(): Flow<List<FavoriteExercise>> =
        favoriteExerciseRepository.getFavoriteExercises()
}
