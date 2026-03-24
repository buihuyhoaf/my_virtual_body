package com.hoabui.virtualbody3d.ui.messages.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.Icon
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GIconButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GIconButtonVariant
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.chat.ChatBubbleItem
import com.hoabui.virtualbody3d.domain.model.chat.MessageThread
import com.hoabui.virtualbody3d.ui.common_ui.atom.field.GTextField
import com.hoabui.virtualbody3d.ui.common_ui.molecule.topbar.GTopBar
import com.hoabui.virtualbody3d.ui.common_ui.molecule.topbar.GTopBarBackIcon
import com.hoabui.virtualbody3d.ui.common_ui.organism.scaffold.GScaffold
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
internal fun MessageDetailContent(
    message: MessageThread,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val chatMessages = remember(message) {
        buildChatBubblesFromThread(message)
    }
    var inputText by remember { mutableStateOf("") }

    GScaffold(
        modifier = modifier,
        topBar = {
            GTopBar(
                title = message.senderId,
                windowInsets = WindowInsets(0),
                navigationIcon = { GTopBarBackIcon(onBack = onBack) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = token.spacing.md, vertical = token.spacing.xs),
                verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
                reverseLayout = false,
            ) {
                items(
                    items = chatMessages,
                    key = { "${it.isOutgoing}-${it.text.take(20)}" },
                ) { item ->
                    ChatBubbleRow(
                        text = item.text,
                        isOutgoing = item.isOutgoing,
                        showAvatar = !item.isOutgoing,
                    )
                }
            }

            ChatInputBar(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = token.spacing.xs, bottom = token.spacing.md),
            )
        }
    }
}

@Composable
internal fun ChatBubbleRow(
    text: String,
    isOutgoing: Boolean,
    showAvatar: Boolean = !isOutgoing,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isOutgoing) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom,
    ) {
        if (showAvatar) {
            Box(
                modifier = Modifier
                    .size(token.chat.avatarSizeBubble)
                    .clip(CircleShape)
                    .background(token.colors.surfaceOverlay),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.body_unsplash),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
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
                        bottomEnd = if (isOutgoing) token.radius.sm else token.radius.md,
                    ),
                )
                .background(
                    if (isOutgoing) token.colors.primary else token.colors.surfaceOverlay,
                )
                .padding(
                    horizontal = token.spacing.md,
                    vertical = token.spacing.xs,
                ),
        ) {
            Text(
                text = text,
                style = token.typography.bodyMedium,
                color = if (isOutgoing) token.colors.onPrimary else token.colors.textPrimary,
            )
        }
    }
}

@Composable
internal fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
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
                    vertical = token.spacing.xs,
                ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
            ) {
                GTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(
                            min = token.chat.inputMinHeight,
                            max = token.chat.inputMaxHeight,
                        )
                        .widthIn(min = 0.dp),
                    placeholder = "Type a message...",
                    singleLine = false,
                    maxLines = Int.MAX_VALUE,
                )
                Box(
                    modifier = Modifier
                        .size(token.chat.sendButtonSize)
                        .then(if (sendEnabled) Modifier else Modifier.alpha(0.5f)),
                    contentAlignment = Alignment.Center,
                ) {
                    GIconButton(
                        onClick = { /* Send logic not implemented */ },
                        modifier = Modifier.fillMaxSize(),
                        enabled = sendEnabled,
                        variant = GIconButtonVariant.Filled,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                        )
                    }
                }
            }
        }
    }
}

internal fun buildChatBubblesFromThread(thread: MessageThread): List<ChatBubbleItem> {
    val incoming = listOf(ChatBubbleItem(text = thread.lastMessage, isOutgoing = false))
    val outgoing = listOf(ChatBubbleItem(text = "Thanks!", isOutgoing = true))
    return incoming + outgoing
}
