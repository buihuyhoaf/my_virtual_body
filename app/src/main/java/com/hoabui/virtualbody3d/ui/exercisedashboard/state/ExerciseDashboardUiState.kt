package com.hoabui.virtualbody3d.ui.exercisedashboard.state

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class ExerciseDashboardUiState(
    val achievement: DashboardAchievementUiModel? = null,
    val coach: DashboardCoachUiModel,
    val categories: ImmutableList<DashboardCategoryUiModel> = persistentListOf(),
    val heatmap: ExerciseLibraryWeekStripUiState = ExerciseLibraryWeekStripUiState.Loading,
)
