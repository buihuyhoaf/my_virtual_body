package com.hoabui.virtualbody3d.ui.theme.tokens.primitive

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class PrimitiveBorderTokens(
    val none: Dp,
    val hairline: Dp,
    val thin: Dp,
    val medium: Dp,
) {
    companion object {
        fun default(): PrimitiveBorderTokens = PrimitiveBorderTokens(
            none = 0.dp,
            hairline = 1.dp,
            thin = 1.5.dp,
            medium = 2.dp,
        )
    }
}
