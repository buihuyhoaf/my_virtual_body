package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens

/**
 * Design tokens for camera capture UI: control row button and icon sizes.
 * All sizes derived from primitive spacing.
 */
@Immutable
data class CameraTokens(
    /** Size of secondary action buttons (gallery, rotate, back). */
    val secondaryButtonSize: Dp,
    /** Icon size inside secondary buttons. */
    val secondaryIconSize: Dp,
    /** Size of primary capture / send button. */
    val primaryButtonSize: Dp,
    /** Border width of primary button ring. */
    val primaryButtonBorderWidth: Dp,
    /** Icon size for send (confirm) action in review state. */
    val sendIconSize: Dp
)

fun gymCameraTokens(primitiveSpacing: PrimitiveSpacingTokens): CameraTokens = CameraTokens(
    secondaryButtonSize = primitiveSpacing.xxxl,
    secondaryIconSize = primitiveSpacing.iconMedium,
    primaryButtonSize = primitiveSpacing.buttonPrimary,
    primaryButtonBorderWidth = primitiveSpacing.xxs,
    sendIconSize = primitiveSpacing.xl
)
