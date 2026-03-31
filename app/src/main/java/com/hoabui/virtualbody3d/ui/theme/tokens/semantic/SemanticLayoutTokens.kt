package com.hoabui.virtualbody3d.ui.theme.tokens.semantic

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Immutable
data class BodyAnalysisLayoutSemantics(
    val topBarVerticalPadding: Dp,
    val topBarIconSize: Dp,
    val scoreChipTopPadding: Dp,
    val metricChipFirstRowTopPadding: Dp,
    val metricChipSecondRowTopPadding: Dp,
    val previewTrackMaxWidth: Dp,
    val bottomBarIconContainerSize: Dp,
    val bottomBarIconSize: Dp,
    val bottomBarSelectionPillWidthExpanded: Dp,
    val bottomBarSelectionPillHeight: Dp,
    val dashboardHandleWidth: Dp,
    val dashboardHandleHeight: Dp,
    val dashboardGreetingIconSize: Dp,
    val dashboardGreetingIconContainerSize: Dp,
    val dashboardCalorieRingSize: Dp,
    val dashboardMealItemWidth: Dp,
    val dashboardMealItemImageSize: Dp,
    val dashboardSummaryCardHeight: Dp,
    val dashboardScrollContentBottomSpacing: Dp,
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
    val cardSmallWidth: Dp,
    val cardSmallHeight: Dp,
    val cardMediumWidth: Dp,
    val cardMediumHeight: Dp,
    val cardLargeWidth: Dp,
    val cardLargeHeight: Dp,
    val cardExerciseLibraryWidth: Dp,
    val cardExerciseLibraryHeight: Dp,
    val gImageCardTextSectionTopPadding: Dp,
    val gImageCardCornerRadius: Dp,
    val upcomingExerciseChipWidth: Dp,
    val upcomingExerciseChipHeight: Dp,
    val upcomingExerciseChipImageSize: Dp,
    val upcomingExerciseChipImageCornerRadius: Dp,
    val upcomingExerciseChipContentHorizontalPadding: Dp,
    val bodyRegionPlaceholderSize: Dp,
    val supplementCardWidth: Dp,
    val supplementCardHeight: Dp,
    val dashboardCaloriePremiumRingSize: Dp,
    val dashboardCaloriePremiumSideColumnWidth: Dp,
    val timelineItemWidth: Dp,
    val timelineAvatarSquareSize: Dp,
    val timelineAvatarCornerRadius: Dp,
    val timelineWeightLineEstimate: Dp,
    val timelineSecondaryBlockEstimate: Dp,
    val timelineSecondaryMetricIconSize: Dp,
    val heroSlimChipHeight: Dp,
    val heroSlimChipPaddingHorizontal: Dp,
    val heroSlimChipIconSize: Dp,
    val heroSlimChipIconTextGap: Dp,
    val heroSlimChipLabelLetterSpacing: TextUnit,
    val timelineDotCenterDivisor: Float,
    val exerciseLibraryCartNumericFieldWidth: Dp,
    /** Top corner radius for the exercise library selection slab (dashboard bar). */
    val exerciseLibrarySelectionBarTopCornerRadius: Dp,
    /** Circular thumbnails in the exercise library anchored cart row. */
    val exerciseLibraryCartThumbnailSize: Dp,
    /** Thin top edge line for the anchored exercise console (docked slab). */
    val exerciseLibraryAnchoredConsoleTopBorderWidth: Dp,
    /** Active thumbnail inset between ring and image. */
    val exerciseLibraryCartThumbnailActiveInset: Dp,
    /** Shared height for compact precision row (fields + primary action). */
    val exerciseLibraryConsolePrecisionRowHeight: Dp,
    /** Circular workout-plan FAB on Exercise Library (Material-aligned tap target). */
    val exerciseLibraryWorkoutPlanFabSize: Dp,
    /** Icon size inside [exerciseLibraryWorkoutPlanFabSize]. */
    val exerciseLibraryWorkoutPlanFabIconSize: Dp,
    /** Minimum width/height for numeric badge on workout-plan FAB. */
    val exerciseLibraryWorkoutPlanFabBadgeMinSize: Dp,
    /** Visual diameter of circular scrim for list/cart corner sticker actions. */
    val exerciseLibraryCornerStickerDiameter: Dp,
    /** Icon glyph size (+ / check / close) inside corner stickers. */
    val exerciseLibraryCornerActionGlyphSize: Dp,
    /** Expanded touch target for corner stickers (a11y / scroll safety). */
    val exerciseLibraryCornerStickerTouchTargetSize: Dp,
)

fun gymBodyAnalysisLayoutSemantics(): BodyAnalysisLayoutSemantics = BodyAnalysisLayoutSemantics(
    topBarVerticalPadding = 10.dp,
    topBarIconSize = 40.dp,
    scoreChipTopPadding = 28.dp,
    metricChipFirstRowTopPadding = 84.dp,
    metricChipSecondRowTopPadding = 140.dp,
    previewTrackMaxWidth = 192.dp,
    bottomBarIconContainerSize = 36.dp,
    bottomBarIconSize = 24.dp,
    bottomBarSelectionPillWidthExpanded = 56.dp,
    bottomBarSelectionPillHeight = 36.dp,
    dashboardHandleWidth = 48.dp,
    dashboardHandleHeight = 6.dp,
    dashboardGreetingIconSize = 20.dp,
    dashboardGreetingIconContainerSize = 40.dp,
    dashboardCalorieRingSize = 80.dp,
    dashboardMealItemWidth = 160.dp,
    dashboardMealItemImageSize = 64.dp,
    dashboardSummaryCardHeight = 112.dp,
    dashboardScrollContentBottomSpacing = 88.dp,
    metricChipMinWidth = 100.dp,
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
    bodyRegionItemHeight = 120.dp,
    cardSmallWidth = 100.dp,
    cardSmallHeight = 145.dp,
    cardMediumWidth = 96.dp,
    cardMediumHeight = 141.dp,
    cardLargeWidth = 120.dp,
    cardLargeHeight = 165.dp,
    cardExerciseLibraryWidth = 140.dp,
    cardExerciseLibraryHeight = 158.dp,
    gImageCardTextSectionTopPadding = 6.dp,
    gImageCardCornerRadius = 18.dp,
    upcomingExerciseChipWidth = 200.dp,
    upcomingExerciseChipHeight = 64.dp,
    upcomingExerciseChipImageSize = 44.dp,
    upcomingExerciseChipImageCornerRadius = 10.dp,
    upcomingExerciseChipContentHorizontalPadding = 10.dp,
    bodyRegionPlaceholderSize = 40.dp,
    supplementCardWidth = 80.dp,
    supplementCardHeight = 100.dp,
    dashboardCaloriePremiumRingSize = 120.dp,
    dashboardCaloriePremiumSideColumnWidth = 88.dp,
    timelineItemWidth = 90.dp,
    timelineAvatarSquareSize = 64.dp,
    timelineAvatarCornerRadius = 12.dp,
    timelineWeightLineEstimate = 20.dp,
    timelineSecondaryBlockEstimate = 28.dp,
    timelineSecondaryMetricIconSize = 10.dp,
    heroSlimChipHeight = 32.dp,
    heroSlimChipPaddingHorizontal = 8.dp,
    heroSlimChipIconSize = 16.dp,
    heroSlimChipIconTextGap = 4.dp,
    heroSlimChipLabelLetterSpacing = 0.05.sp,
    timelineDotCenterDivisor = 2f,
    exerciseLibraryCartNumericFieldWidth = 60.dp,
    exerciseLibrarySelectionBarTopCornerRadius = 28.dp,
    exerciseLibraryCartThumbnailSize = 56.dp,
    exerciseLibraryAnchoredConsoleTopBorderWidth = 0.5.dp,
    exerciseLibraryCartThumbnailActiveInset = 4.dp,
    exerciseLibraryConsolePrecisionRowHeight = 52.dp,
    exerciseLibraryWorkoutPlanFabSize = 56.dp,
    exerciseLibraryWorkoutPlanFabIconSize = 28.dp,
    exerciseLibraryWorkoutPlanFabBadgeMinSize = 18.dp,
    exerciseLibraryCornerStickerDiameter = 30.dp,
    exerciseLibraryCornerActionGlyphSize = 24.dp,
    exerciseLibraryCornerStickerTouchTargetSize = 48.dp,
)

@Immutable
data class OnboardingLayoutSemantics(
    val illustrationBodyWidth: Dp,
    val illustrationBodyHeight: Dp,
    val illustrationScannerSize: Dp,
    val illustrationJournalWidth: Dp,
    val illustrationJournalHeight: Dp,
    val illustrationBaseUnit: Dp,
    val illustrationFineUnit: Dp,
)

fun gymOnboardingLayoutSemantics(): OnboardingLayoutSemantics = OnboardingLayoutSemantics(
    illustrationBodyWidth = 240.dp,
    illustrationBodyHeight = 320.dp,
    illustrationScannerSize = 240.dp,
    illustrationJournalWidth = 240.dp,
    illustrationJournalHeight = 120.dp,
    illustrationBaseUnit = 20.dp,
    illustrationFineUnit = 5.dp,
)

@Immutable
data class MealLayoutSemantics(
    val cardWidth: Dp,
    val imageSize: Dp,
    val macroProgressHeight: Dp,
    val nutritionSummaryRingSize: Dp,
    val nutritionSummaryRingStrokeWidth: Dp,
    val nutritionSummarySnapshotSize: Dp,
    val nutritionSummarySnapshotOverlap: Dp,
)

fun gymMealLayoutSemantics(): MealLayoutSemantics = MealLayoutSemantics(
    cardWidth = 228.dp,
    imageSize = 52.dp,
    macroProgressHeight = 3.dp,
    nutritionSummaryRingSize = 64.dp,
    nutritionSummaryRingStrokeWidth = 6.dp,
    nutritionSummarySnapshotSize = 38.dp,
    nutritionSummarySnapshotOverlap = 12.dp,
)

@Immutable
data class CalendarLayoutSemantics(
    val dayItemImageSize: Dp,
    val panelBottomPadding: Dp,
    val panelOffsetY: Dp,
)

fun gymCalendarLayoutSemantics(): CalendarLayoutSemantics = CalendarLayoutSemantics(
    dayItemImageSize = 34.dp,
    panelBottomPadding = 14.dp,
    panelOffsetY = (-12).dp,
)

@Immutable
data class StatusPopupLayoutSemantics(
    val width: Dp,
)

fun gymStatusPopupLayoutSemantics(): StatusPopupLayoutSemantics = StatusPopupLayoutSemantics(
    width = 340.dp,
)

@Immutable
data class ThinkingCardLayoutSemantics(
    val width: Dp,
    val dotSpacing: Dp,
)

fun gymThinkingCardLayoutSemantics(): ThinkingCardLayoutSemantics = ThinkingCardLayoutSemantics(
    width = 300.dp,
    dotSpacing = 6.dp,
)

@Immutable
data class BodyDetailLayoutSemantics(
    val heroHeight: Dp,
)

fun gymBodyDetailLayoutSemantics(): BodyDetailLayoutSemantics = BodyDetailLayoutSemantics(
    heroHeight = 220.dp,
)

@Immutable
data class SurfaceEffectSemantics(
    val innerRadialRadiusFraction: Float,
    val innerRadialCenterYFraction: Float,
)

fun gymSurfaceEffectSemantics(): SurfaceEffectSemantics = SurfaceEffectSemantics(
    innerRadialRadiusFraction = 1.18f,
    innerRadialCenterYFraction = -0.38f,
)

@Immutable
data class CreateBaselineVisualSemantics(
    val viewfinderAspectRatio: Float,
    val cornerStrokeWidth: Dp,
    val dashedBorderDashLength: Float,
    val dashedBorderGapLength: Float,
)

fun gymCreateBaselineVisualSemantics(): CreateBaselineVisualSemantics = CreateBaselineVisualSemantics(
    viewfinderAspectRatio = 1f / 1.414f,
    cornerStrokeWidth = 4.dp,
    dashedBorderDashLength = 20f,
    dashedBorderGapLength = 10f,
)
