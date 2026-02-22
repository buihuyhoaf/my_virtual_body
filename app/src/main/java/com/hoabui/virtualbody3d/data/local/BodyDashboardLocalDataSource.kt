package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.model.BodyDashboardDto
import com.hoabui.virtualbody3d.data.model.DashboardSummaryDto
import com.hoabui.virtualbody3d.data.model.DashboardSummaryTypeDto
import com.hoabui.virtualbody3d.data.model.MealDto
import com.hoabui.virtualbody3d.data.model.NutritionSummaryDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BodyDashboardLocalDataSource @Inject constructor() {
    fun getBodyDashboard(): BodyDashboardDto {
        return BodyDashboardDto(
            dateLabel = "Today • Fri, 20 Feb",
            nutrition = NutritionSummaryDto(
                intake = 2100,
                burned = 680,
                goal = 2400
            ),
            meals = listOf(
                MealDto(name = "Quinoa Power Bowl", calories = 420),
                MealDto(name = "Greek Yogurt", calories = 250),
                MealDto(name = "Salmon & Rice", calories = 640)
            ),
            summaries = listOf(
                DashboardSummaryDto(
                    type = DashboardSummaryTypeDto.Workout,
                    value = "45 min",
                    subtitle = "Strength session",
                    progress = 0.72f
                ),
                DashboardSummaryDto(
                    type = DashboardSummaryTypeDto.Sleep,
                    value = "7h 40m",
                    subtitle = "Recovery score 84%",
                    progress = 0.84f
                )
            )
        )
    }
}
