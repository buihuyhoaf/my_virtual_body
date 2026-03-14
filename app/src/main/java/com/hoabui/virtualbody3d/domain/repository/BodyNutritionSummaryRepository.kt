package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.NutritionSummary

interface BodyNutritionSummaryRepository {
    fun getNutritionSummary(): NutritionSummary
}
