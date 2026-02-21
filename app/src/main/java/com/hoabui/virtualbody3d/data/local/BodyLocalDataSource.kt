package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.model.BodyMetricsDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BodyLocalDataSource @Inject constructor() {
    fun getBodyMetrics(): BodyMetricsDto {
        return BodyMetricsDto(
            height = "180",
            heightUnit = "cm",
            weight = "75.5",
            weightUnit = "kg",
            weightProgress = 0.65f,
            bodyFat = "15.4",
            bodyFatProgress = 0.4f,
            muscleMass = "62.0",
            muscleMassUnit = "kg",
            muscleMassProgress = 0.78f,
            bmi = "23.2",
            bmiStatus = "Normal",
            bmiScalePosition = 0.45f
        )
    }
}
