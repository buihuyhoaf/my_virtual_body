package com.hoabui.virtualbody3d.ui.exerciselibrary.state.mvi

import com.hoabui.virtualbody3d.domain.model.exercise.Muscle
import com.hoabui.virtualbody3d.domain.model.exercise.RegionBody
import com.hoabui.virtualbody3d.domain.model.exercise.RegionGroup
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.CartSetField
import java.time.LocalTime

/**
 * User-driven intents. Cart and slot-toggle mutations are handled via [ExerciseLibraryUpdate]
 * after domain use cases run in the screen ViewModel.
 */
sealed interface ExerciseLibraryIntent {

    data object OpenSessionBooking : ExerciseLibraryIntent

    data class SetSearchQuery(val query: String) : ExerciseLibraryIntent

    data class ExerciseClicked(val exerciseId: String) : ExerciseLibraryIntent

    data class LibraryListToggle(val exerciseId: String) : ExerciseLibraryIntent

    data class DetailAddToCart(val exerciseId: String) : ExerciseLibraryIntent

    data class SelectCartItem(val exerciseId: String) : ExerciseLibraryIntent

    data class RemoveCartItem(val exerciseId: String) : ExerciseLibraryIntent

    data object ClearCart : ExerciseLibraryIntent

    data class StepCartField(
        val exerciseId: String,
        val setIndex: Int,
        val field: CartSetField,
        val delta: Int,
    ) : ExerciseLibraryIntent

    data class SetCartFieldManual(
        val exerciseId: String,
        val setIndex: Int,
        val field: CartSetField,
        val value: String,
    ) : ExerciseLibraryIntent

    data object DismissSessionBooking : ExerciseLibraryIntent

    data class BookingDateSelected(val dateMillis: Long) : ExerciseLibraryIntent

    data class BookingLocationSelected(val locationId: String) : ExerciseLibraryIntent

    data class BookingSlotToggled(val slotStart: LocalTime) : ExerciseLibraryIntent

    data object BookingClearTimeSelection : ExerciseLibraryIntent

    data object ConfirmSessionBooking : ExerciseLibraryIntent

    data object LongSessionEdit : ExerciseLibraryIntent

    data object LongSessionProceedAnyway : ExerciseLibraryIntent

    data object DismissAddExerciseSuccess : ExerciseLibraryIntent

    data class SelectExerciseForDetail(val exerciseId: String) : ExerciseLibraryIntent

    data object ClearExerciseDetail : ExerciseLibraryIntent

    data object ToggleCartExpanded : ExerciseLibraryIntent

    data class FocusStripQuadrantTapped(val index: Int) : ExerciseLibraryIntent

    data object ConfirmSelectionBarEdit : ExerciseLibraryIntent

    data object CancelSelectionBarEdit : ExerciseLibraryIntent

    data class StartSelectionBarEditFromScheduleRow(val scheduleRowId: Long) : ExerciseLibraryIntent

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
