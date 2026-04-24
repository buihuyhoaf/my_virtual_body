package com.hoabui.virtualbody3d.domain.util

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import org.junit.Assert.assertEquals
import org.junit.Test

class CaloriesCalculatorTest {

    @Test
    fun estimateLibraryUptoKcal_strength_roundsToNearestFive() {
        val kcal = CaloriesCalculator.estimateLibraryUptoKcal(
            exerciseId = "squat",
            measurementMode = ExerciseMeasurementMode.Strength,
        )

        assertEquals(25, kcal)
    }

    @Test
    fun estimateLibraryUptoKcal_duration_roundsToNearestFive() {
        val kcal = CaloriesCalculator.estimateLibraryUptoKcal(
            exerciseId = "running",
            measurementMode = ExerciseMeasurementMode.Duration,
        )

        assertEquals(10, kcal)
    }
}
