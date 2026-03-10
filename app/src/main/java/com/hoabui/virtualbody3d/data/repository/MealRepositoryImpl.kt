package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.domain.model.MealAnalysis
import com.hoabui.virtualbody3d.domain.repository.MealRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepositoryImpl @Inject constructor() : MealRepository {

    override suspend fun analyzeMeal(imageUrl: String): MealAnalysis =
        withContext(Dispatchers.IO) {
            delay(SIMULATED_MEAL_ANALYSIS_MS)

            // Fake but realistic-looking meal analysis result.
            val id = UUID.randomUUID().toString()
            val name = "Grilled chicken with rice"
            val caloriesKcal = 560
            val proteinGrams = 35f
            val carbsGrams = 55f
            val fatGrams = 18f
            val servingSizeText = "1 plate"

            val rawLines = listOf(
                "Meal: $name",
                "Serving: $servingSizeText",
                "Calories: $caloriesKcal kcal",
                "Protein: ${proteinGrams} g",
                "Carbs: ${carbsGrams} g",
                "Fat: ${fatGrams} g"
            )

            MealAnalysis(
                id = id,
                name = name,
                caloriesKcal = caloriesKcal,
                proteinGrams = proteinGrams,
                carbsGrams = carbsGrams,
                fatGrams = fatGrams,
                servingSizeText = servingSizeText,
                notes = null,
                rawLines = rawLines
            )
        }

    companion object {
        private const val SIMULATED_MEAL_ANALYSIS_MS = 2000L
    }
}

