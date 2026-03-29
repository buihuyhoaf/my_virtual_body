package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.gymMealLayoutSemantics

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
): MealTokens {
    val layout = gymMealLayoutSemantics()
    return MealTokens(
        carouselRowHeight = spacing.xxl + spacing.xxl + spacing.xs,
        cardWidth = layout.cardWidth,
        imageSize = layout.imageSize,
        macroProgressHeight = layout.macroProgressHeight,
        nutritionSummaryRingSize = layout.nutritionSummaryRingSize,
        nutritionSummaryRingStrokeWidth = layout.nutritionSummaryRingStrokeWidth,
        nutritionSummarySnapshotSize = layout.nutritionSummarySnapshotSize,
        nutritionSummarySnapshotOverlap = layout.nutritionSummarySnapshotOverlap,
    )
}
