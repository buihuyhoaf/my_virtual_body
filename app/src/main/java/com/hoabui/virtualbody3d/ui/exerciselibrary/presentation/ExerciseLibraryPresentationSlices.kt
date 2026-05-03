package com.hoabui.virtualbody3d.ui.exerciselibrary.presentation

import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.LibraryPresentationSlice
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SessionBookingUiModel

/**
 * Merges cart [base] with library + booking projections into the screen [ExerciseLibraryUiState].
 */
internal fun mergeExerciseLibraryPresentation(
    base: ExerciseLibraryUiState,
    library: LibraryPresentationSlice,
    sessionBookingUiModel: SessionBookingUiModel?,
): ExerciseLibraryUiState =
    base.copy(
        libraryList = base.libraryList.copy(
            sections = library.sections,
            exerciseMeasurementById = library.exerciseMeasurementById,
            isAddToSessionEnabled = library.isAddToSessionEnabled,
        ),
        sessionBooking = base.sessionBooking.copy(uiModel = sessionBookingUiModel),
    )
