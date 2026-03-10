package com.hoabui.virtualbody3d.domain.model

data class MessageThread(
    val id: String,
    val senderId: String,
    val lastMessage: String,
    val timestamp: String,
    val isRead: Boolean
)

