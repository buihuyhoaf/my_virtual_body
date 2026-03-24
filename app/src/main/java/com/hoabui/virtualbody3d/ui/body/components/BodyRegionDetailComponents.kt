package com.hoabui.virtualbody3d.ui.body.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.molecule.info.GInfoRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.card.GCard
import com.hoabui.virtualbody3d.ui.common_ui.atom.progress.GProgressBar
import com.hoabui.virtualbody3d.ui.theme.GymTheme

internal data class ExerciseItem(
    val nameResId: Int,
    val descResId: Int,
    val difficultyResId: Int,
)

@Composable
internal fun MuscleCompositionCard(modifier: Modifier = Modifier) {
    val token = GymTheme.token
    GCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = token.colors.surfaceOverlay,
    ) {
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.md),
        ) {
            GText(
                text = stringResource(R.string.body_region_detail_muscle_composition),
                style = token.typography.titleSmall,
                color = token.colors.textPrimary,
            )
            HorizontalBarRow(
                label = stringResource(R.string.body_region_detail_muscle_mass),
                value = "78%",
                progress = 0.78f,
            )
            HorizontalBarRow(
                label = stringResource(R.string.body_region_detail_fat_percentage),
                value = "22%",
                progress = 0.22f,
            )
        }
    }
}

@Composable
internal fun PerformanceCard(modifier: Modifier = Modifier) {
    val token = GymTheme.token
    GCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = token.colors.surfaceOverlay,
    ) {
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.md),
        ) {
            GText(
                text = stringResource(R.string.body_region_detail_performance),
                style = token.typography.titleSmall,
                color = token.colors.textPrimary,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(token.spacing.lg),
            ) {
                RadialScoreBox(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.body_region_detail_strength_score),
                    score = 75,
                )
                RadialScoreBox(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.body_region_detail_endurance),
                    score = 68,
                )
            }
        }
    }
}

@Composable
internal fun BalanceCard(modifier: Modifier = Modifier) {
    val token = GymTheme.token
    GCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = token.colors.surfaceOverlay,
    ) {
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.md),
        ) {
            GText(
                text = stringResource(R.string.body_region_detail_balance),
                style = token.typography.titleSmall,
                color = token.colors.textPrimary,
            )
            BalanceBarRow(
                leftLabel = stringResource(R.string.body_region_detail_left_right_balance),
                leftValue = 52,
                rightValue = 48,
            )
        }
    }
}

@Composable
internal fun HealthIndicatorsCard(modifier: Modifier = Modifier) {
    val token = GymTheme.token
    GCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = token.colors.surfaceOverlay,
    ) {
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.md),
        ) {
            GText(
                text = stringResource(R.string.body_region_detail_health_indicators),
                style = token.typography.titleSmall,
                color = token.colors.textPrimary,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(token.spacing.lg),
            ) {
                RadialScoreBox(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.body_region_detail_flexibility),
                    score = 82,
                )
                RadialScoreBox(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.body_region_detail_recovery),
                    score = 70,
                )
            }
        }
    }
}

@Composable
internal fun HorizontalBarRow(
    label: String,
    value: String,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxs),
    ) {
        GInfoRow(label = label, value = value)
        GProgressBar(progress = progress)
    }
}

@Composable
internal fun RadialScoreBox(
    label: String,
    score: Int,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(token.radius.md),
        color = token.colors.surfaceSubtle,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(token.spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(56.dp),
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 6.dp.toPx()
                    drawArc(
                        color = token.colors.surfaceBorder,
                        startAngle = 90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(size.minDimension - strokeWidth, size.minDimension - strokeWidth),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    )
                    drawArc(
                        color = token.colors.primary,
                        startAngle = 90f,
                        sweepAngle = -360f * (score / 100f),
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(size.minDimension - strokeWidth, size.minDimension - strokeWidth),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    )
                }
                GText(
                    text = "$score",
                    style = token.typography.titleMedium,
                    color = token.colors.textPrimary,
                )
            }
            GText(
                text = label,
                style = token.typography.labelSmall,
                color = token.colors.textSecondary,
            )
        }
    }
}

@Composable
internal fun BalanceBarRow(
    leftLabel: String,
    leftValue: Int,
    rightValue: Int,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val total = (leftValue + rightValue).toFloat().coerceAtLeast(1f)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
    ) {
        GText(
            text = leftLabel,
            style = token.typography.bodySmall,
            color = token.colors.textSecondary,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(token.spacing.lg)
                .clip(RoundedCornerShape(token.radius.sm))
                .background(token.colors.surfaceSubtle),
            horizontalArrangement = Arrangement.Start,
        ) {
            Box(
                modifier = Modifier
                    .weight(leftValue.toFloat() / total)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(token.radius.sm))
                    .background(token.colors.primary),
            )
            Box(
                modifier = Modifier
                    .weight(rightValue.toFloat() / total)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(token.radius.sm))
                    .background(token.colors.primary.copy(alpha = 0.5f)),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            GText(
                text = "L $leftValue%",
                style = token.typography.labelSmall,
                color = token.colors.textSecondary,
            )
            GText(
                text = "R $rightValue%",
                style = token.typography.labelSmall,
                color = token.colors.textSecondary,
            )
        }
    }
}

@Composable
internal fun RecommendedExercisesSection(
    regionDisplayName: String,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val exercises = listOf(
        ExerciseItem(R.string.body_region_detail_exercise_1_name, R.string.body_region_detail_exercise_1_desc, R.string.body_region_detail_difficulty_easy),
        ExerciseItem(R.string.body_region_detail_exercise_2_name, R.string.body_region_detail_exercise_2_desc, R.string.body_region_detail_difficulty_medium),
        ExerciseItem(R.string.body_region_detail_exercise_3_name, R.string.body_region_detail_exercise_3_desc, R.string.body_region_detail_difficulty_medium),
        ExerciseItem(R.string.body_region_detail_exercise_4_name, R.string.body_region_detail_exercise_4_desc, R.string.body_region_detail_difficulty_hard),
    )
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = token.spacing.xxs),
        horizontalArrangement = Arrangement.spacedBy(token.spacing.md),
    ) {
        items(exercises, key = { it.nameResId }) { item ->
            ExerciseCard(
                name = stringResource(item.nameResId),
                description = stringResource(item.descResId),
                difficulty = stringResource(item.difficultyResId),
                targetMuscle = regionDisplayName,
            )
        }
    }
}

@Composable
internal fun ExerciseCard(
    name: String,
    description: String,
    difficulty: String,
    targetMuscle: String,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    GCard(
        modifier = modifier.width(200.dp),
        containerColor = token.colors.surfaceOverlay,
    ) {
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
        ) {
            GText(
                text = name,
                style = token.typography.titleSmall,
                color = token.colors.textPrimary,
            )
            GText(
                text = description,
                style = token.typography.bodySmall,
                color = token.colors.textSecondary,
                maxLines = 2,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GSurface(
                    shape = RoundedCornerShape(token.radius.sm),
                    color = token.colors.primarySoft,
                ) {
                    GText(
                        text = difficulty,
                        style = token.typography.labelSmall,
                        color = token.colors.primary,
                        modifier = Modifier.padding(horizontal = token.spacing.xs, vertical = 2.dp),
                    )
                }
                GText(
                    text = targetMuscle,
                    style = token.typography.labelSmall,
                    color = token.colors.textSecondary,
                )
            }
        }
    }
}

@Composable
internal fun RealLifeApplicationsCard(modifier: Modifier = Modifier) {
    val token = GymTheme.token
    val bullets = listOf(
        R.string.body_region_detail_real_life_walking,
        R.string.body_region_detail_real_life_posture,
        R.string.body_region_detail_real_life_lifting,
        R.string.body_region_detail_real_life_stability,
        R.string.body_region_detail_real_life_daily_tasks,
    )
    GCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = token.colors.surfaceOverlay,
    ) {
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
        ) {
            bullets.forEach { resId ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(token.colors.primary),
                    )
                    Spacer(modifier = Modifier.width(token.spacing.xs))
                    GText(
                        text = stringResource(resId),
                        style = token.typography.bodyMedium,
                        color = token.colors.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
