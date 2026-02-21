package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveRadiusTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens

/**
 * Component tokens for card container styling.
 */
@Immutable
data class CardTokens(
    val elevation: Dp,
    val cornerRadius: Dp,
    val padding: Dp
)

fun gymCardTokens(
    spacing: PrimitiveSpacingTokens,
    radius: PrimitiveRadiusTokens
): CardTokens = CardTokens(
    elevation = spacing.xs,
    cornerRadius = radius.md,
    padding = spacing.md
)
