package com.hoabui.virtualbody3d.ui.body.state

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.BodyDashboard

@Immutable
data class CaloriesTodayUiState(
    val nutrition: NutritionSummaryUiState = NutritionSummaryUiState(),
    val meals: List<MealUiState> = emptyList()
)

fun BodyDashboard.toCaloriesTodayUiState(): CaloriesTodayUiState {
    return CaloriesTodayUiState(
        nutrition = NutritionSummaryUiState(
            intake = nutrition.intake,
            burned = nutrition.burned,
            goal = nutrition.goal
        ),
        meals = meals.map { MealUiState(name = it.name, calories = it.calories) }
    )
}
