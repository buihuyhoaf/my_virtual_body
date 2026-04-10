package com.hoabui.virtualbody3d.ui.exerciselibrary.presentation

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.LibraryCartState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.LibraryFilterState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ExerciseLibraryPresentationSlicesTest {

    @Test
    fun mergeExerciseLibraryPresentation_preservesCartFieldsAndOverlaysLibrary() {
        val base = ExerciseLibraryUiState(
            filters = LibraryFilterState(searchQuery = "squat"),
            cart = LibraryCartState(
                itemDrafts = persistentMapOf(),
                draftOrder = persistentListOf(),
            ),
        )
        val lib = LibraryPresentationSlice(
            sections = persistentListOf(),
            exerciseMeasurementById = persistentMapOf("a" to ExerciseMeasurementMode.Strength),
            selectedExerciseForDetail = null,
            isAddToSessionEnabled = true,
        )
        val merged = mergeExerciseLibraryPresentation(base, lib, sessionBookingUiModel = null)
        assertEquals("squat", merged.filters.searchQuery)
        assertEquals(lib.sections, merged.libraryList.sections)
        assertEquals(lib.exerciseMeasurementById, merged.libraryList.exerciseMeasurementById)
        assertEquals(true, merged.libraryList.isAddToSessionEnabled)
        assertNull(merged.sessionBooking.uiModel)
    }
}
