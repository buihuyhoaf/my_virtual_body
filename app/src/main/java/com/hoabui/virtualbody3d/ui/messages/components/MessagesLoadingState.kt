package com.hoabui.virtualbody3d.ui.messages.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun MessagesLoadingState(modifier: Modifier = Modifier) {
    val token = GymTheme.token
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(token.radius.lg)),
        contentAlignment = Alignment.Center
    ) {
        GText(
            text = stringResource(R.string.messages_loading),
            style = token.typography.bodyMedium,
            color = token.colors.textSecondary
        )
    }
}
