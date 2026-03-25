package com.hoabui.virtualbody3d.ui.theme.tokens

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.runtime.Immutable

@Immutable
data class MotionDurationTokens(
    val short: Int,
    val standard: Int,
    val long: Int
)

@Immutable
data class MotionEasingTokens(
    val standard: Easing,
    val emphasized: Easing,
    val decelerate: Easing
)

@Immutable
data class MotionTokens(
    val duration: MotionDurationTokens,
    val easing: MotionEasingTokens
) {
    companion object {
        fun default(): MotionTokens = MotionTokens(
            duration = MotionDurationTokens(
                short = 150,
                standard = 300,
                long = 500
            ),
            easing = MotionEasingTokens(
                standard = LinearEasing,
                emphasized = CubicBezierEasing(0.2f, 0f, 0f, 1f),
                decelerate = CubicBezierEasing(0f, 0f, 0f, 1f)
            )
        )
    }
}
