package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens

/**
 * Component tokens for the 3D scene control panel layout.
 */
@Immutable
data class ControlPanelTokens(
    val panelHeight: Dp,
    val horizontalPadding: Dp,
    val verticalPadding: Dp
)

fun gymControlPanelTokens(spacing: PrimitiveSpacingTokens): ControlPanelTokens = ControlPanelTokens(
    panelHeight = spacing.xl * 3,
    horizontalPadding = spacing.md,
    verticalPadding = spacing.xs
)
