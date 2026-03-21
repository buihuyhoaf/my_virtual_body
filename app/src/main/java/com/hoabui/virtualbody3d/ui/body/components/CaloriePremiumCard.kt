package com.hoabui.virtualbody3d.ui.body.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.body.data.CalorieUiModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun CaloriePremiumCard(
    data: CalorieUiModel,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val ringSize = token.bodyAnalysis.dashboardCalorieRingSize
    val sideSlotWidth = ringSize * 1.1f
    var animateIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animateIn = true }

    val metricAlpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(durationMillis = 260),
        label = "metric-alpha"
    )
    val metricTranslateX by animateDpAsState(
        targetValue = if (animateIn) 0.dp else -token.spacing.xs,
        animationSpec = tween(durationMillis = 260),
        label = "metric-translate-x"
    )
    val ringScale by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0.96f,
        animationSpec = tween(durationMillis = 280),
        label = "ring-scale"
    )
    val deficitAlpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(durationMillis = 280),
        label = "deficit-alpha"
    )
    val deficitTranslateX by animateDpAsState(
        targetValue = if (animateIn) 0.dp else token.spacing.xs,
        animationSpec = tween(durationMillis = 280),
        label = "deficit-translate-x"
    )

    Card(
        modifier = modifier.wrapContentWidth(), // Chỉ nở vừa đủ nội dung
        shape = RoundedCornerShape(token.radius.xl),
        colors = CardDefaults.cardColors(containerColor = token.colors.surface),
        border = BorderStroke(token.spacing.dividerThickness, token.colors.calorieRingTrack),
        elevation = CardDefaults.cardElevation(defaultElevation = token.card.elevation)
    ) {
        Row(
            modifier = Modifier
                .padding(token.spacing.md)
                .wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(token.spacing.md)
        ) {
            // 1. Metric Section - Căn trái (Slot 1)
            Box(
                modifier = Modifier.width(sideSlotWidth),
                contentAlignment = Alignment.CenterStart
            ) {
                MetricSection(
                    data = data,
                    modifier = Modifier.graphicsLayer(
                        alpha = metricAlpha,
                        translationX = metricTranslateX.value
                    )
                )
            }

            // 2. CalorieDualRingChart - Căn giữa (Slot 2)
            CalorieDualRingChart(
                modifier = Modifier
                    .size(ringSize)
                    .graphicsLayer(
                        scaleX = ringScale,
                        scaleY = ringScale
                    ),
                intakeProgress = data.intakeProgress,
                burnedProgress = data.burnedProgress
            )

            // 3. Deficit Text - Căn phải (Slot 3)
            Box(
                modifier = Modifier.width(sideSlotWidth),
                contentAlignment = Alignment.CenterEnd
            ) {
                DeficitText(
                    deficit = data.deficit,
                    textAlign = TextAlign.End,
                    modifier = Modifier.graphicsLayer(
                        alpha = deficitAlpha,
                        translationX = deficitTranslateX.value
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
    modifier: Modifier = Modifier,
    textAlign: TextAlign
) {
    val token = GymTheme.token
    val deficitColor =
        if (deficit >= 0) token.colors.calorieDeficitPositive else token.colors.calorieDeficitNegative
    Text(
        text = if (deficit >= 0) "-${deficit} kcal" else "+${kotlin.math.abs(deficit)} kcal",
        style = token.typography.headlineSmall,
        color = deficitColor,
        textAlign = textAlign,
        modifier = modifier
    )
}


@Composable
fun MetricHorizontalLineCell(
    modifier: Modifier = Modifier,
    value: Int,
    label: String,
    color: Color
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
            Text(text = "$value", style = token.typography.bodyMedium, color = color)
            Text(text = label, style = token.typography.labelSmall, color = token.colors.textSecondary)
        }
    }
}
