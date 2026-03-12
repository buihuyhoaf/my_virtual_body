package com.hoabui.virtualbody3d.ui.body.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateRight
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.core.extensions.formatMeasurement
import com.hoabui.virtualbody3d.core.extensions.heroLayerAnimation
import com.hoabui.virtualbody3d.core.utils.Constants
import com.hoabui.virtualbody3d.ui.body.screen.BodyModelPreview
import com.hoabui.virtualbody3d.ui.body.screen.BodyScoreChip
import com.hoabui.virtualbody3d.ui.body.screen.FloatingMetricChip
import com.hoabui.virtualbody3d.ui.body.state.BodyRegion
import com.hoabui.virtualbody3d.ui.body.state.BodyUiState
import com.hoabui.virtualbody3d.ui.body.state.NutritionSummaryUiState
import com.hoabui.virtualbody3d.ui.mealcapture.MealPageUiModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HeroSection(
    modifier: Modifier = Modifier,
    uiState: BodyUiState,
    bodyScore: Int,
    showRotateChip: Boolean = false
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    var showImageMode by remember { mutableStateOf(false) }

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
                .clip(RoundedCornerShape(token.radius.lg))
                .heroLayerAnimation(showImageMode, forImageLayer = false)
        )
        Image(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(token.radius.lg))
                .heroLayerAnimation(showImageMode, forImageLayer = true),
            painter = painterResource(R.drawable.body_unsplash),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        if (!showImageMode){
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
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        top = bodyToken.scoreChipTopPadding,
                        end = bodyToken.metricChipSidePadding
                    ),
                verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
            ) {
                FloatingMetricChip(
                    icon = Icons.Default.MonitorWeight,
                    value = uiState.weight.formatMeasurement(Constants.KILOGRAM)
                )
                FloatingMetricChip(
                    icon = Icons.Default.Opacity,
                    value = uiState.height.formatMeasurement(Constants.CENTIMETER)
                )
            }
        }
        if (showRotateChip) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        bottom = bodyToken.previewTrackBottomPadding,
                        end = bodyToken.metricChipSidePadding
                    )
                    .clickable { showImageMode = !showImageMode },
                shape = RoundedCornerShape(token.radius.lg),
                color = token.colors.surfaceOverlay,
                border = BorderStroke(bodyToken.topBarBorderWidth, token.colors.surfaceBorder),
                shadowElevation = token.card.elevation
            ) {
                Box(
                    modifier = Modifier
                        .padding(token.spacing.xs)
                        .size(bodyToken.topBarIconSize),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.RotateRight,
                        contentDescription = stringResource(R.string.analysis_rotate_to_image),
                        tint = token.colors.primary
                    )
                }
            }
        }
    }
}

@Composable
fun StaticHeroSection(
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
        Image(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(token.radius.lg)),
            painter = painterResource(R.drawable.body_unsplash),
            contentDescription = null,
            contentScale = ContentScale.Crop
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
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = bodyToken.scoreChipTopPadding,
                    end = bodyToken.metricChipSidePadding
                ),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
        ) {
            FloatingMetricChip(
                icon = Icons.Default.MonitorWeight,
                value = uiState.weight.formatMeasurement(Constants.KILOGRAM)
            )
            FloatingMetricChip(
                icon = Icons.Default.Opacity,
                value = uiState.height.formatMeasurement(Constants.CENTIMETER)
            )
        }
    }
}

@Composable
fun BodyRegionRow(
    modifier: Modifier = Modifier,
    onRegionClick: (BodyRegion) -> Unit = {}
) {
    val token = GymTheme.token
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(token.spacing.md),
        contentPadding = PaddingValues(horizontal = token.spacing.xs)
    ) {
        items(
            items = BodyRegion.entries,
            key = { it.name }
        ) { region ->
            BodyRegionItem(
                region = region,
                onClick = {
                    onRegionClick(region)
                }
            )
        }
    }
}

@Composable
fun BodyRegionItem(
    region: BodyRegion,
    onClick: () -> Unit
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val surfaceColor = token.colors.surfaceOverlay
    val borderColor = token.colors.surfaceBorder

    Surface(
        modifier = Modifier
            .width(bodyToken.bodyRegionItemWidth)
            .height(bodyToken.bodyRegionItemHeight)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(token.radius.lg),
        color = surfaceColor,
        border = BorderStroke(
            width = bodyToken.topBarBorderWidth,
            color = borderColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
        ) {
            Box(
                modifier = Modifier
                    .size(bodyToken.bodyRegionPlaceholderSize)
                    .background(
                        color = token.colors.dashboardMealImageBackground,
                        shape = RoundedCornerShape(token.radius.md)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(token.radius.md)),
                    painter = painterResource(id = R.drawable.muscles),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                text = when (region) {
                    BodyRegion.UpperBody -> stringResource(R.string.body_region_upper_body)
                    BodyRegion.Core -> stringResource(R.string.body_region_core)
                    BodyRegion.Glutes -> stringResource(R.string.body_region_glutes)
                    BodyRegion.Thighs -> stringResource(R.string.body_region_thighs)
                    BodyRegion.Arms -> stringResource(R.string.body_region_arms)
                },
                style = token.typography.labelMedium,
                color = token.colors.textSecondary
            )
        }
    }
}

@Composable
fun CaloriesTodayPanel(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    nutritionSummary: NutritionSummaryUiState,
    meals: List<MealPageUiModel>
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
                        key = { it.title }
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
        border = BorderStroke(
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
    item: MealPageUiModel
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    Card(
        modifier = modifier.width(bodyToken.dashboardMealItemWidth),
        shape = RoundedCornerShape(token.radius.md),
        colors = CardDefaults.cardColors(containerColor = token.colors.dashboardMealCardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bodyToken.dashboardNutritionCardPadding),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
        ) {
            // Image box: full-width square with rounded corners
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(token.radius.md))
                    .background(
                        color = token.colors.dashboardMealImageBackground,
                        shape = RoundedCornerShape(token.radius.md)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalDining,
                    contentDescription = item.title,
                    tint = token.colors.primary
                )

                // Calories badge overlaid at bottom-end
                if (item.caloriesKcal > 0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(token.spacing.xs),
                        shape = RoundedCornerShape(token.radius.sm),
                        color = token.colors.surfaceElevated,
                        shadowElevation = token.card.elevation
                    ) {
                        Text(
                            text = "🔥 ${item.caloriesKcal} kcal",
                            style = token.typography.labelSmall,
                            color = token.colors.textPrimary,
                            modifier = Modifier.padding(
                                horizontal = token.spacing.xs,
                                vertical = token.spacing.xxs
                            )
                        )
                    }
                }
            }

            // Meal title below image
            Text(
                text = item.title,
                style = token.typography.bodyMedium,
                color = token.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
                sweepAngle = Constants.BODY_ANALYSIS_PROGRESS_RING_SWEEP_ANGLE * progress.coerceIn(
                    0f,
                    1f
                ),
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
