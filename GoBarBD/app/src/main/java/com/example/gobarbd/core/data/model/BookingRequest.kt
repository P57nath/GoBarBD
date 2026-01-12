package com.example.gobarbd.core.data.model

data class BookingRequest(
    val customerId: String,
    val shopId: String,
    val shopName: String = "",
    val shopLocation: String = "",
    val barberId: String,
    val serviceId: String,
    val servicePrice: Double,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val paymentMethod: String,
    val status: String = "PENDING"
)
