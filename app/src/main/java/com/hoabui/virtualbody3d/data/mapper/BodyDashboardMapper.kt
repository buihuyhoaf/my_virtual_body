package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.BodyDashboardDto
import com.hoabui.virtualbody3d.domain.model.BodyDashboard
import com.hoabui.virtualbody3d.domain.model.NutritionSummary

fun BodyDashboardDto.toDomain(): BodyDashboard {
    return BodyDashboard(
        nutrition = NutritionSummary(
            intake = nutrition.intake,
            burned = nutrition.burned,
            goal = nutrition.goal
        )
    )
}
