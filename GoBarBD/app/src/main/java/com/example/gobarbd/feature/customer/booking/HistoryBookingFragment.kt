package com.example.gobarbd.feature.customer.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R
import com.google.firebase.auth.FirebaseAuth

class HistoryBookingFragment : Fragment() {

    private lateinit var viewModel: BookingListViewModel
    private lateinit var adapter: HistoryBookingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_history_booking, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerHistory)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = HistoryBookingAdapter(mutableListOf())
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(requireActivity())[BookingListViewModel::class.java]
        viewModel.history.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
            view.findViewById<View>(R.id.progressHistory).visibility = View.GONE
            view.findViewById<View>(R.id.txtHistoryEmpty).visibility =
                if (list.isEmpty()) View.VISIBLE else View.GONE
        }
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (!userId.isNullOrBlank()) {
            view.findViewById<View>(R.id.progressHistory).visibility = View.VISIBLE
            viewModel.load(userId)
        }

        return view
    }
}
