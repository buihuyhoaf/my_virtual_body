package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveRadiusTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens

/**
 * Component tokens for button geometry.
 */
@Immutable
data class ButtonTokens(
    val height: Dp,
    val cornerRadius: Dp
)

fun gymButtonTokens(
    spacing: PrimitiveSpacingTokens,
    radius: PrimitiveRadiusTokens
): ButtonTokens = ButtonTokens(
    height = spacing.lg + spacing.xxs,
    cornerRadius = radius.md
)
