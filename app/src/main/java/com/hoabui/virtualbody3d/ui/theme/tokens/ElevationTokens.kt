package com.hoabui.virtualbody3d.ui.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Elevation scale for warm terracotta body-inspired theme.
 */
@Immutable
data class ElevationTokens(
    val level0: Dp,
    val level1: Dp,
    val level2: Dp,
    /** Hero / primary focal surfaces — use sparingly to limit overdraw. */
    val level3: Dp
) {
    companion object {
        fun default(): ElevationTokens = ElevationTokens(
            level0 = 0.dp,
            level1 = 2.dp,
            level2 = 4.dp,
            level3 = 8.dp
        )
    }
}
