package com.hoabui.virtualbody3d.data.model

data class BodyDashboardDto(
    val dateLabel: String,
    val nutrition: NutritionSummaryDto,
    val meals: List<MealDto>,
    val summaries: List<DashboardSummaryDto>
)

data class NutritionSummaryDto(
    val intake: Int,
    val burned: Int,
    val goal: Int
)

data class MealDto(
    val name: String,
    val calories: Int
)

data class DashboardSummaryDto(
    val type: DashboardSummaryTypeDto,
    val value: String,
    val subtitle: String,
    val progress: Float
)

enum class DashboardSummaryTypeDto {
    Workout,
    Sleep
}
