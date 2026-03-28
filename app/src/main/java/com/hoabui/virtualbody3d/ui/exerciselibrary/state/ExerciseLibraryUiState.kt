package com.hoabui.virtualbody3d.ui.exerciselibrary.state

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseCardUiModel

/**
 * UI state for the Exercise Library screen.
 */
@Immutable
data class ExerciseLibraryUiState(
    val searchQuery: String = "",
    val selectedExerciseCategory: ExerciseCategory? = null,
    val selectedEquipment: EquipmentType? = null,
    val quickAddedExerciseIds: Set<String> = emptySet(),
    val sections: List<ExerciseSectionUiItem> = emptyList(),
    val selectedExerciseForDetail: Exercise? = null
)

/**
 * One section in the library: a body region and its exercises.
 */
@Immutable
data class ExerciseSectionUiItem(
    val bodyRegion: BodyRegion,
    val items: List<GExerciseCardUiModel>
)
