package com.hoabui.virtualbody3d.ui.scanresult

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.body.BodyCompositionSection
import com.hoabui.virtualbody3d.domain.model.body.BodyScanResult
import com.hoabui.virtualbody3d.domain.model.body.MetabolicSection
import com.hoabui.virtualbody3d.domain.model.body.MetricWithRange
import com.hoabui.virtualbody3d.domain.model.body.MuscleFatAnalysisSection
import com.hoabui.virtualbody3d.domain.model.body.ObesityAnalysisSection
import com.hoabui.virtualbody3d.domain.model.body.SegmentalAnalysisSection
import com.hoabui.virtualbody3d.ui.common_ui.organism.scan.GBodyScanMetricsPanel
import com.hoabui.virtualbody3d.ui.common_ui.organism.scan.GMetricRowUiModel
import com.hoabui.virtualbody3d.ui.common_ui.organism.scan.GMetricSectionUiModel
import com.hoabui.virtualbody3d.ui.common_ui.organism.scan.GSegmentalMetricUiModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Reusable panel in the same style as DashboardPanel (Surface with rounded top, handle).
 * Content is either full metrics from [scanResult] or an empty state when [scanResult] is null.
 * Used by BodyScanResultScreen and BodyAnalysisScreen (panel slider page 2).
 */
@Composable
fun MetricsPanel(
    modifier: Modifier = Modifier,
    scanResult: BodyScanResult?
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = token.colors.dashboardPanelBackground,
        shape = RoundedCornerShape(
            topStart = bodyToken.dashboardPanelTopRadius,
            topEnd = bodyToken.dashboardPanelTopRadius
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = bodyToken.dashboardPanelHorizontalPadding,
                    end = bodyToken.dashboardPanelHorizontalPadding,
                    top = bodyToken.dashboardPanelTopPadding,
                    bottom = bodyToken.dashboardPanelBottomPadding
                ),
            verticalArrangement = Arrangement.spacedBy(bodyToken.dashboardPanelSectionSpacing)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(bodyToken.dashboardHandleWidth)
                    .height(bodyToken.dashboardHandleHeight)
                    .background(
                        color = token.colors.dashboardHandle,
                        shape = RoundedCornerShape(token.radius.lg)
                    )
            )
            if (scanResult != null) {
                MetricsContent(scanResult = scanResult)
            } else {
                MetricsEmptyState()
            }
            Spacer(modifier = Modifier.height(bodyToken.dashboardScrollContentBottomSpacing))
        }
    }
}

@Composable
private fun MetricsEmptyState() {
    val token = GymTheme.token
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        GText(
            text = stringResource(R.string.metrics_panel_no_data),
            style = token.typography.bodyMedium,
            color = token.colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(token.spacing.md)
        )
    }
}

@Composable
private fun MetricsContent(
    modifier: Modifier = Modifier,
    scanResult: BodyScanResult
) {
    val sections = buildMetricsSections(scanResult)
    GBodyScanMetricsPanel(
        sections = sections,
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun buildMetricsSections(scanResult: BodyScanResult): List<GMetricSectionUiModel> {
    val body = scanResult.bodyComposition
    val muscleFat = scanResult.muscleFatAnalysis
    val obesity = scanResult.obesityAnalysis
    val segmentalLean = scanResult.segmentalLean
    val segmentalFat = scanResult.segmentalFat
    val metabolic = scanResult.metabolic

    return listOf(
        bodyCompositionSection(body),
        muscleFatSection(muscleFat),
        obesitySection(obesity),
        segmentalSection(
            id = "segmental_lean",
            title = stringResource(R.string.body_scan_result_segmental_lean),
            data = segmentalLean,
        ),
        segmentalSection(
            id = "segmental_fat",
            title = stringResource(R.string.body_scan_result_segmental_fat),
            data = segmentalFat,
        ),
        metabolicSection(metabolic),
    )
}

@Composable
private fun bodyCompositionSection(data: BodyCompositionSection): GMetricSectionUiModel = GMetricSectionUiModel(
    id = "body_composition",
    title = stringResource(R.string.body_scan_result_body_composition),
    rows = listOf(
        GMetricRowUiModel.ValueRow("weight", stringResource(R.string.body_scan_result_weight), data.weight),
        GMetricRowUiModel.ValueRow("body_fat_mass", stringResource(R.string.body_scan_result_body_fat_mass), data.bodyFatMass),
        GMetricRowUiModel.ValueRow("fat_free_mass", stringResource(R.string.body_scan_result_fat_free_mass), data.fatFreeMass),
        GMetricRowUiModel.ValueRow("total_body_water", stringResource(R.string.body_scan_result_total_body_water), data.totalBodyWater),
        GMetricRowUiModel.ValueRow("protein", stringResource(R.string.body_scan_result_protein), data.protein),
        GMetricRowUiModel.ValueRow("mineral", stringResource(R.string.body_scan_result_mineral), data.mineral),
    ),
)

@Composable
private fun muscleFatSection(data: MuscleFatAnalysisSection): GMetricSectionUiModel = GMetricSectionUiModel(
    id = "muscle_fat_analysis",
    title = stringResource(R.string.body_scan_result_muscle_fat_analysis),
    rows = listOf(
        data.weight.toProgressRow(
            id = "weight",
            label = stringResource(R.string.body_scan_result_weight),
        ),
        data.skeletalMuscleMass.toProgressRow(
            id = "skeletal_muscle_mass",
            label = stringResource(R.string.body_scan_result_skeletal_muscle_mass),
        ),
        data.bodyFatMass.toProgressRow(
            id = "body_fat_mass",
            label = stringResource(R.string.body_scan_result_body_fat_mass),
        ),
    ),
)

@Composable
private fun obesitySection(data: ObesityAnalysisSection): GMetricSectionUiModel = GMetricSectionUiModel(
    id = "obesity_analysis",
    title = stringResource(R.string.body_scan_result_obesity_analysis),
    rows = listOf(
        data.bmi.toProgressRow(
            id = "bmi",
            label = stringResource(R.string.body_scan_result_bmi),
        ),
        data.percentBodyFat.toProgressRow(
            id = "percent_body_fat",
            label = stringResource(R.string.body_scan_result_percent_body_fat),
        ),
    ),
)

@Composable
private fun segmentalSection(
    id: String,
    title: String,
    data: SegmentalAnalysisSection,
): GMetricSectionUiModel = GMetricSectionUiModel(
    id = id,
    title = title,
    rows = listOf(
        GMetricRowUiModel.SegmentalGridRow(
            id = "${id}_grid",
            items = listOf(
                GSegmentalMetricUiModel(
                    label = stringResource(R.string.body_scan_result_left_arm),
                    value = data.leftArm,
                ),
                GSegmentalMetricUiModel(
                    label = stringResource(R.string.body_scan_result_right_arm),
                    value = data.rightArm,
                ),
                GSegmentalMetricUiModel(
                    label = stringResource(R.string.body_scan_result_trunk),
                    value = data.trunk,
                ),
                GSegmentalMetricUiModel(
                    label = stringResource(R.string.body_scan_result_left_leg),
                    value = data.leftLeg,
                ),
                GSegmentalMetricUiModel(
                    label = stringResource(R.string.body_scan_result_right_leg),
                    value = data.rightLeg,
                ),
            ),
        ),
    ),
)

@Composable
private fun metabolicSection(data: MetabolicSection): GMetricSectionUiModel = GMetricSectionUiModel(
    id = "metabolic",
    title = stringResource(R.string.body_scan_result_metabolic),
    rows = listOf(
        GMetricRowUiModel.ValueRow(
            id = "bmr",
            label = stringResource(R.string.body_scan_result_bmr),
            value = data.basalMetabolicRate,
        ),
        GMetricRowUiModel.ValueRow(
            id = "obesity_degree",
            label = stringResource(R.string.body_scan_result_obesity_degree),
            value = data.obesityDegree,
        ),
        GMetricRowUiModel.ValueRow(
            id = "recommended_calorie_intake",
            label = stringResource(R.string.body_scan_result_recommended_calorie),
            value = data.recommendedCalorieIntake,
        ),
    ),
)

private fun MetricWithRange.toProgressRow(
    id: String,
    label: String,
): GMetricRowUiModel.ProgressRow {
    val progress = if (rangeMax > rangeMin) {
        ((currentValue - rangeMin) / (rangeMax - rangeMin)).coerceIn(0f, 1f)
    } else {
        0.5f
    }
    return GMetricRowUiModel.ProgressRow(
        id = id,
        label = label,
        value = value,
        progress = progress,
    )
}
