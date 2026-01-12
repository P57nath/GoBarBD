package com.example.gobarbd.core.data.model

data class ChatMessage(
    val id: String,
    val senderId: String,
    val message: String,
    val timestamp: Long
)
