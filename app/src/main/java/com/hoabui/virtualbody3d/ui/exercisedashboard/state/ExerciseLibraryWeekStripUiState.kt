package com.hoabui.virtualbody3d.ui.exercisedashboard.state

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface ExerciseLibraryWeekStripUiState {
    data object Loading : ExerciseLibraryWeekStripUiState

    @Immutable
    data class Loaded(
        val days: ImmutableList<WeekStripDayUiModel>,
    ) : ExerciseLibraryWeekStripUiState

    data class Error(val message: String) : ExerciseLibraryWeekStripUiState
}
