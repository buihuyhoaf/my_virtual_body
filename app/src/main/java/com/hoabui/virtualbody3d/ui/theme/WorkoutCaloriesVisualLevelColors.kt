package com.hoabui.virtualbody3d.ui.theme

import androidx.compose.ui.graphics.Color
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCaloriesVisualLevel
import com.hoabui.virtualbody3d.ui.theme.tokens.GymToken

/**
 * Semantic color for per-set / per-card calorie emphasis; matches workout calendar list styling.
 */
fun WorkoutCaloriesVisualLevel.toCaloriesVisualLevelColor(token: GymToken): Color = when (this) {
    WorkoutCaloriesVisualLevel.Low -> token.colors.textSecondary
    WorkoutCaloriesVisualLevel.Medium -> token.colors.warning
    WorkoutCaloriesVisualLevel.High -> token.colors.error
}
