package com.hoabui.virtualbody3d.ui.messages.state

import com.hoabui.virtualbody3d.domain.model.MessageThread

data class MessagesUiState(
    val isLoading: Boolean = false,
    val threads: List<MessageThread> = emptyList()
)

