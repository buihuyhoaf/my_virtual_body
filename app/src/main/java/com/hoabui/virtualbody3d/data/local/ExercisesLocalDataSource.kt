package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.model.ExerciseDto
import com.hoabui.virtualbody3d.R
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

    private fun sampleExercises(): List<ExerciseDto> = listOf(
        ExerciseDto(
            id = "1",
            name = "Bench Press",
            imageResId = R.drawable.body_unsplash,
            bodyRegion = "Chest",
            difficulty = "Intermediate",
            description = "Bench press",
            primaryMuscles = listOf("Pectoralis"),
            secondaryMuscles = listOf("Triceps"),
            equipment = "Barbell",
            safetyNotes = "",
            lastWeightKg = null,
            sets = 4,
            reps = 12,
        ),
        ExerciseDto(
            id = "2",
            name = "Squat",
            imageResId = R.drawable.body_unsplash,
            bodyRegion = "Legs",
            difficulty = "Intermediate",
            description = "Squat",
            primaryMuscles = listOf("Quadriceps", "Glutes"),
            secondaryMuscles = listOf("Hamstrings"),
            equipment = "Barbell",
            safetyNotes = "",
            lastWeightKg = null,
            sets = 3,
            reps = 8,
        ),
        ExerciseDto(
            id = "3",
            name = "Deadlift",
            imageResId = R.drawable.body_unsplash,
            bodyRegion = "Back",
            difficulty = "Advanced",
            description = "Deadlift",
            primaryMuscles = listOf("Back", "Hamstrings", "Glutes"),
            secondaryMuscles = listOf("Quadriceps"),
            equipment = "Barbell",
            safetyNotes = "",
            lastWeightKg = null,
            sets = 4,
            reps = 10,
        ),
    )
}
