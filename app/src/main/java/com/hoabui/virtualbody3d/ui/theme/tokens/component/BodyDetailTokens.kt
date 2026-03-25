package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class BodyDetailTokens(
    val heroHeight: Dp
)

fun gymBodyDetailTokens(): BodyDetailTokens = BodyDetailTokens(
    heroHeight = 220.dp
)
