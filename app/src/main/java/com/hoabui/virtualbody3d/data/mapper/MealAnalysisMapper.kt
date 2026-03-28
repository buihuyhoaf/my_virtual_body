package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.MealAnalysisDto
import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.domain.model.nutrition.MealAnalysis

fun MealAnalysisDto.toDomain(): MealAnalysis = MealAnalysis(
    id = id,
    name = name,
    caloriesKcal = caloriesKcal,
    proteinGrams = proteinGrams,
    carbsGrams = carbsGrams,
    fatGrams = fatGrams,
    servingSizeText = servingSizeText,
    notes = notes,
    rawLines = rawLines,
    image = toMealImageSource(),
)

private fun MealAnalysisDto.toMealImageSource(): ImageSource {
    imageUrl?.takeIf { it.isNotBlank() }?.let { return ImageSource.Network(it) }
    return ImageSource.LocalResource(MEAL_IMAGE_FALLBACK_NAME)
}

private const val MEAL_IMAGE_FALLBACK_NAME = "body_unsplash"
