package com.hoabui.virtualbody3d.ui.body.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.body.state.ActivityItem
import com.hoabui.virtualbody3d.ui.body.state.BodyUiState
import com.hoabui.virtualbody3d.ui.body.state.FoodLogItem
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun AnalysisPanel(
    uiState: BodyUiState,
    modifier: Modifier = Modifier,
    bodyScore: Int = ((uiState.bmiScalePosition ?: 0.76f) * 100f).toInt()
) {
    val dailyGoal = 3520
    val intake = 2100
    val burned = 680
    val netRemaining = (dailyGoal - intake + burned).coerceAtLeast(0)
    val progress = ((intake - burned).toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f)

    val meals = listOf(
        FoodLogItem(
            stringResource(R.string.analysis_panel_meal_quinoa_power_bowl),
            420,
            protein = 24,
            carb = 45,
            fat = 12,
            mealType = stringResource(R.string.analysis_panel_meal_type_lunch)
        ),
        FoodLogItem(
            stringResource(R.string.analysis_panel_meal_greek_yogurt),
            250,
            protein = 18,
            carb = 22,
            fat = 8,
            mealType = stringResource(R.string.analysis_panel_meal_type_snack)
        ),
        FoodLogItem(
            stringResource(R.string.analysis_panel_meal_salmon_rice),
            640,
            protein = 42,
            carb = 48,
            fat = 22,
            mealType = stringResource(R.string.analysis_panel_meal_type_dinner)
        )
    )
    val activities = listOf(
        ActivityItem(
            stringResource(R.string.analysis_panel_activity_morning_run),
            stringResource(R.string.analysis_panel_activity_morning_run_duration),
            stringResource(R.string.analysis_panel_activity_intensity_high),
            450,
            0.85f
        ),
        ActivityItem(
            stringResource(R.string.analysis_panel_activity_evening_walk),
            stringResource(R.string.analysis_panel_activity_evening_walk_duration),
            stringResource(R.string.analysis_panel_activity_intensity_low),
            140,
            0.35f
        )
    )
    val weeklyBars = listOf(0.64f, 0.48f, 0.8f, 0.56f, 0.4f, 0.72f, 0.6f)
    val token = GymTheme.token
    val panelShape = RoundedCornerShape(token.radius.lg)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(panelShape),
        shape = panelShape,
        color = token.colors.surface,
        shadowElevation = token.card.elevation,
        tonalElevation = token.card.elevation
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = token.spacing.lg,
                        end = token.spacing.lg,
                        top = token.spacing.xl,
                        bottom = token.spacing.lg
                    ),
                verticalArrangement = Arrangement.spacedBy(token.spacing.lg)
            ) {
                CalorieHeroCard(
                    netRemaining = netRemaining,
                    intake = intake,
                    burned = burned,
                    goal = dailyGoal,
                    progress = progress
                )
                MealsSection(items = meals)
                ActivitiesSection(items = activities)
                WeeklyAnalyticsCard(bars = weeklyBars)
            }
        }
    }
}

@Composable
private fun CalorieHeroCard(
    netRemaining: Int,
    intake: Int,
    burned: Int,
    goal: Int,
    progress: Float
) {
    val token = GymTheme.token
    val intakeColor = token.colors.primary
    val burnedColor = token.colors.error
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(token.radius.lg),
        colors = CardDefaults.cardColors(containerColor = token.colors.primarySoft),
        border = BorderStroke(1.dp, token.colors.surfaceBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(token.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(token.spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        stringResource(R.string.analysis_panel_today_energy),
                        style = token.typography.titleSmall,
                        color = token.colors.textSecondary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.analysis_panel_number_grouped, netRemaining),
                            style = token.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = token.colors.primarySoft
                        ) {
                            Text(
                                text = if (netRemaining > 0) {
                                    stringResource(R.string.analysis_panel_status_deficit)
                                } else {
                                    stringResource(R.string.analysis_panel_status_surplus)
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = token.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = token.colors.primary
                            )
                        }
                    }
                    Text(
                        stringResource(R.string.analysis_panel_calories_remaining),
                        style = token.typography.labelSmall,
                        color = token.colors.textSecondary
                    )
                }
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(80.dp),
                        strokeWidth = 8.dp,
                        color = token.colors.primary,
                        trackColor = token.colors.outlineSoft
                    )
                    Text(
                        stringResource(R.string.analysis_panel_percent_value, (progress * 100).toInt()),
                        style = token.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        stringResource(R.string.analysis_panel_intake_value, intake),
                        style = token.typography.labelSmall,
                        color = token.colors.textSecondary
                    )
                    Text(
                        stringResource(R.string.analysis_panel_goal_value, goal),
                        style = token.typography.labelSmall,
                        color = token.colors.textSecondary
                    )
                }
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .background(token.colors.outlineSoft, RoundedCornerShape(999.dp))
                ) {
                    val intakeRatio = (intake.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
                    val burnedRatio = (burned.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
                    Row(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(intakeRatio)
                                .background(intakeColor)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(burnedRatio)
                            .background(burnedColor)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = maxWidth * (1f - intakeRatio) - 1.dp)
                            .size(width = 2.dp, height = 12.dp)
                            .background(token.colors.primary)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    LegendDot(
                        label = stringResource(R.string.analysis_panel_intake),
                        color = intakeColor,
                        textColor = token.colors.textSecondary
                    )
                    LegendDot(
                        label = stringResource(R.string.analysis_panel_burned),
                        color = burnedColor,
                        textColor = token.colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun MealsSection(items: List<FoodLogItem>) {
    val token = GymTheme.token
    Column(verticalArrangement = Arrangement.spacedBy(token.spacing.md)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.analysis_panel_meals_today),
                style = token.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = token.colors.primary
                ),
                elevation = null
            ) {
                Text(stringResource(R.string.analysis_panel_see_all), style = token.typography.labelLarge)
            }
        }
        items.forEachIndexed { index, item ->
            MealItem(item = item)
            if (index < items.lastIndex) {
                HorizontalDivider(color = token.colors.outlineSoft)
            }
        }
    }
}

@Composable
private fun MealItem(item: FoodLogItem) {
    val token = GymTheme.token
    val intakeColor = token.colors.primary
    val burnedColor = token.colors.error
    val kcalColor = when {
        item.calories < 300 -> intakeColor
        item.calories <= 600 -> token.colors.primarySelected
        else -> burnedColor
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(token.radius.md),
        colors = CardDefaults.cardColors(containerColor = token.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = token.card.elevation)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(token.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(token.spacing.md)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        token.colors.outlineSoft,
                        RoundedCornerShape(token.radius.md)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocalDining, contentDescription = null, tint = token.colors.primary)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(item.name, style = token.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    stringResource(R.string.analysis_panel_macro_format, item.protein, item.carb, item.fat),
                    style = token.typography.bodySmall,
                    color = token.colors.textSecondary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    stringResource(R.string.analysis_panel_kcal_value, item.calories),
                    style = token.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = kcalColor
                )
                Text(item.mealType.uppercase(), style = token.typography.labelSmall, color = token.colors.textSecondary)
            }
        }
    }
}

@Composable
private fun ActivitiesSection(items: List<ActivityItem>) {
    val token = GymTheme.token
    Column(verticalArrangement = Arrangement.spacedBy(token.spacing.md)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.analysis_panel_activities),
                style = token.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = token.colors.primary
                ),
                elevation = null
            ) {
                Text(stringResource(R.string.analysis_panel_view_logs), style = token.typography.labelLarge)
            }
        }

        items.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(token.radius.md),
                colors = CardDefaults.cardColors(containerColor = token.colors.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, token.colors.surfaceBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = token.card.elevation)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(token.spacing.md),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(token.spacing.md), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(token.colors.primarySoft, RoundedCornerShape(999.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.DirectionsRun,
                                contentDescription = null,
                                tint = token.colors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(item.name, style = token.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(
                                stringResource(
                                    R.string.analysis_panel_activity_meta,
                                    item.duration,
                                    item.intensityLabel
                                ),
                                style = token.typography.bodySmall,
                                color = token.colors.textSecondary
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            stringResource(R.string.analysis_panel_kcal_value, item.calories),
                            style = token.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .size(width = 48.dp, height = 4.dp)
                                .background(token.colors.outlineSoft, RoundedCornerShape(999.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(item.intensity.coerceIn(0f, 1f))
                                    .background(token.colors.primary, RoundedCornerShape(999.dp))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyAnalyticsCard(bars: List<Float>) {
    val token = GymTheme.token
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
        shape = RoundedCornerShape(token.radius.lg),
        colors = CardDefaults.cardColors(containerColor = token.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(token.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(token.spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        stringResource(R.string.analysis_panel_weekly_analytics),
                        style = token.typography.titleLarge,
                        color = token.colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        stringResource(R.string.analysis_panel_weekly_subtitle),
                        style = token.typography.bodySmall,
                        color = token.colors.textSecondary
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(token.spacing.xs)) {
                    LegendDot(stringResource(R.string.analysis_panel_intake), token.colors.primary, textColor = token.colors.textSecondary)
                    LegendDot(stringResource(R.string.analysis_panel_burned), token.colors.error, textColor = token.colors.textSecondary)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                bars.forEach { value ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(value.coerceIn(0.2f, 1f))
                            .background(
                                token.colors.outlineSoft,
                                RoundedCornerShape(topStart = token.radius.sm, topEnd = token.radius.sm)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendDot(label: String, color: Color, textColor: Color) {
    val token = GymTheme.token
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, RoundedCornerShape(999.dp))
        )
        Text(label, style = token.typography.labelSmall, color = textColor)
    }
}
