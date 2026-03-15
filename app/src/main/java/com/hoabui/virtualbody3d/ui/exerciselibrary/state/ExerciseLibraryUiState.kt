package com.hoabui.virtualbody3d.ui.exerciselibrary.state

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.BodyRegion
import com.hoabui.virtualbody3d.domain.model.Difficulty
import com.hoabui.virtualbody3d.domain.model.Exercise

/**
 * UI state for the Exercise Library screen.
 */
@Immutable
data class ExerciseLibraryUiState(
    val searchQuery: String = "",
    val selectedBodyRegion: BodyRegion? = null,
    val selectedDifficulty: Difficulty? = null,
    val sections: List<ExerciseSectionUiItem> = emptyList(),
    val selectedExerciseForDetail: Exercise? = null
)

/**
 * One section in the library: a body region and its exercises.
 */
@Immutable
data class ExerciseSectionUiItem(
    val bodyRegion: BodyRegion,
    val exercises: List<ExerciseUiModel>
)
