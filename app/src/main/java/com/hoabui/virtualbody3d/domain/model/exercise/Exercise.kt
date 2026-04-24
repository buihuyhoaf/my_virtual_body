package com.hoabui.virtualbody3d.domain.model.exercise

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.common.ImageSource

/**
 * Domain model for an exercise with full information for library and detail screens.
 *
 * Classification: [bodyRegion] and [equipment] for grouping; [focusMuscles] drives the focus-muscle strip. Used in library, detail, and workout feed.
 */
@Immutable
data class Exercise(
    val id: String,
    val name: String,
    val image: ImageSource,
    val category: ExerciseCategory,
    val bodyRegion: BodyRegion,
    val focusMuscles: List<Muscle> = emptyList(),
    val description: String,
    val equipment: EquipmentType?,
    val safetyNotes: String,
    val lastWeightKg: Double? = null,
    val measurementMode: ExerciseMeasurementMode = ExerciseMeasurementMode.Strength,
    val regionGroup: RegionGroup? = null,
)
