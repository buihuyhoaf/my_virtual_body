package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.model.NutritionSummaryDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NutritionSummaryLocalDataSource @Inject constructor() {

    fun getNutritionSummary(): NutritionSummaryDto {
        return NutritionSummaryDto(
            intake = 2100,
            burned = 680,
            goal = 2400
        )
    }
}
