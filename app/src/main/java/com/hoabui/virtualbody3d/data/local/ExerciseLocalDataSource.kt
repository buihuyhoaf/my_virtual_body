package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.local.mock.ExerciseMockData
import com.hoabui.virtualbody3d.data.model.ExerciseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseLocalDataSource @Inject constructor() {
    fun getAllExercises(): Flow<List<ExerciseDto>> = flowOf(ExerciseMockData.allExercises())
}
