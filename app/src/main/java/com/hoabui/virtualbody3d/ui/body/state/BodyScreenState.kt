package com.hoabui.virtualbody3d.ui.body.state

import com.hoabui.virtualbody3d.domain.model.BodyScanResult
import com.hoabui.virtualbody3d.domain.model.PromoBanner

data class BodyScreenState(
    val scanResult: BodyScanResult,
    val caloriesToday: CaloriesTodayUiState,
    val promoBanners: List<PromoBanner> = emptyList()
)
