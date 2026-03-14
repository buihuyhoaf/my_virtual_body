package com.hoabui.virtualbody3d.data.model

data class ExtractedDataDto(
    val weight: String,
    val bodyFatPercent: String,
    val muscleMass: String,
    val bmi: String,
    val bodyFatMass: String = "",
    val fatFreeMass: String = "",
    val bmr: String = "",
    val visceralFatLevel: String = "",
    val rawLines: List<String> = emptyList()
)
