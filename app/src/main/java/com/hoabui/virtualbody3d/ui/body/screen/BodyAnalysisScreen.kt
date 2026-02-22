package com.hoabui.virtualbody3d.ui.body.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.core.extensions.formatMeasurement
import com.hoabui.virtualbody3d.core.extensions.formatPercent
import com.hoabui.virtualbody3d.ui.body.state.BodyDashboardUiState
import com.hoabui.virtualbody3d.ui.body.state.BodyUiState
import com.hoabui.virtualbody3d.ui.body.state.MealUiState
import com.hoabui.virtualbody3d.ui.body.state.NutritionSummaryUiState
import com.hoabui.virtualbody3d.ui.body.state.SummaryCardType
import com.hoabui.virtualbody3d.ui.body.state.SummaryCardUiState
import com.hoabui.virtualbody3d.ui.body.viewmodel.BodyViewModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun BodyAnalysisRoute(
    viewModel: BodyViewModel = hiltViewModel()
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    BodyAnalysisScreen(
        uiState = screenState.uiState,
        dashboardUiState = screenState.dashboardUiState,
        selectedDate = screenState.selectedDate ?: LocalDate.now(),
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun BodyAnalysisScreen(
    uiState: BodyUiState,
    dashboardUiState: BodyDashboardUiState,
    selectedDate: LocalDate,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val bodyScore = ((uiState.bmiScalePosition ?: 0.76f) * 100f).toInt().coerceIn(0, 100)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(token.spacing.md)
    ) {
        BodyPreviewSection(
            uiState = uiState,
            bodyScore = bodyScore,
            modifier = Modifier.weight(DashboardDefaults.heroSectionWeight)
        )
        DashboardPanel(
            selectedDate = selectedDate,
            nutritionSummary = dashboardUiState.nutrition,
            meals = dashboardUiState.meals,
            summaries = dashboardUiState.summaries,
            modifier = Modifier.weight(DashboardDefaults.dashboardSectionWeight)
        )
    }
}

@Composable
private fun DashboardPanel(
    selectedDate: LocalDate,
    nutritionSummary: NutritionSummaryUiState,
    meals: List<MealUiState>,
    summaries: List<SummaryCardUiState>,
    modifier: Modifier = Modifier
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
            NutritionCard(
                selectedDate = selectedDate,
                summary = nutritionSummary
            )
            Column(verticalArrangement = Arrangement.spacedBy(token.spacing.md)) {
                Text(
                    text = stringResource(R.string.analysis_panel_meals_today),
                    style = token.typography.titleMedium
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(token.spacing.md)) {
                    items(meals) { item ->
                        MealItem(item = item)
                    }
                }
            }
            SummaryGrid(summaries = summaries)
            Spacer(modifier = Modifier.height(bodyToken.dashboardScrollContentBottomSpacing))
        }
    }
}

@Composable
private fun NutritionCard(
    selectedDate: LocalDate,
    summary: NutritionSummaryUiState,
    modifier: Modifier = Modifier
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
                progress = progress,
                centerLabel = stringResource(R.string.analysis_dashboard_goal_value, summary.goal),
                modifier = Modifier.size(bodyToken.dashboardNutritionRingSize)
            )
        }
    }
}

@Composable
private fun MealItem(
    item: MealUiState,
    modifier: Modifier = Modifier
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
                maxLines = DashboardDefaults.mealNameMaxLines
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
    progress: Float,
    centerLabel: String,
    modifier: Modifier = Modifier
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
                startAngle = DashboardDefaults.progressRingStartAngle,
                sweepAngle = DashboardDefaults.progressRingSweepAngle,
                useCenter = false,
                style = Stroke(
                    width = bodyToken.dashboardNutritionRingStrokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            )
            drawArc(
                color = token.colors.primary,
                startAngle = DashboardDefaults.progressRingStartAngle,
                sweepAngle = DashboardDefaults.progressRingSweepAngle * progress.coerceIn(0f, 1f),
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

@Composable
private fun SummaryGrid(
    summaries: List<SummaryCardUiState>,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(token.spacing.md)
    ) {
        summaries.forEach { summary ->
            SummaryCard(
                title = summary.type.toTitle(),
                value = summary.value,
                subtitle = summary.subtitle,
                progress = summary.progress,
                icon = summary.type.toIcon(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    subtitle: String,
    progress: Float,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    Card(
        modifier = modifier.height(bodyToken.dashboardSummaryCardHeight),
        shape = RoundedCornerShape(token.radius.md),
        colors = CardDefaults.cardColors(containerColor = token.colors.dashboardSummaryCardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bodyToken.dashboardSummaryCardPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = token.typography.bodyMedium,
                    color = token.colors.textSecondary
                )
                Icon(imageVector = icon, contentDescription = title, tint = token.colors.primary)
            }
            Text(text = value, style = token.typography.titleMedium)
            Text(
                text = subtitle,
                style = token.typography.labelMedium,
                color = token.colors.textSecondary
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(token.spacing.xs)
                    .background(token.colors.dashboardRingTrack, RoundedCornerShape(token.radius.lg))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .background(token.colors.primary, RoundedCornerShape(token.radius.lg))
                )
            }
        }
    }
}


@Composable
private fun BodyPreviewSection(
    uiState: BodyUiState,
    bodyScore: Int,
    modifier: Modifier = Modifier
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
            score = bodyScore
        )
        FloatingMetricChip(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    top = bodyToken.metricChipFirstRowTopPadding,
                    start = bodyToken.metricChipSidePadding
                ),
            icon = Icons.Default.Straighten,
            value = uiState.height.formatMeasurement(uiState.heightUnit)
        )
        FloatingMetricChip(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    top = bodyToken.metricChipSecondRowTopPadding,
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
        FloatingMetricChip(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = bodyToken.metricChipFirstRowTopPadding,
                    end = bodyToken.metricChipSidePadding
                ),
            icon = Icons.Default.FitnessCenter,
            value = uiState.muscleMass.formatMeasurement(uiState.muscleMassUnit)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = bodyToken.previewTrackBottomPadding)
                .widthIn(max = bodyToken.previewTrackMaxWidth)
                .height(bodyToken.previewTrackHeight)
                .background(
                    token.colors.previewTrack,
                    RoundedCornerShape(token.radius.sm)
                )
        )
    }
}

@Composable
private fun SummaryCardType.toTitle(): String {
    return when (this) {
        SummaryCardType.Workout -> stringResource(R.string.tab_workout)
        SummaryCardType.Sleep -> stringResource(R.string.analysis_dashboard_sleep)
    }
}

private fun SummaryCardType.toIcon(): ImageVector {
    return when (this) {
        SummaryCardType.Workout -> Icons.Default.FitnessCenter
        SummaryCardType.Sleep -> Icons.Default.Hotel
    }
}

private object DashboardDefaults {
    const val heroSectionWeight: Float = 0.58f
    const val dashboardSectionWeight: Float = 0.42f
    const val progressRingStartAngle: Float = -90f
    const val progressRingSweepAngle: Float = 360f
    const val mealNameMaxLines: Int = 2
}
