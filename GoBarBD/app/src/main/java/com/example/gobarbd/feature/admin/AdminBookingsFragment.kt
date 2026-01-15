package com.example.gobarbd.feature.admin

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

class AdminBookingsFragment : Fragment() {

    private lateinit var viewModel: AdminBookingsViewModel
    private lateinit var adapter: AdminBookingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_admin_bookings, container, false)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerAdminBookings)
        val progress = view.findViewById<ProgressBar>(R.id.progressAdminBookings)
        val empty = view.findViewById<TextView>(R.id.txtAdminBookingsEmpty)

        adapter = AdminBookingAdapter(mutableListOf())
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        val btnAll = view.findViewById<Button>(R.id.btnFilterAll)
        val btnActive = view.findViewById<Button>(R.id.btnFilterActive)
        val btnCompleted = view.findViewById<Button>(R.id.btnFilterCompleted)
        val btnCancelled = view.findViewById<Button>(R.id.btnFilterCancelled)
        val btnNoShow = view.findViewById<Button>(R.id.btnFilterNoShow)

        viewModel = ViewModelProvider(this)[AdminBookingsViewModel::class.java]
        viewModel.bookings.observe(viewLifecycleOwner) { list ->
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

        btnAll.setOnClickListener { applyFilter("ALL", btnAll, btnActive, btnCompleted, btnCancelled, btnNoShow) }
        btnActive.setOnClickListener { applyFilter("ACTIVE", btnActive, btnAll, btnCompleted, btnCancelled, btnNoShow) }
        btnCompleted.setOnClickListener { applyFilter("COMPLETED", btnCompleted, btnAll, btnActive, btnCancelled, btnNoShow) }
        btnCancelled.setOnClickListener { applyFilter("CANCELLED", btnCancelled, btnAll, btnActive, btnCompleted, btnNoShow) }
        btnNoShow.setOnClickListener { applyFilter("NO_SHOW", btnNoShow, btnAll, btnActive, btnCompleted, btnCancelled) }

        progress.visibility = View.VISIBLE
        viewModel.load()
        applyFilter("ALL", btnAll, btnActive, btnCompleted, btnCancelled, btnNoShow)

        return view
    }

    private fun applyFilter(
        status: String,
        selected: Button,
        btn1: Button,
        btn2: Button,
        btn3: Button,
        btn4: Button
    ) {
        viewModel.filterBy(status)
        setSelected(selected, true)
        setSelected(btn1, false)
        setSelected(btn2, false)
        setSelected(btn3, false)
        setSelected(btn4, false)
    }

    private fun setSelected(button: Button, isSelected: Boolean) {
        if (isSelected) {
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF1E3878.toInt()))
            button.setTextColor(android.graphics.Color.WHITE)
        } else {
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFEEF1FB.toInt()))
            button.setTextColor(0xFF1E3878.toInt())
        }
    }
}
