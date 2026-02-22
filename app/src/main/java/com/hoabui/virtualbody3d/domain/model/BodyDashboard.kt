package com.hoabui.virtualbody3d.domain.model

data class BodyDashboard(
    val dateLabel: String,
    val nutrition: NutritionSummary,
    val meals: List<Meal>,
    val summaries: List<DashboardSummary>
)

data class NutritionSummary(
    val intake: Int,
    val burned: Int,
    val goal: Int
)

data class Meal(
    val name: String,
    val calories: Int
)

data class DashboardSummary(
    val type: DashboardSummaryType,
    val value: String,
    val subtitle: String,
    val progress: Float
)

enum class DashboardSummaryType {
    Workout,
    Sleep
}
