package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.nutrition.NutritionSummary

interface BodyNutritionSummaryRepository {
    fun getNutritionSummary(): NutritionSummary
}
