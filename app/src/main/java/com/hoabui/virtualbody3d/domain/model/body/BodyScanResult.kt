package com.hoabui.virtualbody3d.domain.model.body

/**
 * Domain model cho dữ liệu body (màn tổng quan + báo cáo scan chi tiết).
 * Nguồn dữ liệu duy nhất; metrics tổng quan được derive từ đây.
 */
data class BodyScanResult(
    val bodyComposition: BodyCompositionSection,
    val muscleFatAnalysis: MuscleFatAnalysisSection,
    val obesityAnalysis: ObesityAnalysisSection,
    val segmentalLean: SegmentalAnalysisSection,
    val segmentalFat: SegmentalAnalysisSection,
    val metabolic: MetabolicSection
)

data class BodyCompositionSection(
    val height: String,
    val weight: String,
    val totalBodyWater: String,
    val protein: String,
    val mineral: String,
    val bodyFatMass: String,
    val fatFreeMass: String
)

data class MuscleFatAnalysisSection(
    val weight: MetricWithRange,
    val skeletalMuscleMass: MetricWithRange,
    val bodyFatMass: MetricWithRange
)

data class ObesityAnalysisSection(
    val bmi: MetricWithRange,
    val percentBodyFat: MetricWithRange
)

data class MetricWithRange(
    val value: String,
    val currentValue: Float,
    val rangeMin: Float,
    val rangeMax: Float
)

data class SegmentalAnalysisSection(
    val leftArm: String,
    val rightArm: String,
    val trunk: String,
    val leftLeg: String,
    val rightLeg: String
)

data class MetabolicSection(
    val basalMetabolicRate: String,
    val obesityDegree: String,
    val recommendedCalorieIntake: String
)
