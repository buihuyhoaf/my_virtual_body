package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.MealAnalysis

/**
 * Repository for meal analysis results given an uploaded image URL.
 */
interface MealRepository {

    /**
     * Sends the uploaded image to the backend for meal recognition and nutrition estimation.
     * @param imageUrl URL of the uploaded image
     * @return [MealAnalysis] describing the recognized meal and macro breakdown
     * @throws Exception on analysis failure
     */
    suspend fun analyzeMeal(imageUrl: String): MealAnalysis
}

