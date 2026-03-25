package com.hoabui.virtualbody3d.ui.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class BorderWidthTokens(
    val none: Dp,
    val thin: Dp,
    val standard: Dp
) {
    companion object {
        fun default(): BorderWidthTokens = BorderWidthTokens(
            none = 0.dp,
            thin = 1.dp,
            standard = 1.5.dp
        )
    }
}
