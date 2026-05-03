package com.hoabui.virtualbody3d.ui.exercisedashboard.state

import androidx.compose.runtime.Immutable

@Immutable
data class DashboardAchievementUiModel(
    val anchorEpochDay: Long,
    val exerciseTitlesLine: String,
    val totalKcal: Int,
    val durationMinutes: Int,
)
