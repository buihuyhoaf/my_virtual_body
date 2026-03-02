package com.hoabui.virtualbody3d.ui.body.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.core.extensions.formatMeasurement
import com.hoabui.virtualbody3d.core.extensions.formatPercent
import com.hoabui.virtualbody3d.core.utils.Constants
import com.hoabui.virtualbody3d.ui.body.screen.BodyModelPreview
import com.hoabui.virtualbody3d.ui.body.screen.BodyScoreChip
import com.hoabui.virtualbody3d.ui.body.screen.FloatingMetricChip
import com.hoabui.virtualbody3d.ui.body.state.BodyUiState
import com.hoabui.virtualbody3d.ui.body.state.MealUiState
import com.hoabui.virtualbody3d.ui.body.state.NutritionSummaryUiState
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HeroSection(
    modifier: Modifier = Modifier,
    uiState: BodyUiState,
    bodyScore: Int
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(token.radius.lg))
            .background(
                brush = Brush.radialGradient(
                    center = Offset(0.5f, 0.5f),
                    radius = 1.2f,
                    colors = listOf(token.colors.primarySoft, MaterialTheme.colorScheme.surface)
                )
            )
    ) {
        BodyModelPreview(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = bodyToken.previewModelTopPadding)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            token.colors.backgroundTransparent,
                            token.colors.backgroundScrim
                        )
                    )
                )
        )
        BodyScoreChip(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    top = bodyToken.scoreChipTopPadding,
                    start = bodyToken.metricChipSidePadding
                ),
            score = bodyScore,
            prominent = true
        )
        FloatingMetricChip(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    top = bodyToken.metricChipFirstRowTopPadding,
                    start = bodyToken.metricChipSidePadding
                ),
            icon = Icons.Default.MonitorWeight,
            value = uiState.weight.formatMeasurement(uiState.weightUnit)
        )
        FloatingMetricChip(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = bodyToken.scoreChipTopPadding,
                    end = bodyToken.metricChipSidePadding
                ),
            icon = Icons.Default.Opacity,
            value = uiState.bodyFat.formatPercent()
        )
        TextButton(
            onClick = { },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = bodyToken.previewTrackBottomPadding)
        ) {
            Text(
                text = stringResource(R.string.analysis_view_detailed_analysis),
                style = token.typography.labelMedium,
                color = token.colors.primary
            )
        }
    }
}

@Composable
fun DashboardPanel(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    nutritionSummary: NutritionSummaryUiState,
    meals: List<MealUiState>
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val displayedMeals = meals.take(Constants.BODY_ANALYSIS_MAX_MEALS_DISPLAYED)
    val showViewAll = meals.size > Constants.BODY_ANALYSIS_MAX_MEALS_DISPLAYED

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
            NutritionCard(
                selectedDate = selectedDate,
                summary = nutritionSummary
            )
            Column(verticalArrangement = Arrangement.spacedBy(token.spacing.md)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.analysis_panel_meals_today),
                        style = token.typography.titleMedium
                    )
                    if (showViewAll) {
                        TextButton(onClick = {}) {
                            Text(
                                text = stringResource(R.string.analysis_panel_see_all),
                                style = token.typography.labelMedium,
                                color = token.colors.primary
                            )
                        }
                    }
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(token.spacing.md)) {
                    items(
                        items = displayedMeals,
                        key = { it.name }
                    ) { item ->
                        MealItem(item = item)
                    }
                }
            }
            Spacer(modifier = Modifier.height(bodyToken.dashboardScrollContentBottomSpacing))
        }
    }
}

@Composable
private fun NutritionCard(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    summary: NutritionSummaryUiState
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val netCalories = summary.intake - summary.burned
    val progress = (summary.intake.toFloat() / summary.goal.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(token.radius.lg),
        colors = CardDefaults.cardColors(containerColor = token.colors.dashboardNutritionCardBackground),
        border = androidx.compose.foundation.BorderStroke(
            width = bodyToken.topBarBorderWidth,
            color = token.colors.dashboardNutritionCardBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bodyToken.dashboardNutritionCardPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
            ) {
                Text(
                    text = selectedDate.format(
                        DateTimeFormatter.ofPattern("MMM dd • EEE", Locale.ENGLISH)
                    ),
                    style = token.typography.bodyMedium,
                    color = token.colors.textSecondary
                )
                Text(
                    text = stringResource(R.string.analysis_dashboard_kcal_value, netCalories),
                    style = token.typography.titleLarge
                )
                Text(
                    text = stringResource(
                        R.string.analysis_dashboard_intake_burned,
                        summary.intake,
                        summary.burned
                    ),
                    style = token.typography.bodyMedium,
                    color = token.colors.textSecondary
                )
            }
            CaloriesProgressRing(
                modifier = Modifier.size(bodyToken.dashboardNutritionRingSize),
                progress = progress,
                centerLabel = stringResource(R.string.analysis_dashboard_goal_value, summary.goal)
            )
        }
    }
}

@Composable
private fun MealItem(
    modifier: Modifier = Modifier,
    item: MealUiState
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    Card(
        modifier = modifier.width(bodyToken.dashboardMealItemWidth),
        shape = RoundedCornerShape(token.radius.md),
        colors = CardDefaults.cardColors(containerColor = token.colors.dashboardMealCardBackground)
    ) {
        Column(
            modifier = Modifier.padding(bodyToken.dashboardNutritionCardPadding),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
        ) {
            Box(
                modifier = Modifier
                    .size(bodyToken.dashboardMealItemImageSize)
                    .background(
                        color = token.colors.dashboardMealImageBackground,
                        shape = RoundedCornerShape(token.radius.md)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalDining,
                    contentDescription = item.name,
                    tint = token.colors.primary
                )
            }
            Text(
                text = item.name,
                style = token.typography.bodyMedium,
                maxLines = Constants.BODY_ANALYSIS_MEAL_NAME_MAX_LINES
            )
            Text(
                text = stringResource(R.string.analysis_panel_kcal_value, item.calories),
                style = token.typography.labelMedium,
                color = token.colors.textSecondary
            )
        }
    }
}

@Composable
private fun CaloriesProgressRing(
    modifier: Modifier = Modifier,
    progress: Float,
    centerLabel: String
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = token.colors.dashboardRingTrack,
                startAngle = Constants.BODY_ANALYSIS_PROGRESS_RING_START_ANGLE,
                sweepAngle = Constants.BODY_ANALYSIS_PROGRESS_RING_SWEEP_ANGLE,
                useCenter = false,
                style = Stroke(
                    width = bodyToken.dashboardNutritionRingStrokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            )
            drawArc(
                color = token.colors.primary,
                startAngle = Constants.BODY_ANALYSIS_PROGRESS_RING_START_ANGLE,
                sweepAngle = Constants.BODY_ANALYSIS_PROGRESS_RING_SWEEP_ANGLE * progress.coerceIn(0f, 1f),
                useCenter = false,
                style = Stroke(
                    width = bodyToken.dashboardNutritionRingStrokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }
        Text(
            text = centerLabel,
            style = token.typography.labelMedium,
            textAlign = TextAlign.Center
        )
    }
}
