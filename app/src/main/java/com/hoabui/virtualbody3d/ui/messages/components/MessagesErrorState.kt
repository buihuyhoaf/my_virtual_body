package com.hoabui.virtualbody3d.ui.messages.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun MessagesErrorState(
    message: String,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(token.radius.lg)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = token.typography.bodyMedium,
            color = token.colors.textSecondary
        )
    }
}
