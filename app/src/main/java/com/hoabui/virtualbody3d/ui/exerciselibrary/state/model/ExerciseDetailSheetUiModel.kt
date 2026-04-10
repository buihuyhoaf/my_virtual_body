package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.ExerciseLibraryCardImage

/**
 * Resolved content for [com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseDetailDialog].
 */
@Immutable
data class ExerciseDetailSheetUiModel(
    val id: String,
    val name: String,
    val description: String,
    val safetyNotes: String,
    val lastWeightKg: Double?,
    val targetRegionLabel: String,
    val equipmentLabel: String,
    val heroImage: ExerciseLibraryCardImage,
    val heroContentDescription: String,
)
