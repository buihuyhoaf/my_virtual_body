package com.hoabui.virtualbody3d.ui.body.data

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.hoabui.virtualbody3d.domain.model.nutrition.NutritionSummary
import com.hoabui.virtualbody3d.domain.model.content.PromoBanner

fun PromoBanner.toPromoBannerItem(): PromoBannerItem = PromoBannerItem(
    onClick = null,
    backgroundImageRes = backgroundImageResId,
    backgroundImageResUrl = backgroundImageResUrl,
    backgroundGradientColors = gradientColorHexList?.map { hex ->
        Color(hex.toColorInt())
    }
)

fun NutritionSummary.toCalorieGoalUiModel(): CalorieGoalUiModel {
    return CalorieGoalUiModel(
        intake = intake,
        burned = burned,
        intakeGoal = goal,
        burnGoal = goal
    )
}
