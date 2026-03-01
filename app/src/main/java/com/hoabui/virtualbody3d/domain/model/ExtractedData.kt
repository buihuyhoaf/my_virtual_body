package com.hoabui.virtualbody3d.domain.model

/**
 * Result of OCR/baseline extraction from an InBody result image.
 * Used when reviewing extracted data before confirming baseline.
 */
data class ExtractedData(
    val weight: String,
    val bodyFatPercent: String,
    val muscleMass: String,
    val bmi: String,
    val rawLines: List<String> = emptyList()
)
