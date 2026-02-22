package com.hoabui.virtualbody3d.ui.body.state

data class BodyUiState(
    val height: String = "",
    val heightUnit: String = "cm",
    val weight: String = "",
    val weightUnit: String = "kg",
    val weightProgress: Float? = null,
    val bodyFat: String = "",
    val bodyFatProgress: Float? = null,
    val muscleMass: String = "",
    val muscleMassUnit: String = "kg",
    val muscleMassProgress: Float? = null,
    val bmi: String = "",
    val bmiStatus: String? = null,
    val bmiScalePosition: Float? = null
)
