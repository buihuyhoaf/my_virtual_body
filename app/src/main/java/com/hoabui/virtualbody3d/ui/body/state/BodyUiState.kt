package com.hoabui.virtualbody3d.ui.body.state

data class BodyUiState(
    val height: String = "",
    val weight: String = "",
    val weightProgress: Float? = null,
    val bodyFat: String = "",
    val bodyFatProgress: Float? = null,
    val muscleMass: String = "",
    val muscleMassProgress: Float? = null,
    val bmi: String = "",
    val bmiStatus: String? = null,
    val bmiScalePosition: Float? = null
)
