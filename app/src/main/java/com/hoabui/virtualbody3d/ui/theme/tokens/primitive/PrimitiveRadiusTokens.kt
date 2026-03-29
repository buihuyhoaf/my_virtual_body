package com.hoabui.virtualbody3d.ui.theme.tokens.primitive

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Primitive corner radii – Holistic Vitality: softer corners (large 24dp, extra-large 32dp).
 */
@Immutable
data class PrimitiveRadiusTokens(
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp,
    val pill: Dp
) {
    companion object {
        fun default(): PrimitiveRadiusTokens = PrimitiveRadiusTokens(
            sm = 12.dp,
            md = 20.dp,
            lg = 28.dp,
            xl = 36.dp,
            pill = 999.dp
        )
    }
}
