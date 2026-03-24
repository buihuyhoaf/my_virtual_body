package com.hoabui.virtualbody3d.ui.body.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.atom.card.GCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.core.extensions.graphicsLayerAlphaTranslationX
import com.hoabui.virtualbody3d.ui.body.data.CalorieUiModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import kotlin.math.abs

private const val IntroMetricDurationMs = 260
private const val IntroRingDurationMs = 280
private const val IntroDeficitDurationMs = 280
private const val IntroRingInitialScale = 0.96f

@Composable
fun CaloriePremiumCard(
    modifier: Modifier = Modifier,
    data: CalorieUiModel,
) {
    val token = GymTheme.token
    val body = token.bodyAnalysis
    val ringSize = body.dashboardCalorieRingSize
    val sideColumnWidth = body.dashboardCaloriePremiumSideColumnWidth

    var introVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { introVisible = true }

    val metricAlpha by animateFloatAsState(
        targetValue = if (introVisible) 1f else 0f,
        animationSpec = tween(durationMillis = IntroMetricDurationMs),
        label = "calorie-premium-metric-alpha"
    )
    val metricOffset by animateDpAsState(
        targetValue = if (introVisible) 0.dp else -token.spacing.xs,
        animationSpec = tween(durationMillis = IntroMetricDurationMs),
        label = "calorie-premium-metric-offset"
    )
    val ringScale by animateFloatAsState(
        targetValue = if (introVisible) 1f else IntroRingInitialScale,
        animationSpec = tween(durationMillis = IntroRingDurationMs),
        label = "calorie-premium-ring-scale"
    )
    val deficitAlpha by animateFloatAsState(
        targetValue = if (introVisible) 1f else 0f,
        animationSpec = tween(durationMillis = IntroDeficitDurationMs),
        label = "calorie-premium-deficit-alpha"
    )
    val deficitOffset by animateDpAsState(
        targetValue = if (introVisible) 0.dp else token.spacing.xs,
        animationSpec = tween(durationMillis = IntroDeficitDurationMs),
        label = "calorie-premium-deficit-offset"
    )

    val ringDescription = stringResource(R.string.calorie_premium_ring_content_description)

    GCard(
        modifier = modifier.wrapContentWidth(),
        border = BorderStroke(token.spacing.dividerThickness, token.colors.calorieRingTrack),
    ) {
        Row(
            modifier = Modifier
                .padding(token.spacing.md)
                .wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(token.spacing.md)
        ) {
            Box(
                modifier = Modifier.width(sideColumnWidth),
                contentAlignment = Alignment.CenterStart
            ) {
                MetricSection(
                    data = data,
                    modifier = Modifier.graphicsLayerAlphaTranslationX(
                        alpha = metricAlpha,
                        translationX = metricOffset
                    )
                )
            }

            CalorieDualRingChart(
                intakeProgress = data.intakeProgress,
                burnedProgress = data.burnedProgress,
                modifier = Modifier
                    .size(ringSize)
                    .semantics { contentDescription = ringDescription }
                    .graphicsLayer {
                        scaleX = ringScale
                        scaleY = ringScale
                    }
            )

            Box(
                modifier = Modifier.width(sideColumnWidth),
                contentAlignment = Alignment.CenterEnd
            ) {
                DeficitText(
                    deficit = data.deficit,
                    textAlign = TextAlign.End,
                    modifier = Modifier.graphicsLayerAlphaTranslationX(
                        alpha = deficitAlpha,
                        translationX = deficitOffset
                    )
                )
            }
        }
    }
}

@Composable
private fun MetricSection(
    data: CalorieUiModel,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
        horizontalAlignment = Alignment.Start
    ) {
        MetricHorizontalLineCell(
            value = data.intake,
            label = stringResource(R.string.calorie_premium_intake),
            color = token.colors.calorieIntake
        )
        MetricHorizontalLineCell(
            value = data.burned,
            label = stringResource(R.string.calorie_premium_burned),
            color = token.colors.calorieBurned
        )
    }
}

@Composable
private fun DeficitText(
    deficit: Int,
    textAlign: TextAlign,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val kcalUnit = stringResource(R.string.calorie_premium_kcal)
    val text = if (deficit >= 0) {
        stringResource(R.string.calorie_premium_deficit_minus_format, deficit, kcalUnit)
    } else {
        stringResource(R.string.calorie_premium_deficit_plus_format, abs(deficit), kcalUnit)
    }
    val deficitColor =
        if (deficit >= 0) token.colors.calorieDeficitPositive else token.colors.calorieDeficitNegative
    GText(
        text = text,
        style = token.typography.headlineSmall,
        color = deficitColor,
        textAlign = textAlign,
        modifier = modifier
    )
}

@Composable
private fun MetricHorizontalLineCell(
    value: Int,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(token.spacing.xxs)
        ) {
            drawLine(
                color = color,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = size.height,
                cap = StrokeCap.Round
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            GText(text = "$value", style = token.typography.bodyMedium, color = color)
            GText(text = label, style = token.typography.labelSmall, color = token.colors.textSecondary)
        }
    }
}

@Preview(showBackground = true, name = "Calorie premium — light")
@Composable
private fun CaloriePremiumCardPreviewLight() {
    GymTheme {
        CaloriePremiumCard(
            data = CalorieUiModel(
                intake = 1850,
                burned = 420,
                intakeGoal = 2200,
                burnGoal = 500
            )
        )
    }
}
