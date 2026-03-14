package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.BodyCompositionSectionDto
import com.hoabui.virtualbody3d.data.model.BodyScanResultDto
import com.hoabui.virtualbody3d.data.model.MetabolicSectionDto
import com.hoabui.virtualbody3d.data.model.MetricWithRangeDto
import com.hoabui.virtualbody3d.data.model.MuscleFatAnalysisSectionDto
import com.hoabui.virtualbody3d.data.model.ObesityAnalysisSectionDto
import com.hoabui.virtualbody3d.data.model.SegmentalAnalysisSectionDto
import com.hoabui.virtualbody3d.domain.model.BodyCompositionSection
import com.hoabui.virtualbody3d.domain.model.BodyScanResult
import com.hoabui.virtualbody3d.domain.model.MetabolicSection
import com.hoabui.virtualbody3d.domain.model.MetricWithRange
import com.hoabui.virtualbody3d.domain.model.MuscleFatAnalysisSection
import com.hoabui.virtualbody3d.domain.model.ObesityAnalysisSection
import com.hoabui.virtualbody3d.domain.model.SegmentalAnalysisSection

fun MetricWithRangeDto.toDomain(): MetricWithRange = MetricWithRange(
    value = value,
    currentValue = currentValue,
    rangeMin = rangeMin,
    rangeMax = rangeMax
)

fun BodyCompositionSectionDto.toDomain(): BodyCompositionSection = BodyCompositionSection(
    height = height,
    weight = weight,
    totalBodyWater = totalBodyWater,
    protein = protein,
    mineral = mineral,
    bodyFatMass = bodyFatMass,
    fatFreeMass = fatFreeMass
)

fun MuscleFatAnalysisSectionDto.toDomain(): MuscleFatAnalysisSection = MuscleFatAnalysisSection(
    weight = weight.toDomain(),
    skeletalMuscleMass = skeletalMuscleMass.toDomain(),
    bodyFatMass = bodyFatMass.toDomain()
)

fun ObesityAnalysisSectionDto.toDomain(): ObesityAnalysisSection = ObesityAnalysisSection(
    bmi = bmi.toDomain(),
    percentBodyFat = percentBodyFat.toDomain()
)

fun SegmentalAnalysisSectionDto.toDomain(): SegmentalAnalysisSection = SegmentalAnalysisSection(
    leftArm = leftArm,
    rightArm = rightArm,
    trunk = trunk,
    leftLeg = leftLeg,
    rightLeg = rightLeg
)

fun MetabolicSectionDto.toDomain(): MetabolicSection = MetabolicSection(
    basalMetabolicRate = basalMetabolicRate,
    obesityDegree = obesityDegree,
    recommendedCalorieIntake = recommendedCalorieIntake
)

fun BodyScanResultDto.toDomain(): BodyScanResult = BodyScanResult(
    bodyComposition = bodyComposition.toDomain(),
    muscleFatAnalysis = muscleFatAnalysis.toDomain(),
    obesityAnalysis = obesityAnalysis.toDomain(),
    segmentalLean = segmentalLean.toDomain(),
    segmentalFat = segmentalFat.toDomain(),
    metabolic = metabolic.toDomain()
)
