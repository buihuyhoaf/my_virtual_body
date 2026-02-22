package com.hoabui.virtualbody3d.ui.body.state

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.BodyDashboard
import com.hoabui.virtualbody3d.domain.model.DashboardSummaryType

@Immutable
data class BodyDashboardUiState(
    val dateLabel: String = "",
    val nutrition: NutritionSummaryUiState = NutritionSummaryUiState(),
    val meals: List<MealUiState> = emptyList(),
    val summaries: List<SummaryCardUiState> = emptyList()
)

@Immutable
data class NutritionSummaryUiState(
    val intake: Int = 0,
    val burned: Int = 0,
    val goal: Int = 1
)

@Immutable
data class MealUiState(
    val name: String,
    val calories: Int
)

@Immutable
data class SummaryCardUiState(
    val type: SummaryCardType,
    val value: String,
    val subtitle: String,
    val progress: Float
)

enum class SummaryCardType {
    Workout,
    Sleep
}

fun BodyDashboard.toUiState(): BodyDashboardUiState {
    return BodyDashboardUiState(
        dateLabel = dateLabel,
        nutrition = NutritionSummaryUiState(
            intake = nutrition.intake,
            burned = nutrition.burned,
            goal = nutrition.goal
        ),
        meals = meals.map { meal ->
            MealUiState(name = meal.name, calories = meal.calories)
        },
        summaries = summaries.map { summary ->
            SummaryCardUiState(
                type = summary.type.toUi(),
                value = summary.value,
                subtitle = summary.subtitle,
                progress = summary.progress
            )
        }
    )
}

private fun DashboardSummaryType.toUi(): SummaryCardType {
    return when (this) {
        DashboardSummaryType.Workout -> SummaryCardType.Workout
        DashboardSummaryType.Sleep -> SummaryCardType.Sleep
    }
}
