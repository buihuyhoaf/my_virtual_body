package com.hoabui.virtualbody3d.ui.messages.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun MessagesEmptyState(modifier: Modifier = Modifier) {
    val token = GymTheme.token
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(token.radius.lg)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = token.spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
        ) {
            Text(
                text = stringResource(R.string.messages_empty_title),
                style = token.typography.titleMedium,
                color = token.colors.textSecondary
            )
            Text(
                text = stringResource(R.string.messages_empty_subtitle),
                style = token.typography.bodyMedium,
                color = token.colors.textSecondary
            )
        }
    }
}
