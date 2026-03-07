package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveRadiusTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens

/**
 * Design tokens for StatusPopup: width, spacing, shape, border.
 * Single token values only; no computation.
 */
@Immutable
data class StatusPopupTokens(
    val width: Dp,
    val contentSpacing: Dp,
    val cornerRadius: Dp,
    val borderWidth: Dp
)

fun gymStatusPopupTokens(
    primitiveSpacing: PrimitiveSpacingTokens,
    primitiveRadius: PrimitiveRadiusTokens
): StatusPopupTokens = StatusPopupTokens(
    width = 340.dp,
    contentSpacing = primitiveSpacing.lg,
    cornerRadius = primitiveRadius.md,
    borderWidth = 1.dp
)
