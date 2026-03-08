package com.hoabui.virtualbody3d.ui.body.mapper

import androidx.compose.ui.graphics.Color
import com.hoabui.virtualbody3d.domain.model.PromoBanner
import com.hoabui.virtualbody3d.ui.body.components.PromoBannerItem

fun PromoBanner.toPromoBannerItem(): PromoBannerItem = PromoBannerItem(
    title = title,
    subtitle = subtitle,
    onClick = null,
    backgroundImageRes = backgroundImageResId,
    backgroundGradientColors = gradientColorHexList?.map { hex ->
        Color(android.graphics.Color.parseColor(hex))
    }
)
