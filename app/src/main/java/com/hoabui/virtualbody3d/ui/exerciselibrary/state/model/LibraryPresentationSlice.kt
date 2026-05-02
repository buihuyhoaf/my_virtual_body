package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

/**
 * Library list + measurement map derived from catalog + cart filters (narrow combine inputs).
 */
@Immutable
data class LibraryPresentationSlice(
    val sections: ImmutableList<ExerciseLibrarySectionRowUiModel> = persistentListOf(),
    val exerciseMeasurementById: ImmutableMap<String, ExerciseMeasurementMode> = persistentMapOf(),
    val selectedExerciseForDetail: ExerciseDetailSheetUiModel? = null,
    val isAddToSessionEnabled: Boolean = false,
)
