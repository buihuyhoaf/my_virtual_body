package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.nutrition.MealAnalysis
import java.time.LocalDate

/**
 * Repository for meal analysis results and meal history by day.
 */
interface MealRepository {

    /**
     * Sends the uploaded image to the backend for meal recognition and nutrition estimation.
     * @param imageUrl URL of the uploaded image
     * @return [MealAnalysis] describing the recognized meal and macro breakdown
     * @throws Exception on analysis failure
     */
    suspend fun analyzeMeal(imageUrl: String): MealAnalysis

    /**
     * Returns dates that have at least one meal for the current user, newest first.
     */
    suspend fun getDaysWithMeals(): List<LocalDate>

    /**
     * Returns all meals for the given day.
     */
    suspend fun getMealsByDay(day: LocalDate): List<MealAnalysis>
}

