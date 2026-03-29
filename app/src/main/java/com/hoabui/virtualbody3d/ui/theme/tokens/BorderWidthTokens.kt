package com.hoabui.virtualbody3d.ui.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class BorderWidthTokens(
    val none: Dp,
    /** Ultra-thin border (chips, cards, subtle outlines). */
    val hairline: Dp,
    val thin: Dp,
    val standard: Dp
) {
    companion object {
        fun default(): BorderWidthTokens = BorderWidthTokens(
            none = 0.dp,
            hairline = 1.dp,
            thin = 1.5.dp,
            standard = 2.dp
        )
    }
}
