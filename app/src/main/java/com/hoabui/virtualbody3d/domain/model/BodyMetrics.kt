package com.hoabui.virtualbody3d.domain.model

data class BodyMetrics(
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
