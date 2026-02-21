package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.BodyMetricsDto
import com.hoabui.virtualbody3d.domain.model.BodyMetrics

fun BodyMetricsDto.toDomain(): BodyMetrics {
    return BodyMetrics(
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
