package com.hoabui.virtualbody3d.ui.messages.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun MessagesScreenHeader(
    modifier: Modifier = Modifier,
    showTitle: Boolean = false
) {
    val token = GymTheme.token
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxs)
    ) {
        if (showTitle) {
            Text(
                text = stringResource(R.string.messages_screen_title),
                style = token.typography.headlineSmall,
                color = token.colors.textPrimary
            )
        }
        Text(
            text = stringResource(R.string.messages_screen_subtitle),
            style = token.typography.bodyMedium,
            color = token.colors.textSecondary
        )
    }
}
