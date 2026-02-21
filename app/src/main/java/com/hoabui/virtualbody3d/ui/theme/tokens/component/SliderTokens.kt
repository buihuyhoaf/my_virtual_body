package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens

/**
 * Component tokens for slider controls used in body morph tuning.
 */
@Immutable
data class SliderTokens(
    val height: Dp,
    val trackThickness: Dp,
    val thumbSize: Dp
)

fun gymSliderTokens(spacing: PrimitiveSpacingTokens): SliderTokens = SliderTokens(
    height = spacing.lg,
    trackThickness = spacing.xs / 2,
    thumbSize = spacing.md + spacing.xs
)
