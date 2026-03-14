package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.FavoriteExercise
import kotlinx.coroutines.flow.Flow

interface FavoriteExerciseRepository {
    fun getFavoriteExercises(): Flow<List<FavoriteExercise>>
}
