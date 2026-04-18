package com.hoabui.virtualbody3d.ui.exerciselibrary.presentation

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseDetailSheetUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibrarySectionRowUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SessionBookingUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

/**
 * Library list + measurement map derived from catalog + cart filters (narrow combine inputs).
 */
@Immutable
data class LibraryPresentationSlice(
    val sections: ImmutableList<ExerciseLibrarySectionRowUiModel>,
    val exerciseMeasurementById: ImmutableMap<String, ExerciseMeasurementMode>,
    val selectedExerciseForDetail: ExerciseDetailSheetUiModel?,
    val isAddToSessionEnabled: Boolean,
)

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
            selectedExerciseForDetail = library.selectedExerciseForDetail,
            isAddToSessionEnabled = library.isAddToSessionEnabled,
        ),
        sessionBooking = base.sessionBooking.copy(uiModel = sessionBookingUiModel),
    )
