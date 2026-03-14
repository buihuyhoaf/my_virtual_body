package com.hoabui.virtualbody3d.ui.body.state

import com.hoabui.virtualbody3d.domain.model.BodyScanResult
import com.hoabui.virtualbody3d.domain.model.PromoBanner
import com.hoabui.virtualbody3d.ui.body.data.NutritionSummaryUiState

data class BodyScreenState(
    val scanResult: BodyScanResult,
    val nutritionToday: NutritionSummaryUiState,
    val promoBanners: List<PromoBanner> = emptyList()
)
