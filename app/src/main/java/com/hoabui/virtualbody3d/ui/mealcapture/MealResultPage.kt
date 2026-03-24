package com.hoabui.virtualbody3d.ui.mealcapture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.common_ui.atom.card.GCard
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MealResultPage(
    meal: MealPageUiModel,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val colors = token.colors
    val spacing = token.spacing

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.md)
    ) {
        var bitmap by remember(meal.imageUri) { mutableStateOf<Bitmap?>(null) }

        LaunchedEffect(meal.imageUri) {
            val path = meal.imageUri.path
            bitmap = if (path != null) {
                withContext(Dispatchers.IO) {
                    runCatching { BitmapFactory.decodeFile(path) }.getOrNull()
                }
            } else {
                null
            }
        }

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Meal image – main visual element (square)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(token.radius.lg)),
                contentAlignment = Alignment.Center
            ) {
                val imageBitmap = bitmap?.asImageBitmap()
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = meal.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.body_unsplash),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // 2. Reaction row (social-style)
            Spacer(modifier = Modifier.height(spacing.xs))
            ReactionSummaryRow()

            Spacer(modifier = Modifier.height(spacing.lg))

            // 3. Nutrition summary card – key metrics in a horizontal layout
            GCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                elevation = token.elevation.level1,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(spacing.md)
                ) {
                    GText(
                        text = meal.title,
                        style = token.typography.titleMedium,
                        color = colors.textPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    val summaryMetrics = buildNutritionSummaryMetrics(meal)
                    NutritionSummaryGrid(metrics = summaryMetrics)
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))

        }
    }
}

private data class NutritionSummaryMetric(
    val label: String,
    val value: String
)

@Composable
private fun NutritionMetricColumn(
    label: String,
    value: String
) {
    val token = GymTheme.token
    val colors = token.colors
    val spacing = token.spacing

    Column(
        verticalArrangement = Arrangement.spacedBy(spacing.xxs),
        horizontalAlignment = Alignment.Start
    ) {
        GText(
            text = label,
            style = token.typography.labelSmall,
            color = colors.textSecondary
        )
        GText(
            text = value,
            style = token.typography.titleLarge,
            color = colors.textPrimary,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Composable
private fun NutritionSummaryGrid(
    metrics: List<NutritionSummaryMetric>
) {
    val token = GymTheme.token
    val colors = token.colors
    val spacing = token.spacing

    if (metrics.isEmpty()) return

    val rows = metrics.chunked(2)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        rows.forEach { rowMetrics ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowMetrics.forEach { metric ->
                    GSurface(
                        modifier = Modifier.weight(1f),
                        color = colors.surfaceElevated,
                        shape = RoundedCornerShape(token.radius.md),
                    ) {
                        Column(
                            modifier = Modifier.padding(
                                horizontal = spacing.md,
                                vertical = spacing.md
                            ),
                            verticalArrangement = Arrangement.spacedBy(spacing.xxs)
                        ) {
                            NutritionMetricColumn(
                                label = metric.label,
                                value = metric.value
                            )
                        }
                    }
                }
                // If the last row has only one item, fill the remaining space to keep grid balance.
                if (rowMetrics.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ReactionSummaryRow() {
    val token = GymTheme.token
    val colors = token.colors
    val spacing = token.spacing

    val emojis = listOf("👍", "😍", "😮", "😢")
    // Placeholder reaction count – can be wired to real data later
    val reactionCountLabel = "12 reactions"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            emojis.forEach { emoji ->
                GText(
                    text = emoji,
                    style = token.typography.titleLarge
                )
            }
        }
        GText(
            text = reactionCountLabel,
            style = token.typography.labelSmall,
            color = colors.textSecondary
        )
    }
}

private fun buildNutritionSummaryMetrics(
    meal: MealPageUiModel
): List<NutritionSummaryMetric> {
    val caloriesValue = meal.caloriesText.ifBlank { "-" }

    val macroLines = meal.macroSummaryText
        .lineSequence()
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .toList()

    val macroMap = macroLines
        .mapNotNull { line ->
            val parts = line.split(":", limit = 2)
            val key = parts.getOrNull(0)?.trim().orEmpty()
            val value = parts.getOrNull(1)?.trim().orEmpty()
            if (key.isNotBlank()) key.lowercase() to value else null
        }
        .toMap()

    fun valueFor(key: String): String {
        val match = macroMap.entries.firstOrNull { entry ->
            entry.key.contains(key, ignoreCase = true)
        }?.value
        return match?.takeIf { it.isNotBlank() } ?: "-"
    }

    val protein = valueFor("protein")
    val carbs = valueFor("carb")
    val fat = valueFor("fat")

    return listOf(
        NutritionSummaryMetric(label = "Calories", value = caloriesValue),
        NutritionSummaryMetric(label = "Protein", value = protein),
        NutritionSummaryMetric(label = "Carbs", value = carbs),
        NutritionSummaryMetric(label = "Fat", value = fat)
    )
}

