package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.MealAnalysisDto
import com.hoabui.virtualbody3d.domain.model.MealAnalysis

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
    imageUrl = imageUrl
)
