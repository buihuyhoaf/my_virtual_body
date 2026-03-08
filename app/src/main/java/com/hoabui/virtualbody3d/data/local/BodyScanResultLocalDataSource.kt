package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.domain.model.BodyCompositionSection
import com.hoabui.virtualbody3d.domain.model.BodyScanResult
import com.hoabui.virtualbody3d.domain.model.MetricWithRange
import com.hoabui.virtualbody3d.domain.model.MetabolicSection
import com.hoabui.virtualbody3d.domain.model.MuscleFatAnalysisSection
import com.hoabui.virtualbody3d.domain.model.ObesityAnalysisSection
import com.hoabui.virtualbody3d.domain.model.SegmentalAnalysisSection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BodyScanResultLocalDataSource @Inject constructor() {

    fun getBodyScanResult(): BodyScanResult {
        return BodyScanResult(
            bodyComposition = BodyCompositionSection(
                height = "175",
                weight = "68.5",
                totalBodyWater = "38.2",
                protein = "10.1 ",
                mineral = "3.2",
                bodyFatMass = "18.4 ",
                fatFreeMass = "50.1 "
            ),
            muscleFatAnalysis = MuscleFatAnalysisSection(
                weight = MetricWithRange("68.5 ", 68.5f, 40f, 100f),
                skeletalMuscleMass = MetricWithRange("28.3 ", 28.3f, 20f, 45f),
                bodyFatMass = MetricWithRange("18.4 ", 18.4f, 10f, 35f)
            ),
            obesityAnalysis = ObesityAnalysisSection(
                bmi = MetricWithRange("22.4", 22.4f, 15f, 35f),
                percentBodyFat = MetricWithRange("26.8", 26.8f, 10f, 45f)
            ),
            segmentalLean = SegmentalAnalysisSection(
                leftArm = "2.8",
                rightArm = "2.9",
                trunk = "22.1",
                leftLeg = "7.2",
                rightLeg = "7.3"
            ),
            segmentalFat = SegmentalAnalysisSection(
                leftArm = "1.2",
                rightArm = "1.1",
                trunk = "9.8",
                leftLeg = "3.1",
                rightLeg = "3.2 "
            ),
            metabolic = MetabolicSection(
                basalMetabolicRate = "1,520",
                obesityDegree = "98",
                recommendedCalorieIntake = "2,280"
            )
        )
    }
}
