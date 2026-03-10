package com.hoabui.virtualbody3d.ui.mealcapture

import android.net.Uri
import com.hoabui.virtualbody3d.domain.model.MealAnalysis

/**
 * UI model representing a single analyzed meal page in the vertical pager.
 * Newest entries should appear first in the list.
 */
data class MealPageUiModel(
    val id: String,
    val imageUri: Uri,
    val title: String,
    val caloriesText: String,
    val macroSummaryText: String,
    val rawLines: List<String>
)

fun MealAnalysis.toMealPageUiModel(
    imageUri: Uri
): MealPageUiModel {
    val title = name.ifBlank { "Meal" }

    val caloriesText = caloriesKcal?.let { "$it kcal" } ?: "Calories: -"

    val macroSummaryText = buildString {
        appendLine("Protein: ${proteinGrams ?: 0f} g")
        appendLine("Carbs: ${carbsGrams ?: 0f} g")
        append("Fat: ${fatGrams ?: 0f} g")
    }

    return MealPageUiModel(
        id = id,
        imageUri = imageUri,
        title = title,
        caloriesText = caloriesText,
        macroSummaryText = macroSummaryText,
        rawLines = rawLines
    )
}

