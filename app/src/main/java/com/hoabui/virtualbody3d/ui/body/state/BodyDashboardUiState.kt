package com.hoabui.virtualbody3d.ui.body.state

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.BodyDashboard

@Immutable
data class NutritionSummaryUiState(
    val intake: Int = 0,
    val burned: Int = 0,
    val goal: Int = 1
)

fun BodyDashboard.toNutritionSummaryUiState(): NutritionSummaryUiState {
    return NutritionSummaryUiState(
        intake = nutrition.intake,
        burned = nutrition.burned,
        goal = nutrition.goal
    )
}
