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
import com.example.gobarbd.feature.auth.LoginActivity
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
        view.findViewById<TextView>(R.id.txtProfilePhone).visibility = View.GONE
        view.findViewById<View>(R.id.btnEditProfile).visibility = View.GONE
        view.findViewById<View>(R.id.txtAvailabilityLabel).visibility = View.GONE
        view.findViewById<View>(R.id.txtAvailability).visibility = View.GONE
        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
            val intent = android.content.Intent(requireContext(), LoginActivity::class.java).apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
        }
        return view
    }
}
