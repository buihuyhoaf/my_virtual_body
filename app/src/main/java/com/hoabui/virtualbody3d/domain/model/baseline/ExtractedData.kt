package com.hoabui.virtualbody3d.domain.model.baseline

/**
 * Result of OCR/baseline extraction from an InBody result image.
 * Used when reviewing extracted data before confirming baseline.
 */
data class ExtractedData(
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
