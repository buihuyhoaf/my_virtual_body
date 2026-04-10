package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable
import java.time.YearMonth

@Immutable
sealed interface LibraryMonthlySummaryState {
    data object Loading : LibraryMonthlySummaryState

    data class Loaded(
        val yearMonth: YearMonth,
        val workoutDayCount: Int,
        val restDayCount: Int,
    ) : LibraryMonthlySummaryState

    data class Error(val message: String) : LibraryMonthlySummaryState
}
