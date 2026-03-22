package com.hoabui.virtualbody3d.ui.exerciselibrary.state

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.Difficulty
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.MuscleGroup

/**
 * Presentation model for an exercise, optimized for UI display in Exercise Library and Detail screens.
 *
 * Labels (difficulty, bodyRegion, primaryMuscle, equipment) are resolved in composables via
 * [ExerciseDisplayResources] and [stringResource], so this model holds enums for type safety and filtering.
 */
@Immutable
data class ExerciseUiModel(
    val id: String,
    val name: String,
    val imageResId: Int,
    val difficulty: Difficulty,
    val bodyRegion: BodyRegion,
    val primaryMuscles: List<MuscleGroup>,
    val secondaryMuscles: List<MuscleGroup>,
    val equipment: EquipmentType?,
    val description: String,
    val safetyNotes: String,
    val lastWeightKg: Double? = null
)
