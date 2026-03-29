package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.gymBodyDetailLayoutSemantics

@Immutable
data class BodyDetailTokens(
    val heroHeight: Dp
)

fun gymBodyDetailTokens(): BodyDetailTokens = BodyDetailTokens(
    heroHeight = gymBodyDetailLayoutSemantics().heroHeight
)
