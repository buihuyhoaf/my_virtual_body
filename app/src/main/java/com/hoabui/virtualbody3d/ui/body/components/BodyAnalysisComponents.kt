package com.hoabui.virtualbody3d.ui.body.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillParentMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.core.extensions.formatMeasurement
import com.hoabui.virtualbody3d.core.utils.Constants
import com.hoabui.virtualbody3d.ui.body.data.UpcomingWorkoutUiItem
import com.hoabui.virtualbody3d.ui.body.screen.BodyModelPreview
import com.hoabui.virtualbody3d.ui.body.screen.BodyScoreChip
import com.hoabui.virtualbody3d.ui.body.screen.FloatingMetricChip
import com.hoabui.virtualbody3d.ui.body.state.BodyRegion
import com.hoabui.virtualbody3d.ui.body.state.BodyUiState
import com.hoabui.virtualbody3d.ui.common_ui.image.LocalResourceProvider
import com.hoabui.virtualbody3d.ui.common_ui.image.toImageModel
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GUpcomingExerciseCard
import com.hoabui.virtualbody3d.ui.common_ui.molecule.section.GSectionHeader
import com.hoabui.virtualbody3d.ui.common_ui.molecule.state.GStatePanel
import com.hoabui.virtualbody3d.ui.mealcapture.MealPageUiModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Compact dashboard card showing body progress: Muscle Mass, Body Fat, Weight.
 * Each metric shows value, optional change indicator (+0.9 ↑ / -1.2 ↓), and label.
 */
@Composable
fun MuscleProgressCard(
    modifier: Modifier = Modifier,
    uiState: BodyUiState
) {
    val token = GymTheme.token
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(token.radius.lg),
        colors = CardDefaults.cardColors(containerColor = token.colors.dashboardSummaryCardBackground),
        border = BorderStroke(
            width = token.bodyAnalysis.topBarBorderWidth,
            color = token.colors.borderSubtle
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(token.card.padding),
            verticalArrangement = Arrangement.spacedBy(token.spacing.md)
        ) {
            GText(
                text = stringResource(R.string.analysis_body_progress),
                style = token.typography.titleMedium,
                color = token.colors.textPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BodyProgressMetricItem(
                    value = uiState.muscleMass.ifEmpty { stringResource(R.string.body_placeholder) },
                    change = uiState.muscleMassProgress,
                    label = stringResource(R.string.body_muscle_mass),
                    unit = stringResource(R.string.body_unit_kg)
                )
                BodyProgressMetricItem(
                    value = uiState.bodyFat.ifEmpty { stringResource(R.string.body_placeholder) },
                    change = uiState.bodyFatProgress,
                    label = stringResource(R.string.body_body_fat),
                    unit = stringResource(R.string.body_unit_percent)
                )
                BodyProgressMetricItem(
                    value = uiState.weight.ifEmpty { stringResource(R.string.body_placeholder) },
                    change = uiState.weightProgress,
                    label = stringResource(R.string.body_weight),
                    unit = stringResource(R.string.body_unit_kg)
                )
            }
        }
    }
}

@Composable
private fun BodyProgressMetricItem(
    modifier: Modifier = Modifier,
    value: String,
    change: Float?,
    label: String,
    unit: String
) {
    val token = GymTheme.token
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxs)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            GText(
                text = value,
                style = token.typography.titleMedium,
                color = token.colors.textPrimary
            )
            if (value != stringResource(R.string.body_placeholder)) {
                GText(
                    text = unit,
                    style = token.typography.labelSmall,
                    color = token.colors.textSecondary,
                    modifier = Modifier.padding(start = token.spacing.xxs)
                )
            }
        }
        change?.let { delta ->
            val isPositive = delta >= 0f
            val sign = if (isPositive) "+" else ""
            val arrow = if (isPositive) "↑" else "↓"
            GText(
                text = "$sign${"%.1f".format(delta)} $arrow",
                style = token.typography.labelSmall,
                color = if (isPositive) token.colors.primary else token.colors.error
            )
        }
        GText(
            text = label,
            style = token.typography.labelSmall,
            color = token.colors.textSecondary
        )
    }
}

/**
 * Shared section with title and horizontal LazyRow.
 * Reduces spacing between section title and row content (xs) for a tighter block.
 */
@Composable
fun SectionHorizontalRow(
    modifier: Modifier = Modifier,
    @StringRes titleResId: Int? = null,
    onSeeMoreClick: (() -> Unit)? = null,
    content: LazyListScope.() -> Unit
) {
    val token = GymTheme.token
    Column(
        verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
    ) {
        if (titleResId != null) {
            GSectionHeader(
                title = stringResource(titleResId),
                actionText = if (onSeeMoreClick != null) stringResource(R.string.home_section_see_more) else null,
                onActionClick = onSeeMoreClick,
            )
        }
        LazyRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(token.spacing.md),
            contentPadding = PaddingValues(
                horizontal = token.spacing.md,
                vertical = token.spacing.md
            ),
            content = content
        )
    }
}

@Composable
fun UpcomingExercisesRow(
    modifier: Modifier = Modifier,
    exercises: List<UpcomingWorkoutUiItem>,
    onSeeMoreClick: (() -> Unit)? = null
) {
    val token = GymTheme.token
    val hasExercises = exercises.isNotEmpty()
    val resourceProvider = LocalResourceProvider.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
    ) {
        GSectionHeader(
            title = stringResource(R.string.home_section_incomming_exercises),
            actionText = if (hasExercises && onSeeMoreClick != null) {
                stringResource(R.string.home_section_see_more)
            } else {
                null
            },
            onActionClick = onSeeMoreClick,
        )
        if (hasExercises) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(token.spacing.sm),
            ) {
                items(
                    items = exercises,
                    key = { it.id }
                ) { item ->
                    GUpcomingExerciseCard(
                        model = item.image.toImageModel(resourceProvider),
                        title = item.name,
                        subtitle = when (item.measurementMode) {
                            ExerciseMeasurementMode.Duration -> {
                                val total = item.durationSeconds ?: 0
                                val m = total / 60
                                val s = total % 60
                                stringResource(R.string.home_upcoming_duration_subtitle, m, s)
                            }
                            ExerciseMeasurementMode.Strength ->
                                stringResource(
                                    R.string.home_upcoming_chip_subtitle,
                                    item.reps,
                                    item.sets,
                                )
                        },
                        onClick = {},
                    )
                }
            }
        } else {
            GStatePanel(
                title = stringResource(R.string.workout_calendar_rest_day_title),
                subtitle = stringResource(R.string.workout_calendar_rest_day_subtitle),
                modifier = Modifier
                    .fillParentMaxHeight(REST_DAY_PANEL_HEIGHT_FRACTION)
                    .fillMaxWidth()
                    .clickable(enabled = onSeeMoreClick != null) { onSeeMoreClick?.invoke() },
                icon = {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        modifier = Modifier.size(token.spacing.xl),
                        tint = token.colors.textMuted,
                    )
                },
            )
        }
    }
}

private const val REST_DAY_PANEL_HEIGHT_FRACTION = 0.6f

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
                    colors = listOf(token.colors.primarySoft, token.colors.surface)
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
                iconResId = R.drawable.scale,
                value = uiState.weight.formatMeasurement(Constants.KILOGRAM)
            )
            FloatingMetricChip(
                iconResId = R.drawable.scale,
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
        modifier = modifier,
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
            GText(
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
private fun MealItem(
    modifier: Modifier = Modifier,
    item: MealPageUiModel
) {
    val resourceProvider = LocalResourceProvider.current
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val imageModel = remember(item.image) { item.image.toImageModel(resourceProvider) }
    Card(
        modifier = modifier.width(bodyToken.dashboardMealItemWidth),
        shape = RoundedCornerShape(token.radius.md),
        colors = CardDefaults.cardColors(containerColor = token.colors.dashboardMealCardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bodyToken.dashboardCalorieCardPadding),
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
                if (imageModel != null) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.body_unsplash),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Calories badge overlaid at bottom-end
                if (item.caloriesKcal > 0) {
                    GSurface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(token.spacing.xs),
                        shape = RoundedCornerShape(token.radius.sm),
                        color = token.colors.surfaceElevated,
                        shadowElevation = token.card.elevation,
                    ) {
                        GText(
                            text = stringResource(
                                R.string.analysis_dashboard_meal_kcal_badge,
                                item.caloriesKcal
                            ),
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
            GText(
                text = item.title,
                style = token.typography.bodyMedium,
                color = token.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
