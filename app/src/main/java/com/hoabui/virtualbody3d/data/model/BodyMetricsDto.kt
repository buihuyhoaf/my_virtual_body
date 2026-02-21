package com.hoabui.virtualbody3d.data.model

data class BodyMetricsDto(
    val height: String,
    val heightUnit: String,
    val weight: String,
    val weightUnit: String,
    val weightProgress: Float?,
    val bodyFat: String,
    val bodyFatProgress: Float?,
    val muscleMass: String,
    val muscleMassUnit: String,
    val muscleMassProgress: Float?,
    val bmi: String,
    val bmiStatus: String?,
    val bmiScalePosition: Float?
)
