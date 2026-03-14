package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.model.MealAnalysisDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealLocalDataSource @Inject constructor() {

    suspend fun analyzeMeal(imageUrl: String): MealAnalysisDto = withContext(Dispatchers.IO) {
        delay(SIMULATED_MEAL_ANALYSIS_MS)
        createFakeMealAnalysisDto()
    }

    suspend fun getDaysWithMeals(): List<LocalDate> = withContext(Dispatchers.IO) {
        delay(300)
        val today = LocalDate.now()
        listOf(today, today.minusDays(1), today.minusDays(2))
    }

    suspend fun getMealsByDay(day: LocalDate): List<MealAnalysisDto> = withContext(Dispatchers.IO) {
        delay(400)
        val today = LocalDate.now()
        if (day == today) {
            listOf(
                createFakeMealAnalysisDto(),
                createFakeMealAnalysisDto(name = "Salad bowl").copy(id = UUID.randomUUID().toString()),
                createFakeMealAnalysisDto(name = "Salad bowl").copy(id = UUID.randomUUID().toString()),
                createFakeMealAnalysisDto(name = "Salad bowl").copy(id = UUID.randomUUID().toString()),
            )
        } else {
            listOf(createFakeMealAnalysisDto(name = "Past day meal").copy(id = UUID.randomUUID().toString()))
        }
    }

    private fun createFakeMealAnalysisDto(name: String = "Grilled chicken with rice"): MealAnalysisDto {
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
        return MealAnalysisDto(
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
