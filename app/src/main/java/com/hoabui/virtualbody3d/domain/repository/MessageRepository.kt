package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.chat.MessageThread

interface MessageRepository {
    fun getMessageThreads(): List<MessageThread>
    fun getMessageById(messageId: String): MessageThread?
}

