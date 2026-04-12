package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import androidx.compose.runtime.Immutable

/**
 * UI model for a single day cell in the weekly activity heatmap card.
 *
 * @param dayLabel Short Vietnamese day label (e.g. "T2" for Monday, "CN" for Sunday).
 * @param dayOfMonth Calendar day of the month (1–31).
 * @param densityLevel Intensity bucket: 0 = no sessions, 1 = 1 session, 2 = 2 sessions, 3 = 3+.
 * @param isToday Whether this day is today's date (triggers highlight border).
 */
@Immutable
data class WeeklyHeatmapDayUiModel(
    val dayLabel: String,
    val dayOfMonth: Int,
    val densityLevel: Int,
    val isToday: Boolean,
)
