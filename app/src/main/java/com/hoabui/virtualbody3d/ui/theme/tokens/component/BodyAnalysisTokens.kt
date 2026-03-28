package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens

/** Explicit width/height per card size for image+text tiles (no formula — token-only). */
@Immutable
data class CardImageWithTextSizeTokens(
    val smallWidth: Dp,
    val smallHeight: Dp,
    val mediumWidth: Dp,
    val mediumHeight: Dp,
    val largeWidth: Dp,
    val largeHeight: Dp
)

/**
 * Component tokens dedicated to BodyAnalysis screen layout.
 * Keep all screen-specific dimensions centralized here.
 */
@Immutable
data class BodyAnalysisTokens(
    val topBarHorizontalPadding: Dp,
    val topBarVerticalPadding: Dp,
    val topBarInnerHorizontalPadding: Dp,
    val topBarBorderWidth: Dp,
    val topBarIconSize: Dp,
    val topBarActionElevation: Dp,
    val previewModelTopPadding: Dp,
    val scoreChipTopPadding: Dp,
    val metricChipFirstRowTopPadding: Dp,
    val metricChipSecondRowTopPadding: Dp,
    val metricChipSidePadding: Dp,
    val previewTrackBottomPadding: Dp,
    val previewTrackMaxWidth: Dp,
    val previewTrackHeight: Dp,
    val bottomBarBorderWidth: Dp,
    val bottomBarItemPadding: Dp,
    val bottomBarSelectedHorizontalPadding: Dp,
    val bottomBarSelectedVerticalPadding: Dp,
    val bottomBarLabelTopSpacing: Dp,
    val bottomBarIconContainerSize: Dp,
    val bottomBarIconSize: Dp,
    /** Horizontal capsule behind the selected tab icon (expanded width). */
    val bottomBarSelectionPillWidthExpanded: Dp,
    /** Capsule height for the floating nav selection indicator. */
    val bottomBarSelectionPillHeight: Dp,
    val dashboardPanelTopRadius: Dp,
    val dashboardPanelHorizontalPadding: Dp,
    val dashboardPanelTopPadding: Dp,
    val dashboardPanelBottomPadding: Dp,
    val dashboardPanelSectionSpacing: Dp,
    val dashboardHandleWidth: Dp,
    val dashboardHandleHeight: Dp,
    val dashboardGreetingIconSize: Dp,
    val dashboardGreetingIconContainerSize: Dp,
    val dashboardCalorieCardPadding: Dp,
    val dashboardCalorieRingSize: Dp,
    val dashboardCalorieRingOuterStrokeWidth: Dp,
    val dashboardCalorieRingInnerStrokeWidth: Dp,
    val dashboardCalorieRingGap: Dp,
    val dashboardMealItemWidth: Dp,
    val dashboardMealItemImageSize: Dp,
    val dashboardSummaryCardPadding: Dp,
    val dashboardSummaryCardHeight: Dp,
    val dashboardScrollContentBottomSpacing: Dp,
    val dashboardFloatingNavHorizontalPadding: Dp,
    val dashboardFloatingNavVerticalPadding: Dp,
    val dashboardFloatingNavBottomPadding: Dp,
    val metricChipMinWidth: Dp,
    val metricChipIconContainerSize: Dp,
    val metricChipIconSize: Dp,
    val scoreChipMinWidth: Dp,
    val scoreChipProminentMinWidth: Dp,
    val scoreChipProgressSize: Dp,
    val scoreChipProminentProgressSize: Dp,
    val scoreChipInnerSize: Dp,
    val scoreChipProminentInnerSize: Dp,
    val scoreChipStrokeWidth: Dp,
    val scoreChipProminentStrokeWidth: Dp,
    val bodyRegionItemWidth: Dp,
    val bodyRegionItemHeight: Dp,
    /** Sizes for image cards (e.g. exercise tiles); use instead of scaling [bodyRegionItemWidth]/[bodyRegionItemHeight]. */
    val cardImageWithText: CardImageWithTextSizeTokens,
    /** Top inset between square image and title row on [GImageCard] (home soft-edge). */
    val gImageCardTextSectionTopPadding: Dp,
    /** Corner radius for home [GImageCard] small tile + matching [AddCard] / image clip. */
    val gImageCardCornerRadius: Dp,
    val bodyRegionPlaceholderSize: Dp,
    val supplementCardWidth: Dp,
    val supplementCardHeight: Dp,
    val dashboardCaloriePremiumRingSize: Dp,
    val dashboardCaloriePremiumRingStrokeWidth: Dp,
    /** Min width for left/right columns flanking the home calorie ring (metrics + deficit). */
    val dashboardCaloriePremiumSideColumnWidth: Dp,
    val timelineItemWidth: Dp,
    val timelineItemSpacing: Dp,
    /** Square avatar edge length (e.g. xxl + md = 64dp). */
    val timelineAvatarSquareSize: Dp,
    val timelineAvatarCornerRadius: Dp,
    val timelinePlaceholderIconSize: Dp,
    val timelineDotSize: Dp,
    val timelineLineThickness: Dp,
    val timelineLineOffsetY: Dp,
    val timelineDateSlotHeight: Dp,
    val timelineDateToAvatarGap: Dp,
    val timelineAvatarToDotGap: Dp,
    val timelineDotToMetricGap: Dp
)

fun gymBodyAnalysisTokens(spacing: PrimitiveSpacingTokens): BodyAnalysisTokens = BodyAnalysisTokens(
    topBarHorizontalPadding = spacing.md,
    topBarVerticalPadding = 10.dp,
    topBarInnerHorizontalPadding = spacing.xxs,
    topBarBorderWidth = 1.dp,
    topBarIconSize = 40.dp,
    topBarActionElevation = 8.dp,
    previewModelTopPadding = spacing.xs,
    scoreChipTopPadding = 28.dp,
    metricChipFirstRowTopPadding = 84.dp,
    metricChipSecondRowTopPadding = 140.dp,
    metricChipSidePadding = spacing.md,
    previewTrackBottomPadding = spacing.xl,
    previewTrackMaxWidth = 192.dp,
    previewTrackHeight = 8.dp,
    bottomBarBorderWidth = 1.dp,
    bottomBarItemPadding = spacing.xxs,
    bottomBarSelectedHorizontalPadding = spacing.xs,
    bottomBarSelectedVerticalPadding = spacing.xxs,
    bottomBarLabelTopSpacing = spacing.xxs,
    // Keep consistent with the existing bottom bar icon container sizing.
    bottomBarIconContainerSize = 36.dp,
    // Phosphor Light tab icons — single size for Holistic Vitality nav.
    bottomBarIconSize = 24.dp,
    bottomBarSelectionPillWidthExpanded = 56.dp,
    bottomBarSelectionPillHeight = 36.dp,
    dashboardPanelTopRadius = spacing.xl,
    dashboardPanelHorizontalPadding = spacing.md,
    dashboardPanelTopPadding = spacing.lg,
    dashboardPanelBottomPadding = spacing.xl,
    dashboardPanelSectionSpacing = spacing.lg,
    dashboardHandleWidth = 48.dp,
    dashboardHandleHeight = 6.dp,
    dashboardGreetingIconSize = 20.dp,
    dashboardGreetingIconContainerSize = 40.dp,
    dashboardCalorieCardPadding = spacing.xs,
    dashboardCalorieRingSize = 80.dp,
    dashboardCalorieRingOuterStrokeWidth = spacing.xs,
    dashboardCalorieRingInnerStrokeWidth = spacing.xxxs,
    dashboardCalorieRingGap = spacing.xxs,
    dashboardMealItemWidth = 160.dp,
    dashboardMealItemImageSize = 64.dp,
    dashboardSummaryCardPadding = spacing.md,
    dashboardSummaryCardHeight = 112.dp,
    dashboardScrollContentBottomSpacing = 88.dp,
    dashboardFloatingNavHorizontalPadding = spacing.md,
    dashboardFloatingNavVerticalPadding = spacing.xs,
    dashboardFloatingNavBottomPadding = spacing.md,
    metricChipMinWidth = 92.dp,
    metricChipIconContainerSize = 22.dp,
    metricChipIconSize = 13.dp,
    scoreChipMinWidth = 92.dp,
    scoreChipProminentMinWidth = 108.dp,
    scoreChipProgressSize = 22.dp,
    scoreChipProminentProgressSize = 28.dp,
    scoreChipInnerSize = 12.dp,
    scoreChipProminentInnerSize = 14.dp,
    scoreChipStrokeWidth = 4.dp,
    scoreChipProminentStrokeWidth = 5.dp,
    bodyRegionItemWidth = 120.dp,
    bodyRegionItemHeight = 120.dp, // 1.5 × width for fitness-style tile
    cardImageWithText = CardImageWithTextSizeTokens(
        smallWidth = 100.dp,
        smallHeight = 145.dp,
        mediumWidth = 96.dp,
        mediumHeight = 141.dp,
        largeWidth = 120.dp,
        largeHeight = 165.dp
    ),
    gImageCardTextSectionTopPadding = 6.dp,
    gImageCardCornerRadius = 18.dp,
    bodyRegionPlaceholderSize = 40.dp,
    supplementCardWidth = 80.dp,
    supplementCardHeight = 100.dp,
    dashboardCaloriePremiumRingSize = 120.dp,
    dashboardCaloriePremiumRingStrokeWidth = spacing.md,
    // Slightly wider than [dashboardCalorieRingSize] so metric/deficit text aligns without clipping.
    dashboardCaloriePremiumSideColumnWidth = 88.dp,
    // Home reference: meal item is 160dp and body-region item is 120dp.
    // Timeline is intentionally smaller for quick scan density.
    // Wide enough for weight + delta chip and two secondary metric lines.
    timelineItemWidth = 104.dp,
    timelineItemSpacing = spacing.md,
    timelineAvatarSquareSize = 80.dp,
    timelineAvatarCornerRadius = spacing.md,
    timelinePlaceholderIconSize = spacing.xl,
    timelineDotSize = spacing.xs,
    timelineLineThickness = spacing.dividerThickness,
    // Align line with dot center: date + gap + square avatar + gap + half dot.
    timelineLineOffsetY = spacing.md + spacing.xs + spacing.xxl + spacing.md + spacing.xs + spacing.xs / 2f,
    timelineDateSlotHeight = spacing.md,
    timelineDateToAvatarGap = spacing.xs,
    timelineAvatarToDotGap = spacing.xs,
    timelineDotToMetricGap = spacing.xs
)
