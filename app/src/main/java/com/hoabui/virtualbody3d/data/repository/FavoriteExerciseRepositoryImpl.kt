package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.FavoriteExerciseLocalDataSource
import com.hoabui.virtualbody3d.data.mapper.toDomain
import com.hoabui.virtualbody3d.domain.model.FavoriteExercise
import com.hoabui.virtualbody3d.domain.repository.FavoriteExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteExerciseRepositoryImpl @Inject constructor(
    private val localDataSource: FavoriteExerciseLocalDataSource
) : FavoriteExerciseRepository {

    override fun getFavoriteExercises(): Flow<List<FavoriteExercise>> =
        localDataSource.getFavoriteExercises().map { list -> list.map { it.toDomain() } }
}
