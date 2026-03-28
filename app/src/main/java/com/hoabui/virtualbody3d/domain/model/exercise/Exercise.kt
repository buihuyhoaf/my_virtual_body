package com.hoabui.virtualbody3d.domain.model.exercise

import com.hoabui.virtualbody3d.domain.model.common.ImageSource

/**
 * Domain model for an exercise with full information for library and detail screens.
 *
 * Supports: ExerciseCard display, exercise detail screen, library browsing, workout planner.
 */
data class Exercise(
    val id: String,
    val name: String,
    val image: ImageSource,
    val category: ExerciseCategory,
    val bodyRegion: BodyRegion,
    val description: String,
    val primaryMuscles: List<MuscleGroup>,
    val secondaryMuscles: List<MuscleGroup>,
    val equipment: EquipmentType?,
    val safetyNotes: String,
    val lastWeightKg: Double? = null
)
