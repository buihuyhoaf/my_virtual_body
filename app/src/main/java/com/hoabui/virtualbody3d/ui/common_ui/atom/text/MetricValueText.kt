package com.hoabui.virtualbody3d.ui.common_ui.atom.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun MetricValueText(
    text: String,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    Text(
        text = text,
        modifier = modifier,
        style = token.typography.titleLarge,
        color = token.colors.textPrimary,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
fun MetricLabelText(
    text: String,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    Text(
        text = text,
        modifier = modifier,
        style = token.typography.labelSmall,
        color = token.colors.textSecondary,
    )
}
