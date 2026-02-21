package com.hoabui.virtualbody3d.ui.theme.tokens.primitive

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Primitive corner radii to keep curvature consistent app-wide.
 */
@Immutable
data class PrimitiveRadiusTokens(
    val sm: Dp,
    val md: Dp,
    val lg: Dp
) {
    companion object {
        fun default(): PrimitiveRadiusTokens = PrimitiveRadiusTokens(
            sm = 8.dp,
            md = 16.dp,
            lg = 24.dp
        )
    }
}
