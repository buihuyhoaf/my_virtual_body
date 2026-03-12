package com.hoabui.virtualbody3d.data.model

data class BodyDashboardDto(
    val nutrition: NutritionSummaryDto
)

data class NutritionSummaryDto(
    val intake: Int,
    val burned: Int,
    val goal: Int
)
