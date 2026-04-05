package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.local.db.NutritionSummaryDao
import com.hoabui.virtualbody3d.data.local.db.toNutritionSummaryDto
import com.hoabui.virtualbody3d.data.model.NutritionSummaryDto
import com.hoabui.virtualbody3d.di.IoDispatcher
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

@Singleton
class NutritionSummaryLocalDataSource @Inject constructor(
    private val nutritionSummaryDao: NutritionSummaryDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    fun observeNutritionSummary(): Flow<NutritionSummaryDto> =
        nutritionSummaryDao.observeActive()
            .map { entity ->
                requireNotNull(entity) { "nutrition_summary row missing (migration seed failed?)" }
                    .toNutritionSummaryDto()
            }
            .flowOn(ioDispatcher)
}
