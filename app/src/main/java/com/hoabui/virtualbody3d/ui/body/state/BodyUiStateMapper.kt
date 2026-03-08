package com.hoabui.virtualbody3d.ui.body.state

import com.hoabui.virtualbody3d.domain.model.BodyScanResult
import com.hoabui.virtualbody3d.domain.model.MetricWithRange

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
        bmiStatus = null,
        bmiScalePosition = obesity.bmi.toScalePosition()
    )
}

private fun MetricWithRange.toScalePosition(): Float? {
    val range = rangeMax - rangeMin
    if (range <= 0f) return null
    return ((currentValue - rangeMin) / range).coerceIn(0f, 1f)
}
