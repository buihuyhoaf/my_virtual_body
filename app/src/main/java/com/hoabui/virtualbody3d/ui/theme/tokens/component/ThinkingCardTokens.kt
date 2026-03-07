package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveRadiusTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens

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
    primitiveRadius: PrimitiveRadiusTokens
): ThinkingCardTokens = ThinkingCardTokens(
    width = 300.dp,
    padding = primitiveSpacing.lg,
    cornerRadius = primitiveRadius.lg,
    elevation = 8.dp,
    backgroundAlpha = 0.96f,
    dotSize = primitiveSpacing.xs,
    dotSpacing = 6.dp
)
