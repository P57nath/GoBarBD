package com.example.gobarbd.feature.barber

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BarberDashboardFragment : Fragment() {

    private lateinit var viewModel: BarberDashboardViewModel
    private lateinit var adapter: BarberQueueAdapter
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_barber_dashboard, container, false)
        val txtName = view.findViewById<TextView>(R.id.txtBarberName)
        val txtTotal = view.findViewById<TextView>(R.id.txtTotalCount)
        val txtActive = view.findViewById<TextView>(R.id.txtActiveCount)
        val txtCompleted = view.findViewById<TextView>(R.id.txtCompletedCount)
        val txtCompletedWeek = view.findViewById<TextView>(R.id.txtCompletedWeek)
        val txtEarningsWeek = view.findViewById<TextView>(R.id.txtEarningsWeek)
        val progress = view.findViewById<ProgressBar>(R.id.progressBarberQueue)
        val empty = view.findViewById<TextView>(R.id.txtBarberQueueEmpty)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerBarberQueue)

        adapter = BarberQueueAdapter(mutableListOf())
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        viewModel = ViewModelProvider(requireActivity())[BarberDashboardViewModel::class.java]
        viewModel.summary.observe(viewLifecycleOwner) { summary ->
            txtTotal.text = summary.total.toString()
            txtActive.text = summary.active.toString()
            txtCompleted.text = summary.completed.toString()
            txtCompletedWeek.text = summary.completedWeek.toString()
            txtEarningsWeek.text = "$${summary.earningsWeek.toInt()}"
        }
        viewModel.todayQueue.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
            progress.visibility = View.GONE
            empty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.error.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.btnViewBookings).setOnClickListener {
            requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                R.id.bottom_navigation
            )?.selectedItemId = R.id.nav_barber_bookings
        }
        view.findViewById<Button>(R.id.btnViewEarnings).setOnClickListener {
            val intent = android.content.Intent(
                requireContext(),
                BarberEarningsActivity::class.java
            )
            startActivity(intent)
        }

        progress.visibility = View.VISIBLE
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            progress.visibility = View.GONE
            empty.visibility = View.VISIBLE
            txtName.text = "Barber"
            Toast.makeText(requireContext(), "Please login", Toast.LENGTH_SHORT).show()
        } else {
            txtName.text = user.displayName ?: "Barber"
            firestore.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    val barberId = doc.getString("barberId") ?: user.uid
                    viewModel.load(barberId)
                }
                .addOnFailureListener {
                    viewModel.load(user.uid)
                }
        }

        return view
    }
}
