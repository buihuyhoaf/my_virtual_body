package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.domain.model.MealAnalysis
import com.hoabui.virtualbody3d.domain.repository.MealRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepositoryImpl @Inject constructor() : MealRepository {

    override suspend fun analyzeMeal(imageUrl: String): MealAnalysis =
        withContext(Dispatchers.IO) {
            delay(SIMULATED_MEAL_ANALYSIS_MS)
            createFakeMealAnalysis()
        }

    override suspend fun getDaysWithMeals(): List<LocalDate> = withContext(Dispatchers.IO) {
        delay(300) // Simulate network
        val today = LocalDate.now()
        listOf(today, today.minusDays(1), today.minusDays(2))
    }

    override suspend fun getMealsByDay(day: LocalDate): List<MealAnalysis> =
        withContext(Dispatchers.IO) {
            delay(400) // Simulate network
            val today = LocalDate.now()
            if (day == today) {
                listOf(
                    createFakeMealAnalysis(),
                    createFakeMealAnalysis(name = "Salad bowl").copy(id = UUID.randomUUID().toString()),
                    createFakeMealAnalysis(name = "Salad bowl").copy(id = UUID.randomUUID().toString()),
                    createFakeMealAnalysis(name = "Salad bowl").copy(id = UUID.randomUUID().toString()),
                )
            } else {
                listOf(createFakeMealAnalysis(name = "Past day meal").copy(id = UUID.randomUUID().toString()))
            }
        }

    private fun createFakeMealAnalysis(name: String = "Grilled chicken with rice"): MealAnalysis {
        val id = UUID.randomUUID().toString()
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
        return MealAnalysis(
            id = id,
            name = name,
            caloriesKcal = caloriesKcal,
            proteinGrams = proteinGrams,
            carbsGrams = carbsGrams,
            fatGrams = fatGrams,
            servingSizeText = servingSizeText,
            notes = null,
            rawLines = rawLines,
            imageUrl = null
        )
    }

    companion object {
        private const val SIMULATED_MEAL_ANALYSIS_MS = 2000L
    }
}

