package com.hoabui.virtualbody3d.ui.messages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.domain.model.chat.MessageThread
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.messages.components.MessageDetailContent
import com.hoabui.virtualbody3d.ui.messages.components.MessagesErrorState
import com.hoabui.virtualbody3d.ui.messages.components.MessagesLoadingState
import com.hoabui.virtualbody3d.ui.messages.viewmodel.MessageDetailViewModel

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

