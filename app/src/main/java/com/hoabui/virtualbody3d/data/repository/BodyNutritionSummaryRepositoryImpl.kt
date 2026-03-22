package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.NutritionSummaryLocalDataSource
import com.hoabui.virtualbody3d.data.mapper.toDomain
import com.hoabui.virtualbody3d.domain.model.nutrition.NutritionSummary
import com.hoabui.virtualbody3d.domain.repository.BodyNutritionSummaryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BodyNutritionSummaryRepositoryImpl @Inject constructor(
    private val localDataSource: NutritionSummaryLocalDataSource
) : BodyNutritionSummaryRepository {
    override fun getNutritionSummary(): NutritionSummary {
        return localDataSource.getNutritionSummary().toDomain()
    }
}
