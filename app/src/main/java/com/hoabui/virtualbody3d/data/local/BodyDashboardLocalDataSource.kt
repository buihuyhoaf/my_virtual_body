package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.model.BodyDashboardDto
import com.hoabui.virtualbody3d.data.model.NutritionSummaryDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BodyDashboardLocalDataSource @Inject constructor() {
    fun getBodyDashboard(): BodyDashboardDto {
        return BodyDashboardDto(
            nutrition = NutritionSummaryDto(
                intake = 2100,
                burned = 680,
                goal = 2400
            )
        )
    }
}
