package com.hoabui.virtualbody3d.domain.model.exercise

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Domain model for an exercise with full information for library and detail screens.
 *
 * Supports: ExerciseCard display, exercise detail screen, library browsing, workout planner.
 */
@Immutable
data class Exercise(
    val id: String,
    val name: String,
    val image: ImageSource,
    val category: ExerciseCategory,
    val bodyRegion: BodyRegion,
    val description: String,
    val primaryMuscles: ImmutableList<MuscleGroup> = persistentListOf(),
    val secondaryMuscles: ImmutableList<MuscleGroup> = persistentListOf(),
    val equipment: EquipmentType?,
    val safetyNotes: String,
    val lastWeightKg: Double? = null
)
