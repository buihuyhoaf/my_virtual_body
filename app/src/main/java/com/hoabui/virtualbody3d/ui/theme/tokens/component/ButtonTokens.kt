package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveRadiusTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens

/**
 * Component tokens for [GButton] geometry and interaction states.
 *
 * - [height]: minimum touch-target height (WCAG 2.5.5 recommends ≥ 44 dp; 48 dp used here).
 * - [contentPaddingHorizontal]: horizontal padding inside the button between border and content.
 * - [iconSize]: bounding box for leading / trailing icon slots.
 * - [disabledContainerAlpha]: opacity applied to container color when the button is disabled.
 * - [disabledContentAlpha]: opacity applied to content (text + icon) color when disabled.
 *   Both alpha values align with the Material 3 disabled-state specification (0.38f).
 */
@Immutable
data class ButtonTokens(
    val height: Dp,
    val cornerRadius: Dp,
    val contentPaddingHorizontal: Dp,
    val iconSize: Dp,
    val disabledContainerAlpha: Float,
    val disabledContentAlpha: Float,
)

fun gymButtonTokens(
    spacing: PrimitiveSpacingTokens,
    radius: PrimitiveRadiusTokens,
): ButtonTokens = ButtonTokens(
    height = spacing.xxl,                       // 48 dp — WCAG 2.5.5 minimum touch target
    cornerRadius = radius.md,                   // 16 dp
    contentPaddingHorizontal = spacing.md,      // 16 dp
    iconSize = spacing.md + spacing.xxs,        // 20 dp  (fits inside 48 dp height with breathing room)
    disabledContainerAlpha = 0.38f,             // M3 spec: disabled opacity
    disabledContentAlpha = 0.38f,
)
