package com.hoabui.virtualbody3d.ui.exerciselibrary.presentation

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.LibraryPresentationSlice
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExerciseLibraryPresentationSlicesTest {

    @Test
    fun mergeExerciseLibraryPresentation_preservesCartFieldsAndOverlaysLibrary() {
        val base = ExerciseLibraryUiState(
            searchQuery = "squat",
            itemDrafts = persistentMapOf(),
            draftOrder = persistentListOf(),
        )
        val lib = LibraryPresentationSlice(
            sections = persistentListOf(),
            exerciseMeasurementById = persistentMapOf("a" to ExerciseMeasurementMode.Strength),
            isAddToSessionEnabled = true,
        )
        val merged = mergeExerciseLibraryPresentation(
            base = base,
            library = lib,
        )
        assertEquals("squat", merged.searchQuery)
        assertEquals(lib.sections, merged.libraryList.sections)
        assertEquals(lib.exerciseMeasurementById, merged.libraryList.exerciseMeasurementById)
        assertEquals(true, merged.libraryList.isAddToSessionEnabled)
    }
}
