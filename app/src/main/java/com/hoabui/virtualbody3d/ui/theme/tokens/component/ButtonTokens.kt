package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveAlphaTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveBorderTokens
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
 */
@Immutable
data class ButtonTokens(
    val height: Dp,
    val cornerRadius: Dp,
    val contentPaddingHorizontal: Dp,
    val iconSize: Dp,
    val outlinedBorderWidth: Dp,
    val loadingIndicatorSizeDelta: Dp,
    val loadingIndicatorStrokeWidth: Dp,
    val disabledContainerAlpha: Float,
    val disabledContentAlpha: Float,
)

fun gymButtonTokens(
    spacing: PrimitiveSpacingTokens,
    radius: PrimitiveRadiusTokens,
    border: PrimitiveBorderTokens,
    alpha: PrimitiveAlphaTokens,
): ButtonTokens = ButtonTokens(
    height = spacing.xxl,
    cornerRadius = radius.md,
    contentPaddingHorizontal = spacing.md,
    iconSize = spacing.md + spacing.xxs,
    outlinedBorderWidth = border.thin,
    loadingIndicatorSizeDelta = spacing.xxs,
    loadingIndicatorStrokeWidth = spacing.xxxs,
    disabledContainerAlpha = alpha.DISABLED,
    disabledContentAlpha = alpha.DISABLED,
)
