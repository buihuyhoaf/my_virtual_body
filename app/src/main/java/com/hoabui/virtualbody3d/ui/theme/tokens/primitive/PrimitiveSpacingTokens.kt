package com.hoabui.virtualbody3d.ui.theme.tokens.primitive

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Primitive spacing scale used to compose semantic/component spacing.
 * Each value is a single token; no computation (e.g. lg + xxs) in component tokens.
 */
@Immutable
data class PrimitiveSpacingTokens(
    /** Zero spacing — use instead of raw `0.dp` in token-driven layouts. */
    val none: Dp,
    val xxxs: Dp,
    val xxs: Dp,
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp,
    val xxl: Dp,
    val xxxl: Dp,
    /** Tight gap (e.g. booking location label vs selector). */
    val spacingStep1: Dp,
    val iconMedium: Dp,
    val buttonPrimary: Dp,
    val dividerThickness: Dp
) {
    companion object {
        fun compact(): PrimitiveSpacingTokens = PrimitiveSpacingTokens(
            none = 0.dp,
            xxxs = 2.dp,
            xxs = 4.dp,
            xs = 8.dp,
            sm = 12.dp,
            md = 16.dp,
            lg = 24.dp,
            xl = 32.dp,
            xxl = 48.dp,
            xxxl = 56.dp,
            spacingStep1 = 2.dp,
            iconMedium = 28.dp,
            buttonPrimary = 72.dp,
            dividerThickness = 1.dp
        )

        fun expanded(): PrimitiveSpacingTokens = PrimitiveSpacingTokens(
            none = 0.dp,
            xxxs = 2.dp,
            xxs = 6.dp,
            xs = 12.dp,
            sm = 16.dp,
            md = 24.dp,
            lg = 32.dp,
            xl = 40.dp,
            xxl = 56.dp,
            xxxl = 64.dp,
            spacingStep1 = 2.dp,
            iconMedium = 32.dp,
            buttonPrimary = 80.dp,
            dividerThickness = 1.dp
        )

        fun default(): PrimitiveSpacingTokens = compact()
    }
}
