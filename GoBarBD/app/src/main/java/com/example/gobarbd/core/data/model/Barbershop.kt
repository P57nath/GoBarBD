package com.example.gobarbd.core.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Barbershop(
    val name: String,
    val location: String,
    val rating: Float,
    val imageResource: Int,
    val distance: Float = 0f,
    val categories: List<String> = emptyList(),
    val id: String = "",
    val ratingCount: Int = 0,
    val isOpen: Boolean = true,
    val description: String = ""
) : Parcelable
