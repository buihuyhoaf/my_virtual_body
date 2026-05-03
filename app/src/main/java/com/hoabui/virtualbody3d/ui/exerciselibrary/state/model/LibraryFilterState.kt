package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import kotlinx.collections.immutable.ImmutableSet

@Immutable
data class LibraryFilterState(
    val searchQuery: String = "",
    val selectedExerciseCategory: ExerciseCategory? = null,
    val selectedBodyRegions: ImmutableSet<BodyRegion>? = null,
    val selectedEquipment: EquipmentType? = null,
)
