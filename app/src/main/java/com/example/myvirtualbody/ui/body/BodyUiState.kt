package com.example.myvirtualbody.ui.body

/**
 * UI state for Body Analysis Screen.
 * Follows clean architecture pattern - pure data class with no business logic.
 *
 * Progress values are 0f..1f for progress bars. bmiStatus e.g. "Normal", "Underweight", "Overweight".
 */
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
