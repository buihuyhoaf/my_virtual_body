package com.hoabui.virtualbody3d.domain.model

data class BodyDashboard(
    val nutrition: NutritionSummary
)

data class NutritionSummary(
    val intake: Int,
    val burned: Int,
    val goal: Int
)
