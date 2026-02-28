package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveRadiusTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens

/**
 * Design tokens for login screen: input fields, CTA button, social buttons.
 * Aligns with brand plum (#8E3B46) and rounded-cta (24dp) from reference.
 */
@Immutable
data class LoginTokens(
    val inputCornerRadius: Dp,
    val ctaCornerRadius: Dp,
    val logoSize: Dp,
    val primaryButtonHeight: Dp,
    val socialButtonHeight: Dp
)

fun gymLoginTokens(
    spacing: PrimitiveSpacingTokens,
    radius: PrimitiveRadiusTokens
): LoginTokens = LoginTokens(
    inputCornerRadius = radius.md,
    ctaCornerRadius = radius.lg,
    logoSize = 48.dp,
    primaryButtonHeight = spacing.xl + spacing.md,
    socialButtonHeight = spacing.lg + spacing.xs
)
