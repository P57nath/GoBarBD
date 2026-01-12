package com.example.gobarbd.feature.barber

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class BarberProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val textView = TextView(context).apply {
            text = "Barber Profile"
            gravity = Gravity.CENTER
            textSize = 18f
        }
        return textView
    }
}
