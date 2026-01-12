package com.example.gobarbd.feature.customer.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gobarbd.R
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val user = FirebaseAuth.getInstance().currentUser
        view.findViewById<TextView>(R.id.txtProfileName).text =
            user?.displayName ?: "Customer"
        view.findViewById<TextView>(R.id.txtProfileEmail).text =
            user?.email ?: "Not signed in"
        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
        }
        return view
    }
}
