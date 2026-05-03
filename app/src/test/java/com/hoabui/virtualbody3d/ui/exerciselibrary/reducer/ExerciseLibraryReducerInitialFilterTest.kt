package com.hoabui.virtualbody3d.ui.exerciselibrary.reducer

import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.LibraryFilterState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.mvi.ExerciseLibraryIntent
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.mvi.ExerciseLibraryUpdate
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.reducer.ExerciseLibraryReducer
import kotlinx.collections.immutable.persistentSetOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ExerciseLibraryReducerInitialFilterTest {

    private val reducer = ExerciseLibraryReducer()

    private fun reduceIntent(state: ExerciseLibraryUiState, intent: ExerciseLibraryIntent): ExerciseLibraryUiState =
        reducer.reduce(state, ExerciseLibraryUpdate.UserIntent(intent))

    @Test
    fun setInitialExerciseCategory_clearsBodyRegions_andSetsCategory() {
        val regions = persistentSetOf(BodyRegion.Legs, BodyRegion.Arms)
        val state =
            ExerciseLibraryUiState(filters = LibraryFilterState(selectedBodyRegions = regions))
        val next =
            reduceIntent(state, ExerciseLibraryIntent.SetInitialExerciseCategoryFilter(ExerciseCategory.Strength))
        assertEquals(ExerciseCategory.Strength, next.filters.selectedExerciseCategory)
        assertNull(next.filters.selectedBodyRegions)
    }

    @Test
    fun setInitialBodyRegionFilter_clearsCategory_andSetsRegions() {
        val state =
            ExerciseLibraryUiState(filters = LibraryFilterState(selectedExerciseCategory = ExerciseCategory.Cardio))
        val regions = persistentSetOf(BodyRegion.Chest, BodyRegion.Shoulders)
        val next =
            reduceIntent(state, ExerciseLibraryIntent.SetInitialBodyRegionFilter(regions))
        assertNull(next.filters.selectedExerciseCategory)
        assertEquals(regions, next.filters.selectedBodyRegions)
    }
}
