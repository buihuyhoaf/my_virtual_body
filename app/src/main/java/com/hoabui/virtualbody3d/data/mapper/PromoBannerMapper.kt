package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.PromoBannerDto
import com.hoabui.virtualbody3d.domain.model.PromoBanner

fun PromoBannerDto.toDomain(): PromoBanner = PromoBanner(
    id = id,
    backgroundImageResId = backgroundImageResId,
    backgroundImageResUrl = backgroundImageResUrl,
    gradientColorHexList = gradientColorHexList
)
