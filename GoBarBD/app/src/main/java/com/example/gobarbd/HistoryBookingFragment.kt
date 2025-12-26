package com.example.gobarbd

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoryBookingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_history_booking, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerHistory)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = HistoryBookingAdapter(getDummyHistory())

        return view
    }

    // Temporary static data (replace with backend later)
    private fun getDummyHistory(): List<HistoryBookingModel> {
        return listOf(
            HistoryBookingModel(
                R.drawable.shop1,
                "Varcity Barbershop Jogja ex The Varcher",
                "Condongcatur (10 km)",
                4.5f
            ),
            HistoryBookingModel(
                R.drawable.shop2,
                "Twinsky Monkey Barber & Men Stuff",
                "Jl Taman Siswa (8 km)",
                5.0f
            ),
            HistoryBookingModel(
                R.drawable.shop3,
                "Barberman – Haircut styling & massage",
                "J-Walk Centre (17 km)",
                4.5f
            ),
            HistoryBookingModel(
                R.drawable.shop4,
                "Alana Barbershop – Haircut massage & Spa",
                "Banguntapan (5 km)",
                4.5f
            )
        )
    }
}
