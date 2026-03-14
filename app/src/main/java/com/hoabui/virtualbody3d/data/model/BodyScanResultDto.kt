package com.hoabui.virtualbody3d.data.model

data class BodyScanResultDto(
    val bodyComposition: BodyCompositionSectionDto,
    val muscleFatAnalysis: MuscleFatAnalysisSectionDto,
    val obesityAnalysis: ObesityAnalysisSectionDto,
    val segmentalLean: SegmentalAnalysisSectionDto,
    val segmentalFat: SegmentalAnalysisSectionDto,
    val metabolic: MetabolicSectionDto
)

data class BodyCompositionSectionDto(
    val height: String,
    val weight: String,
    val totalBodyWater: String,
    val protein: String,
    val mineral: String,
    val bodyFatMass: String,
    val fatFreeMass: String
)

data class MuscleFatAnalysisSectionDto(
    val weight: MetricWithRangeDto,
    val skeletalMuscleMass: MetricWithRangeDto,
    val bodyFatMass: MetricWithRangeDto
)

data class ObesityAnalysisSectionDto(
    val bmi: MetricWithRangeDto,
    val percentBodyFat: MetricWithRangeDto
)

data class MetricWithRangeDto(
    val value: String,
    val currentValue: Float,
    val rangeMin: Float,
    val rangeMax: Float
)

data class SegmentalAnalysisSectionDto(
    val leftArm: String,
    val rightArm: String,
    val trunk: String,
    val leftLeg: String,
    val rightLeg: String
)

data class MetabolicSectionDto(
    val basalMetabolicRate: String,
    val obesityDegree: String,
    val recommendedCalorieIntake: String
)
