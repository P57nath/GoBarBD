package com.example.gobarbd.core.data.model

data class Booking(
    val id: String,
    val shopId: String,
    val shopName: String,
    val shopLocation: String,
    val rating: Float,
    val status: String,
    val imageRes: Int
)
