package com.hoabui.virtualbody3d.ui.workoutfeed.state

import com.hoabui.virtualbody3d.domain.model.WorkoutFeedItem

/**
 * UI state for the workout feed screen.
 * Holds domain models only; no UI logic.
 */
data class WorkoutFeedUiState(
    val feedItems: List<WorkoutFeedItem> = emptyList()
)
