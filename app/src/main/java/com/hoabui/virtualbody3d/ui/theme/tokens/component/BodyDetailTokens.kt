package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.gymBodyDetailLayoutSemantics

@Immutable
data class BodyDetailTokens(
    val heroHeight: Dp,
    val exerciseDetailIconicTileSize: Dp,
    val exerciseDetailDialogMaxHeightFraction: Float,
    val exerciseDetailCardWidthFraction: Float,
)

fun gymBodyDetailTokens(): BodyDetailTokens {
    val layout = gymBodyDetailLayoutSemantics()
    return BodyDetailTokens(
        heroHeight = layout.heroHeight,
        exerciseDetailIconicTileSize = layout.exerciseDetailIconicTileSize,
        exerciseDetailDialogMaxHeightFraction = layout.exerciseDetailDialogMaxHeightFraction,
        exerciseDetailCardWidthFraction = layout.exerciseDetailCardWidthFraction,
    )
}
