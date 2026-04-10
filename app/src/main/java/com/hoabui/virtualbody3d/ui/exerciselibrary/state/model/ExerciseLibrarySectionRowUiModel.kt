package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseCardUiModel
import kotlinx.collections.immutable.ImmutableList

/** One section in the library: a body region and its exercises. */
@Immutable
data class ExerciseLibrarySectionRowUiModel(
    val bodyRegion: BodyRegion,
    val items: ImmutableList<GExerciseCardUiModel>,
)
