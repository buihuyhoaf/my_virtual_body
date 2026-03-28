package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.ExerciseLocalDataSource
import com.hoabui.virtualbody3d.data.mapper.ExerciseMapper
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepositoryImpl @Inject constructor(
    private val localDataSource: ExerciseLocalDataSource,
    private val exerciseMapper: ExerciseMapper
) : ExerciseRepository {

    override fun getAllExercises(): Flow<List<Exercise>> =
        localDataSource.getAllExercises().map { list -> list.map(exerciseMapper::toDomain) }

    override fun getExerciseById(id: String): Flow<Exercise?> =
        localDataSource.getAllExercises().map { list ->
            list.find { it.id == id }?.let(exerciseMapper::toDomain)
        }
}
