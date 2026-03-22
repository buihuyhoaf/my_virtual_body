package com.hoabui.virtualbody3d.domain.model.nutrition

/**
 * Domain model representing the result of meal analysis for a single image.
 */
data class MealAnalysis(
    val id: String,
    val name: String,
    val caloriesKcal: Int?,
    val proteinGrams: Float?,
    val carbsGrams: Float?,
    val fatGrams: Float?,
    val servingSizeText: String? = null,
    val notes: String? = null,
    val rawLines: List<String> = emptyList(),
    /** Optional image URL when meal is loaded from API (e.g. getMealsByDay). */
    val imageUrl: String? = null
)
