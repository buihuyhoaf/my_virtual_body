package com.hoabui.virtualbody3d.ui.exerciselibrary.state.mvi

import com.hoabui.virtualbody3d.domain.model.exercise.Muscle
import com.hoabui.virtualbody3d.domain.model.exercise.RegionBody
import com.hoabui.virtualbody3d.domain.model.exercise.RegionGroup

/**
 * User-driven intents. Cart and slot-toggle mutations are handled via [ExerciseLibraryUpdate]
 * after domain use cases run in the screen ViewModel.
 */
sealed interface ExerciseLibraryIntent {

    data class SetSearchQuery(val query: String) : ExerciseLibraryIntent

    data object DismissSessionBooking : ExerciseLibraryIntent

    data class BookingDateSelected(val dateMillis: Long) : ExerciseLibraryIntent

    data class BookingLocationSelected(val locationId: String) : ExerciseLibraryIntent

    data object BookingClearTimeSelection : ExerciseLibraryIntent

    data object LongSessionEdit : ExerciseLibraryIntent

    data object LongSessionProceedAnyway : ExerciseLibraryIntent

    data object DismissAddExerciseSuccess : ExerciseLibraryIntent

    data class SelectExerciseForDetail(val exerciseId: String) : ExerciseLibraryIntent

    data object ClearExerciseDetail : ExerciseLibraryIntent

    data object ToggleCartExpanded : ExerciseLibraryIntent

    data class ToggleFocusStripRegionGroup(val regionGroup: RegionGroup) : ExerciseLibraryIntent

    data class ToggleFocusStripRegionBody(
        val regionGroup: RegionGroup,
        val regionBody: RegionBody,
    ) : ExerciseLibraryIntent

    data class ToggleFocusStripMuscle(
        val regionGroup: RegionGroup,
        val regionBody: RegionBody,
        val muscle: Muscle,
    ) : ExerciseLibraryIntent

    data object ClearFocusStripSelection : ExerciseLibraryIntent
}
