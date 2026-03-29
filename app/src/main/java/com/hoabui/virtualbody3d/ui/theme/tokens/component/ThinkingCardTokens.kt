package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.tokens.ElevationTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveAlphaTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveRadiusTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.gymThinkingCardLayoutSemantics

/**
 * Design tokens for ChatGPT-style thinking card: width, padding, shape, elevation, dots.
 * Single token values only; no computation.
 */
@Immutable
data class ThinkingCardTokens(
    val width: Dp,
    val padding: Dp,
    val cornerRadius: Dp,
    val elevation: Dp,
    val backgroundAlpha: Float,
    val dotSize: Dp,
    val dotSpacing: Dp
)

fun gymThinkingCardTokens(
    primitiveSpacing: PrimitiveSpacingTokens,
    primitiveRadius: PrimitiveRadiusTokens,
    elevation: ElevationTokens,
    alpha: PrimitiveAlphaTokens,
): ThinkingCardTokens {
    val layout = gymThinkingCardLayoutSemantics()
    return ThinkingCardTokens(
        width = layout.width,
        padding = primitiveSpacing.lg,
        cornerRadius = primitiveRadius.lg,
        elevation = elevation.level3,
        backgroundAlpha = alpha.THINKING_CARD_BACKGROUND,
        dotSize = primitiveSpacing.xs,
        dotSpacing = layout.dotSpacing
    )
}
