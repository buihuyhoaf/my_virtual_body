package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.model.ExerciseDto
import com.hoabui.virtualbody3d.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteExerciseLocalDataSource @Inject constructor() {

    fun getFavoriteExercises(): Flow<List<ExerciseDto>> = flowOf(
        listOf(
            ExerciseDto(
                id = "1",
                name = "Bench Press",
                imageResId = R.drawable.body_unsplash,
                bodyRegion = "Chest",
                difficulty = "Intermediate",
                description = "Bench press (favorite)",
                primaryMuscles = listOf("Pectoralis"),
                secondaryMuscles = listOf("Triceps"),
                equipment = "Barbell",
                safetyNotes = "",
                lastWeightKg = null
            ),
            ExerciseDto(
                id = "2",
                name = "Squat",
                imageResId = R.drawable.body_unsplash,
                bodyRegion = "Legs",
                difficulty = "Intermediate",
                description = "Squat (favorite)",
                primaryMuscles = listOf("Quadriceps", "Glutes"),
                secondaryMuscles = listOf("Hamstrings"),
                equipment = "Barbell",
                safetyNotes = "",
                lastWeightKg = null
            ),
            ExerciseDto(
                id = "3",
                name = "Deadlift",
                imageResId = R.drawable.body_unsplash,
                bodyRegion = "Back",
                difficulty = "Advanced",
                description = "Deadlift (favorite)",
                primaryMuscles = listOf("Back", "Hamstrings", "Glutes"),
                secondaryMuscles = listOf("Quadriceps"),
                equipment = "Barbell",
                safetyNotes = "",
                lastWeightKg = null
            )
        )
    )
}
