package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

/**
 * Derived list projection (sections, measurement map, detail selection) merged from catalog + filters + cart.
 */
@Immutable
data class ExerciseLibraryListProjectionState(
    val sections: ImmutableList<ExerciseLibrarySectionRowUiModel> = persistentListOf(),
    val selectedExerciseForDetail: ExerciseDetailSheetUiModel? = null,
    /** [Exercise.id] → measurement mode from the library catalog (for cart validation and console UI). */
    val exerciseMeasurementById: ImmutableMap<String, ExerciseMeasurementMode> = persistentMapOf(),
    /** Precomputed: non-empty valid cart for opening the booking sheet. */
    val isAddToSessionEnabled: Boolean = false,
)
