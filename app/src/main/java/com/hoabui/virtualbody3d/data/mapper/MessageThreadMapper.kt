package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.MessageThreadDto
import com.hoabui.virtualbody3d.domain.model.chat.MessageThread

fun MessageThreadDto.toDomain(): MessageThread = MessageThread(
    id = id,
    senderId = senderId,
    lastMessage = lastMessage,
    timestamp = timestamp,
    isRead = isRead
)
