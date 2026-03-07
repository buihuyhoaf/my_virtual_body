package com.hoabui.virtualbody3d.domain.model

/**
 * Type of AI analysis performed on an uploaded image.
 */
enum class AnalysisType {
    /** Extract body metrics (e.g. from InBody result image). */
    OCR,
    /** Recognize meal / food from image. */
    MEAL
}
