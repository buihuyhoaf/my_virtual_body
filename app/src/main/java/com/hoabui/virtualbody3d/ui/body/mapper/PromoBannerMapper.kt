package com.hoabui.virtualbody3d.ui.body.mapper

import androidx.compose.ui.graphics.Color
import com.hoabui.virtualbody3d.domain.model.PromoBanner
import com.hoabui.virtualbody3d.ui.body.components.PromoBannerItem
import androidx.core.graphics.toColorInt

fun PromoBanner.toPromoBannerItem(): PromoBannerItem = PromoBannerItem(
    onClick = null,
    backgroundImageRes = backgroundImageResId,
    backgroundGradientColors = gradientColorHexList?.map { hex ->
        Color(hex.toColorInt())
    }
)
