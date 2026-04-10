package com.hoabui.virtualbody3d.ui.exerciselibrary.presentation

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import kotlinx.collections.immutable.ImmutableList

/** Inputs for booking UI projection after dedupe on [ExerciseLibraryBookingPresentationKey]. */
@Immutable
internal data class BookingPipelineRow(
    val dedupeKey: ExerciseLibraryBookingPresentationKey?,
    val filtersWithMeasurement: ExerciseLibraryUiState,
    val gymLocations: ImmutableList<GymLocation>,
)
