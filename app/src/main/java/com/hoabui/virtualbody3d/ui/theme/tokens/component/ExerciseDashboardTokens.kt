package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveBorderTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveRadiusTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens

@Immutable
data class ExerciseDashboardTokens(
    val categoryCardHeight: Dp,
    val categoryCardCornerRadius: Dp,
    val categoryCardVerticalSpacing: Dp,
    val categoryCardTextScrimStartAlpha: Float,
    val categoryCardTextScrimEndAlpha: Float,
    val achievementSectionVerticalPadding: Dp,
    val achievementSectionDividerThickness: Dp,
    val middleSectionHorizontalSpacing: Dp,
    val coachBubbleMaxWidthFraction: Float,
    val coachBubbleBorderWidth: Dp,
    val coachSpeechVerticalPadding: Dp,
    val coachSpeechHorizontalPadding: Dp,
    val heatmapSectionTopSpacing: Dp,
    val heatmapPrimaryLevel0Alpha: Float,
    val heatmapPrimaryLevel1Alpha: Float,
    val heatmapPrimaryLevel2Alpha: Float,
    val heatmapPrimaryLevel3Alpha: Float,
    val categoryCardPressScaleTarget: Float,
    val dashboardLoadingContentAlpha: Float,
)

fun gymExerciseDashboardTokens(
    spacing: PrimitiveSpacingTokens,
    radius: PrimitiveRadiusTokens,
    border: PrimitiveBorderTokens,
): ExerciseDashboardTokens =
    ExerciseDashboardTokens(
        categoryCardHeight = spacing.buttonPrimary,
        categoryCardCornerRadius = radius.md,
        categoryCardVerticalSpacing = spacing.xs,
        categoryCardTextScrimStartAlpha = 0f,
        categoryCardTextScrimEndAlpha = 0.75f,
        achievementSectionVerticalPadding = spacing.sm,
        achievementSectionDividerThickness = spacing.dividerThickness,
        middleSectionHorizontalSpacing = spacing.sm,
        coachBubbleMaxWidthFraction = 0.85f,
        coachBubbleBorderWidth = border.thin,
        coachSpeechVerticalPadding = spacing.sm,
        coachSpeechHorizontalPadding = spacing.md,
        heatmapSectionTopSpacing = spacing.xxs,
        heatmapPrimaryLevel0Alpha = 0.08f,
        heatmapPrimaryLevel1Alpha = 0.30f,
        heatmapPrimaryLevel2Alpha = 0.60f,
        heatmapPrimaryLevel3Alpha = 1f,
        categoryCardPressScaleTarget = 0.96f,
        dashboardLoadingContentAlpha = 0.40f,
    )
