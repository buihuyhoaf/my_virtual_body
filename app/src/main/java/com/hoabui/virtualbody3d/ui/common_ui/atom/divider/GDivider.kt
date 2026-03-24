package com.hoabui.virtualbody3d.ui.common_ui.atom.divider

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun GDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = GymTheme.token.spacing.dividerThickness,
    color: Color = GymTheme.token.colors.borderSubtle,
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color,
    )
}
