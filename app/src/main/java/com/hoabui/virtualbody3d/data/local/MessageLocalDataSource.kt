package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.model.MessageThreadDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageLocalDataSource @Inject constructor() {

    fun getMessageThreads(): List<MessageThreadDto> = listOf(
        MessageThreadDto(
            id = "welcome",
            senderId = "coach_alex",
            lastMessage = "Here's a quick overview of how to get the most from your body insights.",
            timestamp = "Today • 09:24",
            isRead = false
        ),
        MessageThreadDto(
            id = "coaching_tip",
            senderId = "workout_bot",
            lastMessage = "Small, consistent changes will compound much faster than big, irregular efforts.",
            timestamp = "Yesterday • 20:10",
            isRead = true
        ),
        MessageThreadDto(
            id = "scan_reminder",
            senderId = "system",
            lastMessage = "It's been a while since your last 3D scan. A new scan helps you see subtle changes.",
            timestamp = "Mon • 14:05",
            isRead = false
        )
    )

    fun getMessageById(messageId: String): MessageThreadDto? =
        getMessageThreads().find { it.id == messageId }
}
