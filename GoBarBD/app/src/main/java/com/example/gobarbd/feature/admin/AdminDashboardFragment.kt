package com.example.gobarbd.feature.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R

class AdminDashboardFragment : Fragment() {

    private lateinit var viewModel: AdminDashboardViewModel
    private lateinit var adapter: AdminBookingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false)
        val txtShops = view.findViewById<TextView>(R.id.txtTotalShops)
        val txtBookings = view.findViewById<TextView>(R.id.txtTotalBookings)
        val txtActive = view.findViewById<TextView>(R.id.txtActiveBookings)
        val txtToday = view.findViewById<TextView>(R.id.txtTodayBookings)
        val progress = view.findViewById<ProgressBar>(R.id.progressAdminRecent)
        val empty = view.findViewById<TextView>(R.id.txtAdminRecentEmpty)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerAdminRecent)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = AdminBookingAdapter(mutableListOf())
        recycler.adapter = adapter

        viewModel = ViewModelProvider(this)[AdminDashboardViewModel::class.java]
        viewModel.summary.observe(viewLifecycleOwner) { summary ->
            txtShops.text = summary.totalShops.toString()
            txtBookings.text = summary.totalBookings.toString()
            txtActive.text = summary.activeBookings.toString()
            txtToday.text = summary.todayBookings.toString()
        }
        viewModel.recentBookings.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
            progress.visibility = View.GONE
            empty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.error.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
            progress.visibility = View.GONE
        }

        progress.visibility = View.VISIBLE
        viewModel.load()

        return view
    }
}
