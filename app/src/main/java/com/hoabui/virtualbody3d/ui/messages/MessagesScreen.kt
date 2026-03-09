package com.hoabui.virtualbody3d.ui.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.MessageThread
import com.hoabui.virtualbody3d.ui.messages.viewmodel.MessagesViewModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun MessagesScreen(
    modifier: Modifier = Modifier,
    viewModel: MessagesViewModel = hiltViewModel(),
    onMessageClick: (MessageThread) -> Unit
) {
    val token = GymTheme.token
    val state by viewModel.state.collectAsStateWithLifecycle()
    val messages = state.threads

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = token.spacing.md,
                vertical = token.spacing.xs
            ),
        verticalArrangement = Arrangement.spacedBy(token.spacing.md)
    ) {
        Text(
            text = stringResource(R.string.tab_messages),
            style = token.typography.headlineSmall,
            color = token.colors.textPrimary
        )

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(token.radius.lg)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "...",
                    style = token.typography.bodyMedium,
                    color = token.colors.textSecondary
                )
            }
        } else if (messages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(token.radius.lg)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No messages yet",
                    style = token.typography.bodyMedium,
                    color = token.colors.textSecondary
                )
            }
        } else {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(token.radius.lg),
                color = token.colors.surface,
                shadowElevation = token.card.elevation
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(
                        items = messages,
                        key = { it.id }
                    ) { message ->
                        MessageListItem(
                            message = message,
                            onClick = { onMessageClick(message) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageListItem(
    message: MessageThread,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val avatarSize = 44.dp

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = token.spacing.md),
        color = token.colors.surface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = token.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xs)
        ) {
            Box(
                modifier = Modifier
                    .size(avatarSize)
                    .clip(RoundedCornerShape(999.dp))
                    .background(token.colors.surfaceOverlay),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message.senderName.firstOrNull()?.uppercase() ?: "",
                    style = token.typography.titleMedium,
                    color = token.colors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.senderName,
                        style = token.typography.bodyLarge,
                        color = token.colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.size(token.spacing.xs))
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
            modifier = Modifier
                .padding(start = avatarSize + token.spacing.xs)
        )
    }
}

@Composable
fun MessageDetailScreen(
    messageId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    // For now, reuse the same sample list; in a real implementation this would
    // be driven by its own ViewModel or a shared MessagesViewModel.
    val message = com.hoabui.virtualbody3d.data.repository.MessageRepositoryImpl()
        .getMessageThreads()
        .find { it.id == messageId } ?: return

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = token.spacing.md, vertical = token.spacing.xs)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(token.spacing.md)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xs)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.body_region_detail_back),
                    tint = token.colors.textPrimary
                )
            }
            Text(
                text = stringResource(R.string.tab_messages),
                style = token.typography.titleMedium,
                color = token.colors.textPrimary
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
        ) {
            Text(
                text = message.senderName,
                style = token.typography.headlineSmall,
                color = token.colors.textPrimary
            )
            Text(
                text = message.timestamp,
                style = token.typography.bodyMedium,
                color = token.colors.textSecondary
            )
        }

        Divider(color = token.colors.surfaceBorder)

        Text(
            text = message.content,
            style = token.typography.bodyMedium,
            color = token.colors.textPrimary
        )

        Spacer(modifier = Modifier.height(token.spacing.xl))
    }
}

