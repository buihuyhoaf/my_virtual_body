package com.hoabui.virtualbody3d.ui.messages.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.chat.MessageThread
import com.hoabui.virtualbody3d.domain.model.chat.UserInfo
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun MessageListItem(
    message: MessageThread,
    user: UserInfo?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val avatarSize = 48.dp
    val avatarToTextSpacing = token.spacing.md

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = token.colors.surface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(
                    horizontal = token.spacing.md,
                    vertical = token.spacing.xs
                )
                .padding(vertical = token.spacing.xxs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(avatarToTextSpacing)
        ) {
            Box(
                modifier = Modifier
                    .size(avatarSize)
                    .clip(RoundedCornerShape(999.dp))
                    .background(token.colors.surfaceOverlay),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.body_unsplash),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(token.spacing.xxs)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = user?.displayName ?: message.senderId,
                        style = token.typography.bodyLarge,
                        color = token.colors.textPrimary,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = message.timestamp,
                        style = token.typography.labelSmall,
                        color = token.colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Clip
                    )
                }
                Text(
                    text = message.lastMessage,
                    style = token.typography.bodyMedium,
                    color = token.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Divider(
            color = token.colors.surfaceBorder,
            thickness = 1.dp,
            modifier = Modifier.padding(start = token.spacing.md + avatarSize + avatarToTextSpacing)
        )
    }
}
