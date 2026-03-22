package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.nutrition.NutritionSummary
import com.hoabui.virtualbody3d.domain.repository.BodyNutritionSummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCaloriesTodayUseCase @Inject constructor(
    private val bodyNutritionSummaryRepository: BodyNutritionSummaryRepository
) {
    operator fun invoke(): Flow<NutritionSummary> = flow {
        emit(bodyNutritionSummaryRepository.getNutritionSummary())
    }
}
