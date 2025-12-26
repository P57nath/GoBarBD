package com.example.gobarbd

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Barbershop(
    val name: String,
    val location: String,
    val rating: Float,
    val imageResource: Int,
    val distance: Float = 0f,
    val categories: List<String> = emptyList()
) : Parcelable
