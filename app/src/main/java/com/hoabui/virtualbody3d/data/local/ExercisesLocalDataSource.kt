package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.model.ExerciseDto
import com.hoabui.virtualbody3d.data.local.mock.ExerciseMockData
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExercisesLocalDataSource @Inject constructor() {

    fun getExercises(): Flow<List<ExerciseDto>> = flowOf(sampleExercises())

    /**
     * Exercises planned for [day]. Local mock ignores [day] and returns the same list; replace with
     * persistence-backed schedule when available.
     */
    @Suppress("UNUSED_PARAMETER")
    fun getExercisesByDay(day: LocalDate): Flow<List<ExerciseDto>> =
        getExercises()

    private fun sampleExercises(): List<ExerciseDto> = ExerciseMockData.allExercises()
        .take(3)
        .mapIndexed { index, dto ->
            dto.copy(
                sets = if (index == 1) 3 else 4,
                reps = if (index == 1) 8 else 12
            )
        }
}
