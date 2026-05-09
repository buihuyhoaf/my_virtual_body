package com.hoabui.virtualbody3d.ui.exerciselibrary.state

import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.CartSetField
import kotlinx.collections.immutable.ImmutableSet

sealed class ExerciseLibraryEvent {
    data class SearchQueryChanged(val query: String) : ExerciseLibraryEvent()
    data class CategoryFilterChanged(val category: ExerciseCategory) : ExerciseLibraryEvent()
    data class BodyRegionFilterChanged(val regions: ImmutableSet<BodyRegion>) : ExerciseLibraryEvent()
    data class CardSelectionToggled(val exerciseId: String) : ExerciseLibraryEvent()
    data class CartItemSelected(val exerciseId: String) : ExerciseLibraryEvent()
    data class CartItemRemoved(val exerciseId: String) : ExerciseLibraryEvent()
    object CartCleared : ExerciseLibraryEvent()
    data class CartFieldStepped(val exerciseId: String, val setIndex: Int, val field: CartSetField, val delta: Int) : ExerciseLibraryEvent()
    data class CartFieldManualSet(val exerciseId: String, val setIndex: Int, val field: CartSetField, val value: String) : ExerciseLibraryEvent()
    object CartExpandedToggled : ExerciseLibraryEvent()
    data class SelectionBarEditStarted(val scheduleRowId: Long) : ExerciseLibraryEvent()
    object SelectionBarEditCancelled : ExerciseLibraryEvent()
    object SelectionBarEditConfirmed : ExerciseLibraryEvent()
}
