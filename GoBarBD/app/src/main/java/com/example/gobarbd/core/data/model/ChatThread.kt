package com.example.gobarbd.core.data.model

data class ChatThread(
    val id: String,
    val shopName: String,
    val lastMessage: String,
    val timestamp: Long,
    val isActive: Boolean
)
