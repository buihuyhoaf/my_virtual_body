package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens

@Immutable
data class MealTokens(
    val carouselRowHeight: Dp,
    val cardWidth: Dp,
    val imageSize: Dp,
    val macroProgressHeight: Dp,
    /** Home nutrition summary card calorie ring diameter. */
    val nutritionSummaryRingSize: Dp,
    val nutritionSummaryRingStrokeWidth: Dp,
    val nutritionSummarySnapshotSize: Dp,
    /** Horizontal overlap between stacked meal thumbnails (step = snapshotSize − overlap). */
    val nutritionSummarySnapshotOverlap: Dp,
)

fun gymMealTokens(
    spacing: PrimitiveSpacingTokens
): MealTokens = MealTokens(
    carouselRowHeight = spacing.xxl + spacing.xxl + spacing.xs, // 104.dp
    cardWidth = 228.dp,
    imageSize = 52.dp,
    macroProgressHeight = 3.dp,
    nutritionSummaryRingSize = 64.dp,
    nutritionSummaryRingStrokeWidth = 6.dp,
    nutritionSummarySnapshotSize = 38.dp,
    nutritionSummarySnapshotOverlap = 12.dp,
)
