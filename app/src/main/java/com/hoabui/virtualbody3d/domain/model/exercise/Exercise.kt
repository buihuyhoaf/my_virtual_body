package com.hoabui.virtualbody3d.domain.model.exercise

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.common.ImageSource

/**
 * Domain model for an exercise with full information for library and detail screens.
 *
 * Classification pillars: [bodyRegion] and [equipment]. Supports library, detail, workout feed.
 */
@Immutable
data class Exercise(
    val id: String,
    val name: String,
    val image: ImageSource,
    val category: ExerciseCategory,
    val bodyRegion: BodyRegion,
    val description: String,
    val equipment: EquipmentType?,
    val safetyNotes: String,
    val lastWeightKg: Double? = null,
    val measurementMode: ExerciseMeasurementMode = ExerciseMeasurementMode.Strength,
)
