package com.example.gobarbd.core.data.model

data class Booking(
    val id: String,
    val shopId: String,
    val shopName: String,
    val shopLocation: String,
    val rating: Float,
    val status: String,
    val imageRes: Int,
    val customerId: String = "",
    val startTimeMillis: Long = 0L,
    val endTimeMillis: Long = 0L,
    val totalPrice: Double = 0.0
)
