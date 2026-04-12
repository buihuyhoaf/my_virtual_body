package com.hoabui.virtualbody3d.ui.exerciselibrary.state.reducer

import com.hoabui.virtualbody3d.ui.exerciselibrary.data.CommitLibrarySessionBookingSuccessUiMapper
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.LibraryWeeklyHeatmapState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.WeeklyHeatmapDayUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.mvi.ExerciseLibraryUpdate
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Test

class ExerciseLibraryReducerMonthlySummaryTest {

    private val reducer = ExerciseLibraryReducer(CommitLibrarySessionBookingSuccessUiMapper())

    @Test
    fun weeklyHeatmapLoaded_updatesOnlyWeeklyHeatmapSlice() {
        val initial = ExerciseLibraryUiState()
        val days = persistentListOf(
            WeeklyHeatmapDayUiModel(dayLabel = "T2", dayOfMonth = 7, densityLevel = 0, isToday = false),
            WeeklyHeatmapDayUiModel(dayLabel = "T3", dayOfMonth = 8, densityLevel = 1, isToday = false),
            WeeklyHeatmapDayUiModel(dayLabel = "T4", dayOfMonth = 9, densityLevel = 2, isToday = false),
            WeeklyHeatmapDayUiModel(dayLabel = "T5", dayOfMonth = 10, densityLevel = 3, isToday = false),
            WeeklyHeatmapDayUiModel(dayLabel = "T6", dayOfMonth = 11, densityLevel = 0, isToday = false),
            WeeklyHeatmapDayUiModel(dayLabel = "T7", dayOfMonth = 12, densityLevel = 1, isToday = true),
            WeeklyHeatmapDayUiModel(dayLabel = "CN", dayOfMonth = 13, densityLevel = 0, isToday = false),
        )
        val loaded = LibraryWeeklyHeatmapState.Loaded(days = days)
        val next = reducer.reduce(
            initial,
            ExerciseLibraryUpdate.WeeklyHeatmapLoaded(loaded),
        )
        assertEquals(loaded, next.weeklyHeatmap)
        assertEquals(initial.filters, next.filters)
        assertEquals(initial.cart, next.cart)
    }

    @Test
    fun weeklyHeatmapLoaded_errorState_updatesOnlyWeeklyHeatmapSlice() {
        val initial = ExerciseLibraryUiState()
        val error = LibraryWeeklyHeatmapState.Error("Network error")
        val next = reducer.reduce(
            initial,
            ExerciseLibraryUpdate.WeeklyHeatmapLoaded(error),
        )
        assertEquals(error, next.weeklyHeatmap)
        assertEquals(initial.filters, next.filters)
        assertEquals(initial.cart, next.cart)
    }
}
