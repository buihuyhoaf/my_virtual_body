package com.hoabui.virtualbody3d.domain.model

data class MessageThread(
    val id: String,
    val senderName: String,
    val lastMessage: String,
    val content: String,
    val timestamp: String,
    val isRead: Boolean
)

