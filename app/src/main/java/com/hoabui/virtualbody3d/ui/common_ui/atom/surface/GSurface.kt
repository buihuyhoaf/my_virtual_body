package com.hoabui.virtualbody3d.ui.common_ui.atom.surface

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Decorative surface for badges, chips, and non-interactive containers.
 * For clickable surfaces, use [GCard] instead.
 */
@Composable
fun GSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(GymTheme.token.radius.sm),
    color: Color = GymTheme.token.colors.surfaceSubtle,
    border: BorderStroke? = null,
    shadowElevation: Dp = 0.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = color,
        border = border,
        shadowElevation = shadowElevation,
    ) {
        Box(content = content)
    }
}
