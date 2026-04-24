package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.RegionBodyMuscleSelectionMap
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.emptyFocusMusclesStripImageNames
import kotlinx.collections.immutable.ImmutableList

/**
 * UI state for the Exercise Library screen.
 */
@Immutable
data class ExerciseLibraryUiState(
    val filters: LibraryFilterState = LibraryFilterState(),
    val cart: LibraryCartState = LibraryCartState(),
    val catalog: ExerciseLibraryCatalogState = ExerciseLibraryCatalogState(),
    val libraryList: ExerciseLibraryListProjectionState = ExerciseLibraryListProjectionState(),
    val sessionBooking: SessionBookingSheetState = SessionBookingSheetState(),
    val chrome: LibraryChromeState = LibraryChromeState(),
    val weeklyHeatmap: LibraryWeeklyHeatmapState = LibraryWeeklyHeatmapState.Loading,
    /**
     * Four drawable **resource entry** names (front-upper, back-upper, front-lower, back-lower) for
     * the focus-muscle strip; derived in [com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel.ExerciseLibraryViewModel].
     */
    val focusMusclesStrip: ImmutableList<String> = emptyFocusMusclesStripImageNames(),
    val focusStripClickSelection: RegionBodyMuscleSelectionMap = emptyMap(),
)
