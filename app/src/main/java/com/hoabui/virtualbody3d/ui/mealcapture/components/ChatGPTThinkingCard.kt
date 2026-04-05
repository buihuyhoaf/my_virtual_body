package com.hoabui.virtualbody3d.ui.mealcapture.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

private const val DotScaleMin = 0.6f
private const val DotScaleMax = 1f
private const val DotAnimationDurationMs = 500
private const val Dot2DelayMs = 200
private const val Dot3DelayMs = 400

/**
 * Reusable animated dots indicator for AI "thinking" state.
 * Three dots pulse sequentially (scale 0.6f → 1.0f) with staggered delays.
 */
@Composable
fun ThinkingDots(
    color: Color,
    dotSize: Dp,
    dotSpacing: Dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "thinkingDots")
    val dot1Scale = infiniteTransition.animateFloat(
        initialValue = DotScaleMin,
        targetValue = DotScaleMax,
        animationSpec = infiniteRepeatable(
            animation = tween(DotAnimationDurationMs),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(0)
        ),
        label = "dot1"
    )
    val dot2Scale = infiniteTransition.animateFloat(
        initialValue = DotScaleMin,
        targetValue = DotScaleMax,
        animationSpec = infiniteRepeatable(
            animation = tween(DotAnimationDurationMs),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(Dot2DelayMs)
        ),
        label = "dot2"
    )
    val dot3Scale = infiniteTransition.animateFloat(
        initialValue = DotScaleMin,
        targetValue = DotScaleMax,
        animationSpec = infiniteRepeatable(
            animation = tween(DotAnimationDurationMs),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(Dot3DelayMs)
        ),
        label = "dot3"
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Dot(scale = dot1Scale.value, color = color, size = dotSize)
        Spacer(Modifier.width(dotSpacing))
        Dot(scale = dot2Scale.value, color = color, size = dotSize)
        Spacer(Modifier.width(dotSpacing))
        Dot(scale = dot3Scale.value, color = color, size = dotSize)
    }
}

@Composable
private fun Dot(
    scale: Float,
    color: Color,
    size: Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .background(color = color, shape = CircleShape)
    )
}

/**
 * Minimal AI "thinking" card similar to ChatGPT while the model is processing.
 * Centered via Popup; full-screen transparent overlay blocks interaction with content behind.
 */
@Composable
fun ChatGPTThinkingCard(
    visible: Boolean,
    message: String
) {
    if (!visible) return

    val token = GymTheme.token
    val colors = token.colors
    val typography = token.typography
    val thinkingCard = token.thinkingCard

    Popup(
        alignment = Alignment.Center,
        properties = PopupProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { }
        ) {
            Surface(
                modifier = Modifier.align(Alignment.Center),
                shape = RoundedCornerShape(thinkingCard.cornerRadius),
                color = GymTheme.token.colors.surface.copy(alpha = thinkingCard.backgroundAlpha),
                shadowElevation = thinkingCard.elevation
            ) {
                Column(
                    modifier = Modifier
                        .width(thinkingCard.width)
                        .padding(thinkingCard.padding)
                ) {
                    GText(
                        text = message,
                        style = typography.bodyMedium,
                        color = colors.textPrimary
                    )

                    Spacer(Modifier.height(thinkingCard.padding))

                    ThinkingDots(
                        color = colors.primary,
                        dotSize = thinkingCard.dotSize,
                        dotSpacing = thinkingCard.dotSpacing
                    )
                }
            }
        }
    }
}
