package com.hoabui.virtualbody3d.ui.theme.tokens.primitive

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Primitive spacing scale used to compose semantic/component spacing.
 */
@Immutable
data class PrimitiveSpacingTokens(
    val xxs: Dp,
    val xs: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp
) {
    companion object {
        fun default(): PrimitiveSpacingTokens = PrimitiveSpacingTokens(
            xxs = 4.dp,
            xs = 8.dp,
            md = 16.dp,
            lg = 24.dp,
            xl = 32.dp
        )
    }
}
