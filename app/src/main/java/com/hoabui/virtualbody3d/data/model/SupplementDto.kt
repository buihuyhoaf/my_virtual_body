package com.hoabui.virtualbody3d.data.model

/**
 * Data layer DTO for a supplement (local/remote).
 */
data class SupplementDto(
    val id: String,
    val name: String,
    val nutrient: String,
    val imageResId: Int
)
