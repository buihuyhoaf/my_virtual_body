package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.ExerciseLibraryCardImage

/**
 * Stable catalog row for the exercise library: no cart decoration (that is applied when projecting sections).
 */
@Immutable
data class ExerciseLibraryCatalogEntryUiModel(
    val id: String,
    val name: String,
    val category: ExerciseCategory,
    val equipment: EquipmentType?,
    val bodyRegion: BodyRegion,
    val measurementMode: ExerciseMeasurementMode,
    val image: ExerciseLibraryCardImage,
    /** Subtitle for list cards, resolved at catalog load (region + optional equipment). */
    val libraryCardStaticSubtitle: String,
)
