package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.model.BodyCompositionSectionDto
import com.hoabui.virtualbody3d.data.model.BodyScanResultDto
import com.hoabui.virtualbody3d.data.model.MetabolicSectionDto
import com.hoabui.virtualbody3d.data.model.MetricWithRangeDto
import com.hoabui.virtualbody3d.data.model.MuscleFatAnalysisSectionDto
import com.hoabui.virtualbody3d.data.model.ObesityAnalysisSectionDto
import com.hoabui.virtualbody3d.data.model.SegmentalAnalysisSectionDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BodyScanResultLocalDataSource @Inject constructor() {

    fun getBodyScanResult(): BodyScanResultDto {
        return BodyScanResultDto(
            bodyComposition = BodyCompositionSectionDto(
                height = "175",
                weight = "68.5",
                totalBodyWater = "38.2",
                protein = "10.1 ",
                mineral = "3.2",
                bodyFatMass = "18.4 ",
                fatFreeMass = "50.1 "
            ),
            muscleFatAnalysis = MuscleFatAnalysisSectionDto(
                weight = MetricWithRangeDto("68.5 ", 68.5f, 40f, 100f),
                skeletalMuscleMass = MetricWithRangeDto("28.3 ", 28.3f, 20f, 45f),
                bodyFatMass = MetricWithRangeDto("18.4 ", 18.4f, 10f, 35f)
            ),
            obesityAnalysis = ObesityAnalysisSectionDto(
                bmi = MetricWithRangeDto("22.4", 22.4f, 15f, 35f),
                percentBodyFat = MetricWithRangeDto("26.8", 26.8f, 10f, 45f)
            ),
            segmentalLean = SegmentalAnalysisSectionDto(
                leftArm = "2.8",
                rightArm = "2.9",
                trunk = "22.1",
                leftLeg = "7.2",
                rightLeg = "7.3"
            ),
            segmentalFat = SegmentalAnalysisSectionDto(
                leftArm = "1.2",
                rightArm = "1.1",
                trunk = "9.8",
                leftLeg = "3.1",
                rightLeg = "3.2 "
            ),
            metabolic = MetabolicSectionDto(
                basalMetabolicRate = "1,520",
                obesityDegree = "98",
                recommendedCalorieIntake = "2,280"
            )
        )
    }
}
