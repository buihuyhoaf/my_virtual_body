package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveAlphaTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveBorderTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveColorTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.gymCreateBaselineVisualSemantics

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
    primitiveColors: PrimitiveColorTokens,
    border: PrimitiveBorderTokens,
    alpha: PrimitiveAlphaTokens,
): CreateBaselineTokens {
    val visual = gymCreateBaselineVisualSemantics()
    return CreateBaselineTokens(
        instructionIconBoxSize = primitiveSpacing.xxl,
        instructionIconSize = primitiveSpacing.lg,
        instructionIconTextGap = primitiveSpacing.md,
        buttonIconSize = primitiveSpacing.lg,
        buttonIconTextGap = primitiveSpacing.xs,
        guidePadding = primitiveSpacing.md,
        gradientHeight = primitiveSpacing.xxl,
        borderWidth = border.medium,
        viewfinderAspectRatio = visual.viewfinderAspectRatio,
        instructionIconBackground = primitiveColors.primary.copy(alpha = alpha.CREATE_BASELINE_INSTRUCTION_BG),
        viewfinderBorder = primitiveColors.primary.copy(alpha = alpha.CREATE_BASELINE_VIEWFINDER_BORDER),
        guideBorder = primitiveColors.primary.copy(alpha = alpha.CREATE_BASELINE_GUIDE_BORDER),
        gradientStart = primitiveColors.transparent,
        gradientEnd = primitiveColors.primary.copy(alpha = alpha.CREATE_BASELINE_GRADIENT_END),
        cornerMarkerSize = primitiveSpacing.xl,
        cornerStrokeWidth = visual.cornerStrokeWidth,
        dashedBorderDashLength = visual.dashedBorderDashLength,
        dashedBorderGapLength = visual.dashedBorderGapLength
    )
}
