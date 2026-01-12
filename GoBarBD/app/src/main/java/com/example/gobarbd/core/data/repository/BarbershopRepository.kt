package com.example.gobarbd.core.data.repository

import com.example.gobarbd.R
import com.example.gobarbd.core.data.model.Barbershop

object BarbershopRepository {

    fun getNearest(): List<Barbershop> = listOf(
        Barbershop(
            "Alana Barbershop - Haircut massage & Spa",
            "Banguntapan (5 km)",
            4.5f,
            R.drawable.shop1,
            5f,
            listOf("Basic haircut", "Massage")
        ),
        Barbershop(
            "Hercha Barbershop - Haircut & Styling",
            "Jalan Kaliurang (8 km)",
            5.0f,
            R.drawable.shop2,
            8f,
            listOf("Basic haircut", "Styling")
        ),
        Barbershop(
            "Barberking - Haircut styling & massage",
            "Jogja Expo Centre (12 km)",
            4.5f,
            R.drawable.shop3,
            12f,
            listOf("Basic haircut", "Massage")
        ),
        Barbershop(
            "Gentleman Barber Studio",
            "Seturan (6 km)",
            4.7f,
            R.drawable.shop4,
            6f,
            listOf("Haircut", "Beard trim")
        ),
        Barbershop(
            "Urban Cut Barbershop",
            "Gejayan (4 km)",
            4.6f,
            R.drawable.shop1,
            4f,
            listOf("Haircut", "Styling")
        ),
        Barbershop(
            "Classic Men Barber",
            "Maguwoharjo (9 km)",
            4.4f,
            R.drawable.shop2,
            9f,
            listOf("Haircut", "Massage")
        )
    )

    fun getRecommended(): List<Barbershop> = listOf(
        Barbershop(
            "Master piece Barbershop",
            "Joga Expo Centre (2 km)",
            5.0f,
            R.drawable.recommended1,
            2f,
            listOf("Premium haircut", "Styling")
        ),
        Barbershop(
            "Varcity Barbershop Jogja",
            "Condongcatur (10 km)",
            4.5f,
            R.drawable.recommended2,
            10f,
            listOf("Haircut", "Massage")
        ),
        Barbershop(
            "Cheeky Monkey Barber & Men Stuff",
            "Jl Taman Siswa (8 km)",
            5.0f,
            R.drawable.recommended3,
            8f,
            listOf("Haircut", "Beard trim")
        ),
        Barbershop(
            "Barberman Premium Cut",
            "Demangan (3 km)",
            4.8f,
            R.drawable.recommended4,
            3f,
            listOf("Premium haircut", "Styling")
        ),
        Barbershop(
            "Royal Men Salon",
            "Babarsari (7 km)",
            4.6f,
            R.drawable.shop3,
            7f,
            listOf("Haircut", "Massage")
        )
    )
}
