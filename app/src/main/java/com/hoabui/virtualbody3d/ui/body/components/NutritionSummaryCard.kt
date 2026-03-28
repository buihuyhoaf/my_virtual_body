package com.hoabui.virtualbody3d.ui.body.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.ui.common_ui.image.LocalResourceProvider
import com.hoabui.virtualbody3d.ui.common_ui.image.toImageModel
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Light
import com.adamglin.phosphoricons.light.CaretRight
import com.adamglin.phosphoricons.light.Camera
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.body.data.CalorieGoalUiModel
import com.hoabui.virtualbody3d.ui.common_ui.atom.progress.GCircularProgress
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.mealcapture.MealMacroGroup
import com.hoabui.virtualbody3d.ui.mealcapture.MealPageUiModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.font.PlusJakartaSansFamily
import java.text.NumberFormat
import java.util.Locale

/**
 * Compact home-row summary: calorie goal progress and a stacked preview of today’s meals.
 */
@Composable
fun NutritionSummaryCard(
    nutritionToday: CalorieGoalUiModel,
    mealsForToday: List<MealPageUiModel>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val token = GymTheme.token
    val shape = RoundedCornerShape(token.radius.lg)
    val borderStroke = BorderStroke(
        width = token.borderWidth.hairline,
        color = token.colors.borderSubtle,
    )
    val numberFormat = remember {
        NumberFormat.getNumberInstance(Locale.getDefault()).apply {
            maximumFractionDigits = 0
        }
    }

    val intakeGoal = nutritionToday.intakeGoal
    val intake = nutritionToday.intake
    val consumptionProgress = if (intakeGoal <= 0) {
        0f
    } else {
        (intake.toFloat() / intakeGoal).coerceIn(0f, 1f)
    }
    val remainingPercent = if (intakeGoal <= 0) {
        0
    } else {
        val remaining = (intakeGoal - intake).coerceAtLeast(0)
        ((remaining * 100f) / intakeGoal).toInt().coerceIn(0, 100)
    }
    val caloriesLine =
        "${numberFormat.format(intake)} / ${numberFormat.format(intakeGoal)} kcal"

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        color = token.colors.surface,
        border = borderStroke,
    ) {
        Row(
            modifier = Modifier
                .padding(token.spacing.md)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(token.spacing.sm),
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(token.spacing.sm),
            ) {
                val ringSize = token.meal.nutritionSummaryRingSize
                Box(
                    modifier = Modifier.size(ringSize),
                    contentAlignment = Alignment.Center,
                ) {
                    GCircularProgress(
                        progress = consumptionProgress,
                        modifier = Modifier.size(ringSize),
                        color = token.colors.primary,
                        trackColor = token.colors.surfaceSubtle,
                        strokeWidth = token.meal.nutritionSummaryRingStrokeWidth,
                    )
                    GText(
                        text = stringResource(R.string.home_nutrition_remaining_percent, remainingPercent),
                        style = token.typography.labelSmall.copy(
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = token.colors.textPrimary,
                        textAlign = TextAlign.Center,
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(token.spacing.xxxs),
                ) {
                    GText(
                        text = stringResource(R.string.analysis_calories),
                        style = token.typography.labelSmall,
                        color = token.colors.textSecondary,
                    )
                    GText(
                        text = caloriesLine,
                        style = token.typography.titleSmall.copy(
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = token.colors.textPrimary,
                        maxLines = 1,
                    )
                }
            }
            if (mealsForToday.isNotEmpty()) {
                MealSnapshotStack(
                    meals = mealsForToday.take(3).reversed(),
                )
            } else {
                EmptyMealTrackPrompt()
            }
            Icon(
                imageVector = PhosphorIcons.Light.CaretRight,
                contentDescription = null,
                modifier = Modifier.size(token.spacing.md),
                tint = token.colors.textMuted,
            )
        }
    }
}

@Composable
private fun MealSnapshotStack(
    meals: List<MealPageUiModel>,
    modifier: Modifier = Modifier,
) {
    val resourceProvider = LocalResourceProvider.current
    val token = GymTheme.token
    val size = token.meal.nutritionSummarySnapshotSize
    val overlap = token.meal.nutritionSummarySnapshotOverlap
    val step = (size - overlap).coerceAtLeast(1.dp)
    val stackWidth = size + step * (meals.size - 1).coerceAtLeast(0)

    Box(
        modifier = modifier
            .width(stackWidth)
            .height(size),
    ) {
        meals.forEachIndexed { index, meal ->
            val xStep = step * index
            Box(
                modifier = Modifier
                    .offset(x = xStep)
                    .zIndex(index.toFloat())
                    .size(size)
                    .clip(CircleShape)
                    .border(
                        width = token.borderWidth.thin,
                        color = token.colors.background,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                val model = remember(meal.image) { meal.image.toImageModel(resourceProvider) }
                if (model != null) {
                    AsyncImage(
                        model = model,
                        contentDescription = meal.title,
                        modifier = Modifier
                            .size(size)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(size)
                            .clip(CircleShape)
                            .background(token.colors.surfaceSubtle),
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyMealTrackPrompt(
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxxs),
    ) {
        Icon(
            imageVector = PhosphorIcons.Light.Camera,
            contentDescription = null,
            modifier = Modifier.size(token.spacing.lg),
            tint = token.colors.primary.copy(alpha = 0.6f),
        )
        GText(
            text = stringResource(R.string.home_nutrition_track_meal),
            style = token.typography.labelSmall,
            color = token.colors.textMuted,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true, name = "NutritionSummaryCard — Light")
@Composable
private fun NutritionSummaryCardPreviewLight() {
    GymTheme(darkTheme = false) {
        NutritionSummaryCard(
            modifier = Modifier.padding(GymTheme.token.spacing.md),
            nutritionToday = CalorieGoalUiModel(
                intake = 1250,
                burned = 0,
                intakeGoal = 2000,
                burnGoal = 0,
            ),
            mealsForToday = listOf(
                MealPageUiModel(
                    id = "1",
                    image = ImageSource.LocalResource("body_unsplash"),
                    title = "Lunch",
                    caloriesKcal = 400,
                    caloriesText = "400 kcal",
                    macroSummaryText = "",
                    rawLines = emptyList(),
                    dominantMacro = MealMacroGroup.Protein,
                ),
            ),
        )
    }
}

@Preview(
    showBackground = true,
    name = "NutritionSummaryCard — Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun NutritionSummaryCardPreviewDark() {
    GymTheme(darkTheme = true) {
        NutritionSummaryCard(
            modifier = Modifier.padding(GymTheme.token.spacing.md),
            nutritionToday = CalorieGoalUiModel(
                intake = 800,
                burned = 0,
                intakeGoal = 2000,
                burnGoal = 0,
            ),
            mealsForToday = emptyList(),
        )
    }
}
