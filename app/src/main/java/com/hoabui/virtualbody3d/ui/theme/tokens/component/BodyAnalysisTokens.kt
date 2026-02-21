package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens

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
    val bottomBarLabelTopSpacing: Dp
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
    bottomBarLabelTopSpacing = spacing.xxs
)
