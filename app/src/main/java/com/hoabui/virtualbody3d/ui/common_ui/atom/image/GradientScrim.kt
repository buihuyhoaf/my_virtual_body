package com.hoabui.virtualbody3d.ui.common_ui.atom.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Bottom-aligned vertical scrim for imagery; [overlayBaseColor] is typically [SemanticColorTokens.textBlack].
 */
@Composable
fun GradientScrim(
    modifier: Modifier = Modifier,
    overlayBaseColor: Color,
    gradientStartAlpha: Float,
    gradientEndAlpha: Float,
    content: @Composable BoxScope.() -> Unit = { },
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to overlayBaseColor.copy(alpha = gradientStartAlpha.coerceIn(0f, 1f)),
                            1f to overlayBaseColor.copy(alpha = gradientEndAlpha.coerceIn(0f, 1f)),
                        ),
                    ),
                ),
        )
        content()
    }
}
