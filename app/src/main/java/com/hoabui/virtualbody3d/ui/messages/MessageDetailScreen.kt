package com.hoabui.virtualbody3d.ui.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.chat.ChatBubbleItem
import com.hoabui.virtualbody3d.domain.model.chat.MessageThread
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.messages.components.MessagesErrorState
import com.hoabui.virtualbody3d.ui.messages.components.MessagesLoadingState
import com.hoabui.virtualbody3d.ui.messages.viewmodel.MessageDetailViewModel
import com.hoabui.virtualbody3d.navigation.AppTopBarBack
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.outlinedTextFieldColors

@Composable
fun MessageDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MessageDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    UiStateContent(
        state = state,
        modifier = modifier.fillMaxSize(),
        loadingContent = { mod -> MessagesLoadingState(modifier = mod) },
        errorContent = { mod, message -> MessagesErrorState(message = message, modifier = mod) },
        successContent = { mod, message ->
            if (message == null) {
                MessagesErrorState(
                    message = "Message not found",
                    modifier = mod
                )
            } else {
                MessageDetailContent(
                    message = message,
                    onBack = onBack,
                    modifier = mod
                )
            }
        }
    )
}

@Composable
private fun MessageDetailContent(
    message: MessageThread,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val chatMessages = remember(message) {
        buildChatBubblesFromThread(message)
    }
    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // 1. Header with back button, avatar, sender name
        AppTopBarBack(onBack = onBack) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(token.spacing.xs)
            ) {
                Box(
                    modifier = Modifier
                        .size(token.chat.avatarSizeHeader)
                        .clip(CircleShape)
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
                Text(
                    text = message.senderId,
                    style = token.typography.titleMedium,
                    color = token.colors.textPrimary
                )
            }
        }

        // 2. Message list (takes remaining space)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = token.spacing.md, vertical = token.spacing.xs),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
            reverseLayout = false
        ) {
            items(
                items = chatMessages,
                key = { "${it.isOutgoing}-${it.text.take(20)}" }
            ) { item ->
                ChatBubbleRow(
                    text = item.text,
                    isOutgoing = item.isOutgoing,
                    showAvatar = !item.isOutgoing
                )
            }
        }

        // 3. Input bar
        ChatInputBar(
            value = inputText,
            onValueChange = { inputText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = token.spacing.xs, bottom = token.spacing.md)
        )
    }
}

@Composable
private fun ChatBubbleRow(
    text: String,
    isOutgoing: Boolean,
    showAvatar: Boolean = !isOutgoing,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isOutgoing) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (showAvatar) {
            Box(
                modifier = Modifier
                    .size(token.chat.avatarSizeBubble)
                    .clip(CircleShape)
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
            Spacer(modifier = Modifier.width(token.spacing.xs))
        }
        Box(
            modifier = Modifier
                .widthIn(max = token.chat.bubbleMaxWidth)
                .clip(
                    RoundedCornerShape(
                        topStart = token.radius.md,
                        topEnd = token.radius.md,
                        bottomStart = if (isOutgoing) token.radius.md else token.radius.sm,
                        bottomEnd = if (isOutgoing) token.radius.sm else token.radius.md
                    )
                )
                .background(
                    if (isOutgoing) token.colors.primary else token.colors.surfaceOverlay
                )
                .padding(
                    horizontal = token.spacing.md,
                    vertical = token.spacing.xs
                )
        ) {
            Text(
                text = text,
                style = token.typography.bodyMedium,
                color = if (isOutgoing) token.colors.onPrimary else token.colors.textPrimary
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val sendEnabled = value.isNotBlank()
    Row(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(token.radius.pill))
                .background(token.colors.surfaceOverlay)
                .padding(
                    horizontal = token.spacing.md,
                    vertical = token.spacing.xs
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(token.spacing.xs)
            ) {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(
                            min = token.chat.inputMinHeight,
                            max = token.chat.inputMaxHeight
                        )
                        .widthIn(min = 0.dp),
                    placeholder = {
                        Text(
                            text = "Type a message...",
                            style = token.typography.bodyMedium,
                            color = token.colors.textSecondary
                        )
                    },
                    colors = outlinedTextFieldColors(token.colors),
                    singleLine = false,
                    maxLines = Int.MAX_VALUE
                )
                Box(
                    modifier = Modifier
                        .size(token.chat.sendButtonSize)
                        .then(if (sendEnabled) Modifier else Modifier.alpha(0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { /* Send logic not implemented */ },
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(token.colors.primary),
                        enabled = sendEnabled
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = token.colors.onPrimary
                        )
                    }
                }
            }
        }
    }
}

private fun buildChatBubblesFromThread(thread: MessageThread): List<ChatBubbleItem> {
    val incoming = listOf(
        ChatBubbleItem(text = thread.lastMessage, isOutgoing = false)
    )
    val outgoing = listOf(
        ChatBubbleItem(text = "Thanks!", isOutgoing = true)
    )
    return incoming + outgoing
}
