package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable

/**
 * UI state for the Exercise Library screen.
 */
@Immutable
data class ExerciseLibraryUiState(
    val filters: LibraryFilterState = LibraryFilterState(),
    val cart: LibraryCartState = LibraryCartState(),
    val catalog: ExerciseLibraryCatalogState = ExerciseLibraryCatalogState(),
    val libraryList: LibraryPresentationSlice = LibraryPresentationSlice(),
    val sessionBooking: SessionBookingSheetState = SessionBookingSheetState(),
    val chrome: LibraryChromeState = LibraryChromeState(),
)
