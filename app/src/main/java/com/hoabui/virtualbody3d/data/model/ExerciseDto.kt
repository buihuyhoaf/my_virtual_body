package com.hoabui.virtualbody3d.data.model

/**
 * Data layer DTO for an exercise (local/remote).
 * List and enum fields use string representation for serialization.
 */
data class ExerciseDto(
    val id: String,
    val name: String,
    val imageResId: Int,
    val bodyRegion: String,
    val difficulty: String,
    val description: String,
    val primaryMuscles: List<String>,
    val secondaryMuscles: List<String>,
    val equipment: String?,
    val safetyNotes: String,
    val lastWeightKg: Double? = null
)
