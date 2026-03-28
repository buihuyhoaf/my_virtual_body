package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveRadiusTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens

/**
 * Design tokens for login screen: input fields, CTA button, social buttons.
 * Aligns with Holistic Vitality sage primary and rounded CTA (24dp) from reference.
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
    logoSize = spacing.xxxl,
    primaryButtonHeight = spacing.xxl,
    socialButtonHeight = spacing.xxl
)
