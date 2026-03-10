package com.hoabui.virtualbody3d.ui.mealcapture

import android.net.Uri
import com.hoabui.virtualbody3d.domain.model.ExtractedData

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

/**
 * Temporary mapper from [ExtractedData] MEAL results into [MealPageUiModel].
 *
 * For now, we derive simple display strings from [ExtractedData.rawLines] so the UI
 * has something meaningful to show while the real meal schema is not yet defined.
 */
fun ExtractedData.toMealPageUiModel(
    id: String,
    imageUri: Uri
): MealPageUiModel {
    val safeLines = rawLines.ifEmpty { listOf("Meal recognized (placeholder)") }

    val firstLine = safeLines.firstOrNull().orEmpty()
    val remaining = if (safeLines.size > 1) safeLines.drop(1) else emptyList()

    val title = firstLine.ifBlank { "Recognized meal" }
    val caloriesText = remaining.firstOrNull().orEmpty()
    val macroSummaryText = remaining.drop(1).joinToString(separator = "\n")

    return MealPageUiModel(
        id = id,
        imageUri = imageUri,
        title = title,
        caloriesText = caloriesText,
        macroSummaryText = macroSummaryText,
        rawLines = safeLines
    )
}

