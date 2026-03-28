package com.hoabui.virtualbody3d.ui.body.state

/**
 * WHO-style BMI bucket derived from scan [currentValue] for dashboard copy and accent colors.
 */
enum class BodyBmiCategory {
    UNKNOWN,
    UNDERWEIGHT,
    NORMAL,
    OVERWEIGHT,
    OBESE,
}

data class BodyUiState(
    val height: String = "",
    val weight: String = "",
    val weightProgress: Float? = null,
    val bodyFat: String = "",
    val bodyFatProgress: Float? = null,
    val muscleMass: String = "",
    val muscleMassProgress: Float? = null,
    val bmi: String = "",
    val bmiCategory: BodyBmiCategory = BodyBmiCategory.UNKNOWN,
    val bmiScalePosition: Float? = null
)
