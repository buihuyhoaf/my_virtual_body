package com.hoabui.virtualbody3d.domain.model.content

/**
 * Domain model for a supplement: name, main nutrient/mineral, and image resource.
 */
data class Supplement(
    val id: String,
    val name: String,
    val nutrient: String,
    val imageResId: Int
)
