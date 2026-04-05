package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.ExercisesLocalDataSource
import com.hoabui.virtualbody3d.data.mapper.ExerciseMapper
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.repository.ExercisesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExercisesRepositoryImpl @Inject constructor(
    private val localDataSource: ExercisesLocalDataSource,
    private val exerciseMapper: ExerciseMapper
) : ExercisesRepository {

    override fun getAllExercises(): Flow<List<Exercise>> =
        localDataSource.getAllExercises().map { list -> list.map(exerciseMapper::toDomain) }
}
