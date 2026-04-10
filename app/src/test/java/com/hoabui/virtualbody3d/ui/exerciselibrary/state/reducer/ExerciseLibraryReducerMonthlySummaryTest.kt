package com.hoabui.virtualbody3d.ui.exerciselibrary.state.reducer

import com.hoabui.virtualbody3d.ui.exerciselibrary.data.CommitLibrarySessionBookingSuccessUiMapper
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.LibraryMonthlySummaryState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.mvi.ExerciseLibraryUpdate
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.YearMonth

class ExerciseLibraryReducerMonthlySummaryTest {

    private val reducer = ExerciseLibraryReducer(CommitLibrarySessionBookingSuccessUiMapper())

    @Test
    fun monthlySummaryLoaded_updatesOnlyMonthlySlice() {
        val initial = ExerciseLibraryUiState()
        val loaded = LibraryMonthlySummaryState.Loaded(
            yearMonth = YearMonth.of(2026, 4),
            workoutDayCount = 3,
            restDayCount = 27,
        )
        val next = reducer.reduce(
            initial,
            ExerciseLibraryUpdate.MonthlySummaryLoaded(loaded),
        )
        assertEquals(loaded, next.monthlySummary)
        assertEquals(initial.filters, next.filters)
        assertEquals(initial.cart, next.cart)
    }
}
