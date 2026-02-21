package com.hoabui.virtualbody3d.ui.body.state

import com.hoabui.virtualbody3d.domain.model.BodyMetrics

fun BodyMetrics.toUiState(): BodyUiState {
    return BodyUiState(
        height = height,
        heightUnit = heightUnit,
        weight = weight,
        weightUnit = weightUnit,
        weightProgress = weightProgress,
        bodyFat = bodyFat,
        bodyFatProgress = bodyFatProgress,
        muscleMass = muscleMass,
        muscleMassUnit = muscleMassUnit,
        muscleMassProgress = muscleMassProgress,
        bmi = bmi,
        bmiStatus = bmiStatus,
        bmiScalePosition = bmiScalePosition
    )
}
