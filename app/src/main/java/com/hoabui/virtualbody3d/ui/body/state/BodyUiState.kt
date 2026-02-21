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

data class FoodLogItem(
    val name: String,
    val calories: Int,
    val protein: Int,
    val carb: Int,
    val fat: Int,
    val mealType: String
)

data class ActivityItem(
    val name: String,
    val duration: String,
    val intensityLabel: String,
    val calories: Int,
    val intensity: Float
)
