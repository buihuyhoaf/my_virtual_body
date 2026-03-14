package com.hoabui.virtualbody3d.data.model

data class MealAnalysisDto(
    val id: String,
    val name: String,
    val caloriesKcal: Int?,
    val proteinGrams: Float?,
    val carbsGrams: Float?,
    val fatGrams: Float?,
    val servingSizeText: String? = null,
    val notes: String? = null,
    val rawLines: List<String> = emptyList(),
    val imageUrl: String? = null
)
