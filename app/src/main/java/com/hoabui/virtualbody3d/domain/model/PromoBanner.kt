package com.hoabui.virtualbody3d.domain.model

/**
 * Domain model for a promotional or informational banner.
 * UI layer maps to [com.hoabui.virtualbody3d.ui.body.components.PromoBannerItem] (e.g. parse hex to Color).
 */
data class PromoBanner(
    val id: String,
    val backgroundImageResId: Int? = null,
    val gradientColorHexList: List<String>? = null
)
