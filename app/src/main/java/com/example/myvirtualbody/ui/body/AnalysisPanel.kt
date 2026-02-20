package com.example.myvirtualbody.ui.body

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
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myvirtualbody.ui.theme.BodyPrimary

private val PanelShape = RoundedCornerShape(32.dp)
private val HeroShape = RoundedCornerShape(24.dp)
private val SectionShape = RoundedCornerShape(20.dp)
private val MealImageShape = RoundedCornerShape(16.dp)
private val ChartShape = RoundedCornerShape(24.dp)
private val IntakeColor = Color(0xFF22C55E)
private val BurnedColor = Color(0xFFF87171)
private val OrangeColor = Color(0xFFF59E0B)
private val DarkChartBg = Color(0xFF0F172A)

data class FoodLogItem(
    val name: String,
    val calories: Int,
    val protein: Int,
    val carb: Int,
    val fat: Int,
    val mealType: String
)

data class ActivityItem(
    val name: String,
    val duration: String,
    val intensityLabel: String,
    val calories: Int,
    val intensity: Float
)

@Composable
fun AnalysisPanel(
    uiState: BodyUiState,
    modifier: Modifier = Modifier,
    bodyScore: Int = ((uiState.bmiScalePosition ?: 0.76f) * 100f).toInt()
) {
    // Keep stable binding entry points. This panel is now daily analytics-focused.
    val dailyGoal = 3520
    val intake = 2100
    val burned = 680
    val netRemaining = (dailyGoal - intake + burned).coerceAtLeast(0)
    val progress = ((intake - burned).toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f)

    val meals = listOf(
        FoodLogItem("Quinoa Power Bowl", 420, protein = 24, carb = 45, fat = 12, mealType = "Lunch"),
        FoodLogItem("Greek Yogurt", 250, protein = 18, carb = 22, fat = 8, mealType = "Snack"),
        FoodLogItem("Salmon & Rice", 640, protein = 42, carb = 48, fat = 22, mealType = "Dinner")
    )
    val activities = listOf(
        ActivityItem("Morning Run", "45 min", "High Intensity", 450, 0.85f),
        ActivityItem("Evening Walk", "35 min", "Low Intensity", 140, 0.35f)
    )
    val weeklyBars = listOf(0.64f, 0.48f, 0.8f, 0.56f, 0.4f, 0.72f, 0.6f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(PanelShape),
        shape = PanelShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp,
        tonalElevation = 6.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .size(width = 48.dp, height = 6.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(999.dp)
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 24.dp, end = 24.dp, top = 40.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = HeroShape,
        colors = CardDefaults.cardColors(containerColor = BodyPrimary.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, BodyPrimary.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Today's Energy", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "%,d".format(netRemaining),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = BodyPrimary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = if (netRemaining > 0) "Deficit" else "Surplus",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = BodyPrimary
                            )
                        }
                    }
                    Text(
                        "Calories remaining",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(80.dp),
                        strokeWidth = 8.dp,
                        color = BodyPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Intake: %,d".format(intake), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Goal: %,d".format(goal), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(999.dp))
                ) {
                    val intakeRatio = (intake.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
                    val burnedRatio = (burned.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
                    Row(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(intakeRatio)
                                .background(IntakeColor)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(burnedRatio)
                            .background(BurnedColor)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = maxWidth * (1f - intakeRatio) - 1.dp)
                            .size(width = 2.dp, height = 12.dp)
                            .background(BodyPrimary)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    LegendDot("Intake", IntakeColor)
                    LegendDot("Burned", BurnedColor)
                }
            }
        }
    }
}

@Composable
private fun MealsSection(items: List<FoodLogItem>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Meals Today", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = BodyPrimary
                ),
                elevation = null
            ) {
                Text("See All", style = MaterialTheme.typography.labelLarge)
            }
        }
        items.forEachIndexed { index, item ->
            MealItem(item = item)
            if (index < items.lastIndex) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
            }
        }
    }
}

@Composable
private fun MealItem(item: FoodLogItem) {
    val kcalColor = when {
        item.calories < 300 -> IntakeColor
        item.calories <= 600 -> OrangeColor
        else -> BurnedColor
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = SectionShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, MealImageShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocalDining, contentDescription = null, tint = BodyPrimary)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "P ${item.protein}g • C ${item.carb}g • F ${item.fat}g",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${item.calories} kcal", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = kcalColor)
                Text(item.mealType.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ActivitiesSection(items: List<ActivityItem>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Activities", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = BodyPrimary
                ),
                elevation = null
            ) {
                Text("View Logs", style = MaterialTheme.typography.labelLarge)
            }
        }

        items.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = SectionShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.22f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(BodyPrimary.copy(alpha = 0.12f), RoundedCornerShape(999.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.DirectionsRun,
                                contentDescription = null,
                                tint = BodyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(item.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(
                                "${item.duration} • ${item.intensityLabel}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("${item.calories} kcal", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .size(width = 48.dp, height = 4.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(999.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(item.intensity.coerceIn(0f, 1f))
                                    .background(BodyPrimary, RoundedCornerShape(999.dp))
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
        shape = ChartShape,
        colors = CardDefaults.cardColors(containerColor = DarkChartBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text("Weekly Analytics", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(
                        "Net caloric balance trend",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    LegendDot("Intake", Color(0xFF4ADE80), textColor = Color(0xFFCBD5E1))
                    LegendDot("Burned", Color(0xFFF87171), textColor = Color(0xFFCBD5E1))
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
                            .background(Color(0xFF1E293B), RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendDot(label: String, color: Color, textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, RoundedCornerShape(999.dp))
        )
        Text(label, style = MaterialTheme.typography.labelSmall, color = textColor)
    }
}
