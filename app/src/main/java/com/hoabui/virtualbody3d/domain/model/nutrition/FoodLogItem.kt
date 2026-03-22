package com.hoabui.virtualbody3d.domain.model.nutrition

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
