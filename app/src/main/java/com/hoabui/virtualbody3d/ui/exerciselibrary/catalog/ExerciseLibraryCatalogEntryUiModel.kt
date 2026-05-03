package com.hoabui.virtualbody3d.ui.exerciselibrary.catalog

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.Muscle

@Immutable
data class ExerciseLibraryCatalogEntryUiModel(
    val id: String,
    val name: String,
    val category: ExerciseCategory,
    val equipment: EquipmentType?,
    val bodyRegion: BodyRegion,
    val focusMuscles: List<Muscle>,
    val measurementMode: ExerciseMeasurementMode,
    val image: ExerciseLibraryCardImage,
)
