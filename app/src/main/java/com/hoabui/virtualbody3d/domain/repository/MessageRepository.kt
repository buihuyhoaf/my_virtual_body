package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.MessageThread

interface MessageRepository {
    fun getMessageThreads(): List<MessageThread>
    fun getMessageById(messageId: String): MessageThread?
}

