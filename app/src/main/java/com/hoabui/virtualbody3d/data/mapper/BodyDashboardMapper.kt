package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.BodyDashboardDto
import com.hoabui.virtualbody3d.data.model.DashboardSummaryTypeDto
import com.hoabui.virtualbody3d.domain.model.BodyDashboard
import com.hoabui.virtualbody3d.domain.model.DashboardSummary
import com.hoabui.virtualbody3d.domain.model.DashboardSummaryType
import com.hoabui.virtualbody3d.domain.model.Meal
import com.hoabui.virtualbody3d.domain.model.NutritionSummary

fun BodyDashboardDto.toDomain(): BodyDashboard {
    return BodyDashboard(
        dateLabel = dateLabel,
        nutrition = NutritionSummary(
            intake = nutrition.intake,
            burned = nutrition.burned,
            goal = nutrition.goal
        ),
        meals = meals.map { meal ->
            Meal(name = meal.name, calories = meal.calories)
        },
        summaries = summaries.map { summary ->
            DashboardSummary(
                type = summary.type.toDomain(),
                value = summary.value,
                subtitle = summary.subtitle,
                progress = summary.progress
            )
        }
    )
}

private fun DashboardSummaryTypeDto.toDomain(): DashboardSummaryType {
    return when (this) {
        DashboardSummaryTypeDto.Workout -> DashboardSummaryType.Workout
        DashboardSummaryTypeDto.Sleep -> DashboardSummaryType.Sleep
    }
}
