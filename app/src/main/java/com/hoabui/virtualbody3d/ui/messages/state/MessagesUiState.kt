package com.hoabui.virtualbody3d.ui.messages.state

import com.hoabui.virtualbody3d.domain.model.chat.UserInfo
import com.hoabui.virtualbody3d.domain.model.chat.MessageThread

data class MessagesUiState(
    val threads: List<MessageThread> = emptyList(),
    val usersById: Map<String, UserInfo> = emptyMap()
)

