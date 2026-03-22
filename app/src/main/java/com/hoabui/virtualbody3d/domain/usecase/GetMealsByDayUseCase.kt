package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.nutrition.MealAnalysis
import com.hoabui.virtualbody3d.domain.repository.MealRepository
import java.time.LocalDate
import javax.inject.Inject

class GetMealsByDayUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {

    /**
     * Returns all meals for the given day.
     */
    suspend operator fun invoke(day: LocalDate): List<MealAnalysis> =
        mealRepository.getMealsByDay(day)
}
