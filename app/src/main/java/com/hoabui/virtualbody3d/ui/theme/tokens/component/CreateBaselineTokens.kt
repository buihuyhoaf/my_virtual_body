package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveColorTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens

/**
 * Design tokens for Create Baseline screen: viewfinder, instructions, buttons.
 * Sizes from primitive spacing; colors from primitive (opacity variants defined here, not in UI).
 */
@Immutable
data class CreateBaselineTokens(
    val instructionIconBoxSize: Dp,
    val instructionIconSize: Dp,
    val instructionIconTextGap: Dp,
    val buttonIconSize: Dp,
    val buttonIconTextGap: Dp,
    val guidePadding: Dp,
    val gradientHeight: Dp,
    val borderWidth: Dp,
    val viewfinderAspectRatio: Float,
    val instructionIconBackground: Color,
    val viewfinderBorder: Color,
    val guideBorder: Color,
    val gradientStart: Color,
    val gradientEnd: Color,
    val cornerMarkerSize: Dp,
    val cornerStrokeWidth: Dp,
    val dashedBorderDashLength: Float,
    val dashedBorderGapLength: Float
)

fun gymCreateBaselineTokens(
    primitiveSpacing: PrimitiveSpacingTokens,
    primitiveColors: PrimitiveColorTokens
): CreateBaselineTokens = CreateBaselineTokens(
    instructionIconBoxSize = primitiveSpacing.xxl,
    instructionIconSize = primitiveSpacing.lg,
    instructionIconTextGap = primitiveSpacing.md,
    buttonIconSize = primitiveSpacing.lg,
    buttonIconTextGap = primitiveSpacing.xs,
    guidePadding = primitiveSpacing.md,
    gradientHeight = primitiveSpacing.xxl,
    borderWidth = 2.dp,
    viewfinderAspectRatio = 1f / 1.414f,
    instructionIconBackground = primitiveColors.primary.copy(alpha = 0.1f),
    viewfinderBorder = primitiveColors.primary.copy(alpha = 0.3f),
    guideBorder = primitiveColors.primary.copy(alpha = 0.4f),
    gradientStart = primitiveColors.transparent,
    gradientEnd = primitiveColors.primary.copy(alpha = 0.05f),
    cornerMarkerSize = primitiveSpacing.xl,
    cornerStrokeWidth = 4.dp,
    dashedBorderDashLength = 20f,
    dashedBorderGapLength = 10f
)
