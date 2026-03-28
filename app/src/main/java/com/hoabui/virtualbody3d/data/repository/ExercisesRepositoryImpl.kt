package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.ExercisesLocalDataSource
import com.hoabui.virtualbody3d.data.mapper.ExerciseMapper
import com.hoabui.virtualbody3d.domain.model.exercise.FeedExercise
import com.hoabui.virtualbody3d.domain.repository.ExercisesRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExercisesRepositoryImpl @Inject constructor(
    private val localDataSource: ExercisesLocalDataSource,
    private val exerciseMapper: ExerciseMapper
) : ExercisesRepository {

    override fun getExercises(): Flow<List<FeedExercise>> =
        localDataSource.getExercises().map { list -> list.map(exerciseMapper::toFeedExercise) }

    override fun getExercisesByDay(day: LocalDate): Flow<List<FeedExercise>> =
        localDataSource.getExercisesByDay(day).map { list -> list.map(exerciseMapper::toFeedExercise) }
}
