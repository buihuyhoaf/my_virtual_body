package com.hoabui.virtualbody3d.ui.body.data

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.hoabui.virtualbody3d.domain.model.NutritionSummary
import com.hoabui.virtualbody3d.domain.model.PromoBanner

fun PromoBanner.toPromoBannerItem(): PromoBannerItem = PromoBannerItem(
    onClick = null,
    backgroundImageRes = backgroundImageResId,
    backgroundGradientColors = gradientColorHexList?.map { hex ->
        Color(hex.toColorInt())
    }
)

fun NutritionSummary.toNutritionSummaryUiState(): NutritionSummaryUiState {
    return NutritionSummaryUiState(
        intake = intake,
        burned = burned,
        goal = goal
    )
}
