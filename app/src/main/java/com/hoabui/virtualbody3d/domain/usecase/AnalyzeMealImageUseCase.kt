package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.nutrition.MealAnalysis
import com.hoabui.virtualbody3d.domain.repository.MealRepository
import javax.inject.Inject

class AnalyzeMealImageUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {

    /**
     * Analyzes the image at [imageUrl] for meal recognition and macro estimation.
     */
    suspend operator fun invoke(imageUrl: String): MealAnalysis =
        mealRepository.analyzeMeal(imageUrl)
}

