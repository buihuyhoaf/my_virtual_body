package com.hoabui.virtualbody3d.data.model

data class PromoBannerDto(
    val id: String,
    val backgroundImageResId: Int? = null,
    val backgroundImageResUrl: String? = null,
    val gradientColorHexList: List<String>? = null
)
