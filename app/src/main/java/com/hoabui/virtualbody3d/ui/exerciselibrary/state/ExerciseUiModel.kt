package com.hoabui.virtualbody3d.ui.exerciselibrary.state

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.BodyRegion
import com.hoabui.virtualbody3d.domain.model.Difficulty
import com.hoabui.virtualbody3d.domain.model.EquipmentType
import com.hoabui.virtualbody3d.domain.model.MuscleGroup

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
