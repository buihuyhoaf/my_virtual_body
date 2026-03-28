package com.hoabui.virtualbody3d.ui.body.state

import com.hoabui.virtualbody3d.domain.model.body.BodyScanResult
import com.hoabui.virtualbody3d.domain.model.body.MetricWithRange

fun BodyScanResult.toUiState(): BodyUiState {
    val comp = bodyComposition
    val obesity = obesityAnalysis
    val muscleFat = muscleFatAnalysis
    return BodyUiState(
        height = comp.height,
        weight = comp.weight,
        bodyFat = obesity.percentBodyFat.value,
        muscleMass = muscleFat.skeletalMuscleMass.value,
        bmi = obesity.bmi.value,
        bmiCategory = obesity.bmi.toBmiCategory(),
        bmiScalePosition = obesity.bmi.toScalePosition()
    )
}

private fun MetricWithRange.toBmiCategory(): BodyBmiCategory {
    if (currentValue <= 0f) return BodyBmiCategory.UNKNOWN
    return when {
        currentValue < 18.5f -> BodyBmiCategory.UNDERWEIGHT
        currentValue < 25f -> BodyBmiCategory.NORMAL
        currentValue < 30f -> BodyBmiCategory.OVERWEIGHT
        else -> BodyBmiCategory.OBESE
    }
}

private fun MetricWithRange.toScalePosition(): Float? {
    val range = rangeMax - rangeMin
    if (range <= 0f) return null
    return ((currentValue - rangeMin) / range).coerceIn(0f, 1f)
}
