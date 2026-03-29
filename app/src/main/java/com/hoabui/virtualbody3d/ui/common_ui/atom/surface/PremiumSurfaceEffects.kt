package com.hoabui.virtualbody3d.ui.common_ui.atom.surface

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import com.hoabui.virtualbody3d.ui.theme.tokens.GymToken
import kotlin.math.max

fun Modifier.gymPremiumInnerRadialDepth(
    enabled: Boolean,
    token: GymToken,
): Modifier {
    if (!enabled) return this
    val s = token.surface
    return this.drawBehind {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@drawBehind
        val cx = w / 2f
        val cy = h * (0.5f + s.innerRadialCenterYFraction * 0.5f)
        val r = max(w, h) * s.innerRadialRadiusFraction * 0.5f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    token.colors.primary.copy(alpha = s.innerRadialDepthAlpha),
                    Color.Transparent,
                ),
                center = Offset(cx, cy),
                radius = r,
            ),
            radius = r,
            center = Offset(cx, cy),
            style = Fill,
        )
    }
}
