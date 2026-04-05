package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.nutrition.NutritionSummary
import kotlinx.coroutines.flow.Flow

interface BodyNutritionSummaryRepository {
    fun observeNutritionSummary(): Flow<NutritionSummary>
}
