package com.example.gobarbd.feature.admin

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class AdminBookingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val textView = TextView(context).apply {
            text = "Admin Bookings"
            gravity = Gravity.CENTER
            textSize = 18f
        }
        return textView
    }
}
