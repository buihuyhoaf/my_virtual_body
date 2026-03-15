package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun getAllExercises(): Flow<List<Exercise>>
}
