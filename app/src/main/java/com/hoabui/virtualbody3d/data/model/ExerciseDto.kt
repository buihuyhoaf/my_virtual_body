package com.hoabui.virtualbody3d.data.model

data class ExerciseDto(
    // Base exercise info (used in all contexts)
    val id: String,
    val name: String,
    val imageResId: Int,
    val imageResUrl: String? = null,
    val bodyRegion: String,
    val difficulty: String,
    val description: String,
    val primaryMuscles: List<String>,
    val secondaryMuscles: List<String>? = null,
    val equipment: String,
    val safetyNotes: String,
    val lastWeightKg: Double? = null,
    // Workout-specific info
    val sets: Int = 0,
    val reps: Int = 0
)
