package com.hoabui.virtualbody3d.ui.scanresult

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.body.BodyCompositionSection
import com.hoabui.virtualbody3d.domain.model.body.BodyScanResult
import com.hoabui.virtualbody3d.domain.model.body.MetabolicSection
import com.hoabui.virtualbody3d.domain.model.body.MuscleFatAnalysisSection
import com.hoabui.virtualbody3d.domain.model.body.ObesityAnalysisSection
import com.hoabui.virtualbody3d.domain.model.body.SegmentalAnalysisSection
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
        Text(
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
    val token = GymTheme.token
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.lg)
    ) {
        BodyCompositionCard(data = scanResult.bodyComposition)
        MuscleFatChartCard(data = scanResult.muscleFatAnalysis)
        ObesityAnalysisCard(data = scanResult.obesityAnalysis)
        SegmentalLeanCard(data = scanResult.segmentalLean)
        SegmentalFatCard(data = scanResult.segmentalFat)
        MetabolicCard(data = scanResult.metabolic)
    }
}

@Composable
private fun BodyCompositionCard(
    data: BodyCompositionSection
) {
    val token = GymTheme.token
    val colors = token.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(token.radius.lg),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceOverlay),
        elevation = CardDefaults.cardElevation(defaultElevation = token.elevation.level0)
    ) {
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.md)
        ) {
            Text(
                text = stringResource(R.string.body_scan_result_body_composition),
                style = token.typography.titleMedium,
                color = colors.textPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(token.spacing.xs)
            ) {
                CompositionChip(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.body_scan_result_weight),
                    value = data.weight
                )
                CompositionChip(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.body_scan_result_body_fat_mass),
                    value = data.bodyFatMass
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(token.spacing.xs)
            ) {
                CompositionChip(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.body_scan_result_fat_free_mass),
                    value = data.fatFreeMass
                )
                CompositionChip(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.body_scan_result_total_body_water),
                    value = data.totalBodyWater
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(token.spacing.xs)
            ) {
                CompositionChip(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.body_scan_result_protein),
                    value = data.protein
                )
                CompositionChip(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.body_scan_result_mineral),
                    value = data.mineral
                )
            }
        }
    }
}

@Composable
private fun CompositionChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(token.radius.md))
            .background(token.colors.surfaceSubtle)
            .padding(token.spacing.xs),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxs)
    ) {
        Text(
            text = label,
            style = token.typography.labelSmall,
            color = token.colors.textSecondary
        )
        Text(
            text = value,
            style = token.typography.titleSmall,
            color = token.colors.textPrimary
        )
    }
}

@Composable
private fun MuscleFatChartCard(
    data: MuscleFatAnalysisSection
) {
    val token = GymTheme.token
    val colors = token.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(token.radius.lg),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceOverlay),
        elevation = CardDefaults.cardElevation(defaultElevation = token.elevation.level0)
    ) {
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.md)
        ) {
            Text(
                text = stringResource(R.string.body_scan_result_muscle_fat_analysis),
                style = token.typography.titleMedium,
                color = colors.textPrimary
            )
            ChartBarRow(
                label = stringResource(R.string.body_scan_result_weight),
                value = data.weight.value,
                currentValue = data.weight.currentValue,
                rangeMin = data.weight.rangeMin,
                rangeMax = data.weight.rangeMax
            )
            ChartBarRow(
                label = stringResource(R.string.body_scan_result_skeletal_muscle_mass),
                value = data.skeletalMuscleMass.value,
                currentValue = data.skeletalMuscleMass.currentValue,
                rangeMin = data.skeletalMuscleMass.rangeMin,
                rangeMax = data.skeletalMuscleMass.rangeMax
            )
            ChartBarRow(
                label = stringResource(R.string.body_scan_result_body_fat_mass),
                value = data.bodyFatMass.value,
                currentValue = data.bodyFatMass.currentValue,
                rangeMin = data.bodyFatMass.rangeMin,
                rangeMax = data.bodyFatMass.rangeMax
            )
        }
    }
}

@Composable
private fun ChartBarRow(
    label: String,
    value: String,
    currentValue: Float,
    rangeMin: Float,
    rangeMax: Float,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val progress = if (rangeMax > rangeMin) {
        ((currentValue - rangeMin) / (rangeMax - rangeMin)).coerceIn(0f, 1f)
    } else 0.5f
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxs)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = token.typography.bodySmall,
                color = token.colors.textSecondary
            )
            Text(
                text = value,
                style = token.typography.bodyMedium,
                color = token.colors.textPrimary
            )
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(token.radius.sm)),
            color = token.colors.primary,
            trackColor = token.colors.surfaceSubtle
        )
    }
}

@Composable
private fun ObesityAnalysisCard(
    data: ObesityAnalysisSection
) {
    val token = GymTheme.token
    val colors = token.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(token.radius.lg),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceOverlay),
        elevation = CardDefaults.cardElevation(defaultElevation = token.elevation.level0)
    ) {
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.md)
        ) {
            Text(
                text = stringResource(R.string.body_scan_result_obesity_analysis),
                style = token.typography.titleMedium,
                color = colors.textPrimary
            )
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clip(RoundedCornerShape(token.radius.sm))
                    .background(colors.surfaceSubtle)
            ) {
                val bmiProgress = if (data.bmi.rangeMax > data.bmi.rangeMin) {
                    ((data.bmi.currentValue - data.bmi.rangeMin) / (data.bmi.rangeMax - data.bmi.rangeMin)).coerceIn(0f, 1f)
                } else 0.5f
                val thumbOffsetPx = maxWidth.value * bmiProgress
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = stringResource(R.string.body_scan_result_bmi) + " ${data.bmi.value}",
                        style = token.typography.labelMedium,
                        color = colors.textPrimary,
                        modifier = Modifier.padding(horizontal = token.spacing.xs)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .offset(x = (thumbOffsetPx - 2.dp.value).coerceAtLeast(0f).dp)
                        .background(token.colors.primary)
                        .align(Alignment.CenterStart)
                )
            }
            ChartBarRow(
                label = stringResource(R.string.body_scan_result_percent_body_fat),
                value = data.percentBodyFat.value,
                currentValue = data.percentBodyFat.currentValue,
                rangeMin = data.percentBodyFat.rangeMin,
                rangeMax = data.percentBodyFat.rangeMax
            )
        }
    }
}

@Composable
private fun SegmentalLeanCard(
    data: SegmentalAnalysisSection
) {
    val items = listOf(
        SegmentalMetric(stringResource(R.string.body_scan_result_left_arm), data.leftArm),
        SegmentalMetric(stringResource(R.string.body_scan_result_right_arm), data.rightArm),
        SegmentalMetric(stringResource(R.string.body_scan_result_trunk), data.trunk),
        SegmentalMetric(stringResource(R.string.body_scan_result_left_leg), data.leftLeg),
        SegmentalMetric(stringResource(R.string.body_scan_result_right_leg), data.rightLeg)
    )
    SegmentalAnalysisCard(
        title = stringResource(R.string.body_scan_result_segmental_lean),
        items = items
    )
}

@Composable
private fun SegmentalFatCard(
    data: SegmentalAnalysisSection
) {
    val items = listOf(
        SegmentalMetric(stringResource(R.string.body_scan_result_left_arm), data.leftArm),
        SegmentalMetric(stringResource(R.string.body_scan_result_right_arm), data.rightArm),
        SegmentalMetric(stringResource(R.string.body_scan_result_trunk), data.trunk),
        SegmentalMetric(stringResource(R.string.body_scan_result_left_leg), data.leftLeg),
        SegmentalMetric(stringResource(R.string.body_scan_result_right_leg), data.rightLeg)
    )
    SegmentalAnalysisCard(
        title = stringResource(R.string.body_scan_result_segmental_fat),
        items = items
    )
}

@Composable
private fun SegmentalAnalysisCard(
    title: String,
    items: List<SegmentalMetric>,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val colors = token.colors
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(token.radius.lg),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceOverlay),
        elevation = CardDefaults.cardElevation(defaultElevation = token.elevation.level0)
    ) {
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.md)
        ) {
            Text(
                text = title,
                style = token.typography.titleMedium,
                color = colors.textPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(token.spacing.md)
            ) {
                SegmentalCell(
                    modifier = Modifier.weight(1f),
                    label = items.getOrNull(0)?.label ?: "",
                    value = items.getOrNull(0)?.value ?: ""
                )
                SegmentalCell(
                    modifier = Modifier.weight(1f),
                    label = items.getOrNull(1)?.label ?: "",
                    value = items.getOrNull(1)?.value ?: ""
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(token.spacing.md)
            ) {
                SegmentalCell(
                    modifier = Modifier.weight(1f),
                    label = items.getOrNull(2)?.label ?: "",
                    value = items.getOrNull(2)?.value ?: ""
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(token.spacing.md)
            ) {
                SegmentalCell(
                    modifier = Modifier.weight(1f),
                    label = items.getOrNull(3)?.label ?: "",
                    value = items.getOrNull(3)?.value ?: ""
                )
                SegmentalCell(
                    modifier = Modifier.weight(1f),
                    label = items.getOrNull(4)?.label ?: "",
                    value = items.getOrNull(4)?.value ?: ""
                )
            }
        }
    }
}

@Composable
private fun SegmentalCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(token.radius.md))
            .background(token.colors.surfaceSubtle)
            .padding(token.spacing.md),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxs)
    ) {
        Text(
            text = label,
            style = token.typography.labelMedium,
            color = token.colors.textSecondary
        )
        Text(
            text = value,
            style = token.typography.bodyMedium,
            color = token.colors.textPrimary
        )
        LinearProgressIndicator(
            progress = { 1f },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(token.radius.sm)),
            color = token.colors.primary.copy(alpha = 0.5f),
            trackColor = token.colors.surfaceOverlay
        )
    }
}

@Composable
private fun MetabolicCard(
    data: MetabolicSection
) {
    val token = GymTheme.token
    val colors = token.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(token.radius.lg),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceOverlay),
        elevation = CardDefaults.cardElevation(defaultElevation = token.elevation.level0)
    ) {
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.md)
        ) {
            Text(
                text = stringResource(R.string.body_scan_result_metabolic),
                style = token.typography.titleMedium,
                color = colors.textPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(token.spacing.xs)
            ) {
                MetabolicChip(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.body_scan_result_bmr),
                    value = data.basalMetabolicRate
                )
                MetabolicChip(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.body_scan_result_obesity_degree),
                    value = data.obesityDegree
                )
            }
            MetabolicChip(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.body_scan_result_recommended_calorie),
                value = data.recommendedCalorieIntake
            )
        }
    }
}

@Composable
private fun MetabolicChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(token.radius.md))
            .background(token.colors.surfaceSubtle)
            .padding(token.spacing.md),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxs)
    ) {
        Text(
            text = label,
            style = token.typography.labelSmall,
            color = token.colors.textSecondary
        )
        Text(
            text = value,
            style = token.typography.titleSmall,
            color = token.colors.textPrimary
        )
    }
}
