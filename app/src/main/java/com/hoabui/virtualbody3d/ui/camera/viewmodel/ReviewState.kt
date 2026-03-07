package com.hoabui.virtualbody3d.ui.camera.viewmodel

import com.hoabui.virtualbody3d.domain.model.ExtractedData

/**
 * State for the review baseline bottom sheet: editable copy, modification and validity flags.
 */
data class ReviewState(
    val originalData: ExtractedData,
    val editableData: ExtractedData,
    val isModified: Boolean,
    val isValid: Boolean,
    val isLoading: Boolean
) {
    fun isFieldModified(originalValue: String, currentValue: String): Boolean =
        originalValue != currentValue
}

/**
 * Keys for editable metrics in the review sheet (for updating a single field).
 */
enum class ReviewMetric {
    WEIGHT,
    BODY_FAT_PERCENT,
    MUSCLE_MASS,
    BMI,
    BODY_FAT_MASS,
    FAT_FREE_MASS,
    BMR,
    VISCERAL_FAT_LEVEL
}
