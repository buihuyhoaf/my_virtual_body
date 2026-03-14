package com.hoabui.virtualbody3d.data.model

data class MessageThreadDto(
    val id: String,
    val senderId: String,
    val lastMessage: String,
    val timestamp: String,
    val isRead: Boolean
)
