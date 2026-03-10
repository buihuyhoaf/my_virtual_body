package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.domain.model.MessageThread
import com.hoabui.virtualbody3d.domain.repository.MessageRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor() : MessageRepository {

    override fun getMessageThreads(): List<MessageThread> = listOf(
        MessageThread(
            id = "welcome",
            senderId = "coach_alex",
            lastMessage = "Here’s a quick overview of how to get the most from your body insights.",
            timestamp = "Today • 09:24",
            isRead = false
        ),
        MessageThread(
            id = "coaching_tip",
            senderId = "workout_bot",
            lastMessage = "Small, consistent changes will compound much faster than big, irregular efforts.",
            timestamp = "Yesterday • 20:10",
            isRead = true
        ),
        MessageThread(
            id = "scan_reminder",
            senderId = "system",
            lastMessage = "It’s been a while since your last 3D scan. A new scan helps you see subtle changes.",
            timestamp = "Mon • 14:05",
            isRead = false
        )
    )

    override fun getMessageById(messageId: String): MessageThread? =
        getMessageThreads().find { it.id == messageId }
}

