package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.repository.MealRepository
import java.time.LocalDate
import javax.inject.Inject

class GetMealDaysUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {

    /**
     * Returns dates that have at least one meal, newest first.
     */
    suspend operator fun invoke(): List<LocalDate> =
        mealRepository.getDaysWithMeals()
}
