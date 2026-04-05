package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.local.db.ExerciseDao
import com.hoabui.virtualbody3d.data.local.db.toExerciseDto
import com.hoabui.virtualbody3d.data.model.ExerciseDto
import com.hoabui.virtualbody3d.di.IoDispatcher
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

@Singleton
class ExercisesLocalDataSource @Inject constructor(
    private val exerciseDao: ExerciseDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    fun getAllExercises(): Flow<List<ExerciseDto>> =
        exerciseDao.observeAll()
            .map { entities -> entities.map { it.toExerciseDto() } }
            .flowOn(ioDispatcher)
}
