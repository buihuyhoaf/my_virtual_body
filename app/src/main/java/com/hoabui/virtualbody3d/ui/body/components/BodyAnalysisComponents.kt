package com.hoabui.virtualbody3d.ui.body.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.core.extensions.formatMeasurement
import com.hoabui.virtualbody3d.core.utils.Constants
import com.hoabui.virtualbody3d.domain.model.exercise.Difficulty
import com.hoabui.virtualbody3d.ui.body.data.SupplementUiItem
import com.hoabui.virtualbody3d.ui.body.data.UpcomingExerciseHighlight
import com.hoabui.virtualbody3d.ui.body.data.UpcomingWorkoutUiItem
import com.hoabui.virtualbody3d.ui.body.screen.BodyModelPreview
import com.hoabui.virtualbody3d.ui.body.screen.BodyScoreChip
import com.hoabui.virtualbody3d.ui.body.screen.FloatingMetricChip
import com.hoabui.virtualbody3d.ui.body.state.BodyRegion
import com.hoabui.virtualbody3d.ui.body.state.BodyUiState
import com.hoabui.virtualbody3d.ui.common_ui.image.LocalResourceProvider
import com.hoabui.virtualbody3d.ui.common_ui.image.toImageModel
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.CardSize
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GImageCard
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GImageCardBadgeChrome
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GImageCardBeginnerDifficultyDot
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GImageCardHolisticCapsuleLabel
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.cardDimensions
import com.hoabui.virtualbody3d.ui.common_ui.molecule.section.GSectionHeader
import com.hoabui.virtualbody3d.ui.mealcapture.MealPageUiModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun HeroSection(
    modifier: Modifier = Modifier,
    uiState: BodyUiState,
    bodyScore: Int,
    onViewBodyDetailClick: () -> Unit = {},
    onModelInteractionChanged: (Boolean) -> Unit = {},
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    Column(verticalArrangement = Arrangement.spacedBy(token.spacing.xs)) {
        GSectionHeader(
            title = stringResource(R.string.home_section_body),
            actionText = stringResource(R.string.home_section_see_more),
            onActionClick = onViewBodyDetailClick,
        )
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
            BodyModelPreview(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(token.radius.lg)),
                onInteractionChanged = onModelInteractionChanged
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
                    iconResId = R.drawable.ruler_vertical,
                    value = uiState.height.formatMeasurement(Constants.CENTIMETER)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(
                        bottom = bodyToken.scoreChipTopPadding,
                        start = bodyToken.metricChipSidePadding
                    ),
                verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
            ) {
                FloatingMetricChip(
                    iconResId = R.drawable.scale,
                    value = uiState.bodyFat.formatMeasurement(Constants.PERCENT)
                )
                FloatingMetricChip(
                    iconResId = R.drawable.scale,
                    value = uiState.muscleMass.formatMeasurement(Constants.PERCENT)
                )
            }
        }
    }
}

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
 * Shared section with title and horizontal LazyRow. Use for IncommingExercisesRow and SupplementsRow.
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
    onAddExerciseClick: () -> Unit = {},
    onSeeMoreClick: (() -> Unit)? = null
) {
    val token = GymTheme.token
    val resourceProvider = LocalResourceProvider.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
    ) {
        GSectionHeader(
            title = stringResource(R.string.home_section_incomming_exercises),
            actionText = if (onSeeMoreClick != null) stringResource(R.string.home_section_see_more) else null,
            onActionClick = onSeeMoreClick,
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(token.spacing.sm),
            contentPadding = PaddingValues(horizontal = token.spacing.md),
        ) {
            items(
                items = exercises,
                key = { it.id }
            ) { item ->
                GImageCard(
                    model = item.image.toImageModel(resourceProvider),
                    contentDescription = item.name,
                    firstLineText = item.name,
                    secondLineText = stringResource(
                        R.string.home_upcoming_reps_sets,
                        item.reps,
                        item.sets,
                    ),
                    cardSize = CardSize.Small,
                    textSectionLeading = if (item.difficulty == Difficulty.Beginner) {
                        { GImageCardBeginnerDifficultyDot() }
                    } else {
                        null
                    },
                    badge = if (item.highlight == UpcomingExerciseHighlight.New) {
                        { GImageCardHolisticCapsuleLabel(stringResource(R.string.home_exercise_badge_new)) }
                    } else {
                        null
                    },
                    badgeChrome = GImageCardBadgeChrome.Holistic,
                    onClick = {},
                )
            }
            item(key = "add_exercise") {
                AddCard(cardSize = CardSize.Small, onClick = onAddExerciseClick)
            }
        }
    }
}


@Composable
private fun AddCard(
    modifier: Modifier = Modifier,
    cardSize: CardSize = CardSize.Medium,
    onClick: () -> Unit = {}
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val (cardWidth, cardHeight) = bodyToken.cardImageWithText.cardDimensions(cardSize)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = if (isPressed) 0.97f else 1f
    val cardCorner = when (cardSize) {
        CardSize.Small -> bodyToken.gImageCardCornerRadius
        CardSize.Medium, CardSize.Large -> token.radius.lg
    }
    val imageCorner = RoundedCornerShape(bodyToken.gImageCardCornerRadius)
    val addCardBorder = BorderStroke(
        token.borderWidth.hairlineSubtle,
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
    )
    Card(
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(cardCorner),
        colors = CardDefaults.cardColors(containerColor = token.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = token.elevation.level0),
        border = addCardBorder
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardWidth)
                    .clip(imageCorner),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = token.colors.primary,
                    modifier = Modifier.size(token.spacing.xl)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
    }
}

/**
 * Horizontal row of supplement cards for the home dashboard.
 * Reuses [SectionHorizontalRow] so layout matches [IncommingExercisesRow].
 */
@Composable
fun SupplementsRow(
    modifier: Modifier = Modifier,
    supplements: List<SupplementUiItem>
) {
    SectionHorizontalRow(titleResId = R.string.home_section_supplements, modifier = modifier) {
        items(
            items = supplements,
            key = { "${it.name}-${it.nutrient}" }
        ) { item ->
            GImageCard(
                model = item.imageResId,
                contentDescription = item.name,
                firstLineText = item.name,
                secondLineText = item.nutrient,
                cardSize = CardSize.Large,
                onClick = {},
            )
        }
        item(key = "add_supplements") {
            AddCard(cardSize = CardSize.Large, onClick = {})
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
                Image(
                    painter = painterResource(R.drawable.body_unsplash),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

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
