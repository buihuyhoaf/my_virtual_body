package com.hoabui.virtualbody3d.ui.createbaseline.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun CreateBaselineLoadingOverlay(
    message: String
) {
    val token = GymTheme.token
    val colors = token.colors
    val typography = token.typography
    val spacing = token.spacing
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.surface.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = colors.primary,
                modifier = Modifier.size(spacing.xxl)
            )
            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    style = typography.bodyMedium,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(top = spacing.lg)
                )
            }
        }
    }
}
