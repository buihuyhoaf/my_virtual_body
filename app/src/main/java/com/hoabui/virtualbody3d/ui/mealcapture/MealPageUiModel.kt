package com.hoabui.virtualbody3d.ui.mealcapture

import android.net.Uri
import com.hoabui.virtualbody3d.domain.model.nutrition.MealAnalysis
import androidx.core.net.toUri

/**
 * Dominant macro for UI accents (e.g. meal cards), derived from gram totals.
 */
enum class MealMacroGroup {
    Protein,
    Carb,
    Fat,
}

/**
 * UI model representing a single analyzed meal page in the vertical pager.
 * Newest entries should appear first in the list.
 */
data class MealPageUiModel(
    val id: String,
    val imageUri: Uri,
    val title: String,
    val caloriesKcal: Int,
    val caloriesText: String,
    val macroSummaryText: String,
    val rawLines: List<String>,
    val dominantMacro: MealMacroGroup,
)

fun MealAnalysis.toMealPageUiModel(
    imageUri: Uri
): MealPageUiModel {
    val title = name.ifBlank { "Meal" }

    val kcal = caloriesKcal ?: 0
    val caloriesText = if (caloriesKcal != null) "$caloriesKcal kcal" else "Calories: -"

    val macroSummaryText = buildString {
        appendLine("Protein: ${proteinGrams ?: 0f} g")
        appendLine("Carbs: ${carbsGrams ?: 0f} g")
        append("Fat: ${fatGrams ?: 0f} g")
    }

    return MealPageUiModel(
        id = id,
        imageUri = imageUri,
        title = title,
        caloriesKcal = kcal,
        caloriesText = caloriesText,
        macroSummaryText = macroSummaryText,
        rawLines = rawLines,
        dominantMacro = dominantMacroFromGrams(proteinGrams, carbsGrams, fatGrams),
    )
}

private fun dominantMacroFromGrams(
    proteinGrams: Float?,
    carbsGrams: Float?,
    fatGrams: Float?,
): MealMacroGroup {
    val p = proteinGrams ?: 0f
    val c = carbsGrams ?: 0f
    val f = fatGrams ?: 0f
    return when {
        p >= c && p >= f -> MealMacroGroup.Protein
        c >= p && c >= f -> MealMacroGroup.Carb
        else -> MealMacroGroup.Fat
    }
}

/**
 * Maps [MealAnalysis] to [MealPageUiModel] when loaded from API (uses [MealAnalysis.imageUrl] or placeholder).
 */
fun MealAnalysis.toMealPageUiModelFromApi(): MealPageUiModel {
    val imageUri = imageUrl?.toUri() ?: Uri.EMPTY
    return toMealPageUiModel(imageUri)
}

