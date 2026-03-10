package com.hoabui.virtualbody3d.ui.messages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.MessageThread
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.messages.components.MessageListItem
import com.hoabui.virtualbody3d.ui.messages.components.MessagesEmptyState
import com.hoabui.virtualbody3d.ui.messages.components.MessagesErrorState
import com.hoabui.virtualbody3d.ui.messages.components.MessagesLoadingState
import com.hoabui.virtualbody3d.ui.messages.components.MessagesScreenHeader
import com.hoabui.virtualbody3d.ui.messages.state.MessagesUiState
import com.hoabui.virtualbody3d.ui.messages.viewmodel.MessagesViewModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun MessagesScreen(
    modifier: Modifier = Modifier,
    viewModel: MessagesViewModel = hiltViewModel(),
    onMessageClick: (MessageThread) -> Unit
) {
    val token = GymTheme.token
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = token.spacing.md,
                vertical = token.spacing.xs
            ),
        verticalArrangement = Arrangement.spacedBy(token.spacing.md)
    ) {
        MessagesScreenHeader(showTitle = false)
        UiStateContent(
            state = screenState,
            modifier = Modifier.fillMaxSize(),
            loadingContent = { mod -> MessagesLoadingState(modifier = mod) },
            errorContent = { mod, message -> MessagesErrorState(message = message, modifier = mod) },
            successContent = { mod, data ->
                val threads = data.threads
                if (threads.isEmpty()) {
                    MessagesEmptyState(modifier = mod)
                } else {
                    Surface(
                        modifier = mod.fillMaxSize(),
                        shape = RoundedCornerShape(token.radius.lg),
                        color = token.colors.surface,
                        shadowElevation = token.card.elevation
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            items(
                                items = threads,
                                key = { it.id }
                            ) { thread ->
                                MessageListItem(
                                    message = thread,
                                    user = data.usersById[thread.senderId],
                                    onClick = { onMessageClick(thread) }
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}


