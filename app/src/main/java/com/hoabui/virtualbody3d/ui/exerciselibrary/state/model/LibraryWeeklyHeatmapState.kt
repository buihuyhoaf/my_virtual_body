package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

/**
 * UI state for the weekly activity heatmap card in the Exercise Library screen.
 */
@Immutable
sealed interface LibraryWeeklyHeatmapState {
    data object Loading : LibraryWeeklyHeatmapState

    data class Loaded(
        val days: ImmutableList<WeeklyHeatmapDayUiModel>,
    ) : LibraryWeeklyHeatmapState

    data class Error(val message: String) : LibraryWeeklyHeatmapState
}
