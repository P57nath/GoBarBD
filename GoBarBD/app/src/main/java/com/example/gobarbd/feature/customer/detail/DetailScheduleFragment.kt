package com.example.gobarbd.feature.customer.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R

class DetailScheduleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_detail_schedule, container, false)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerSchedule)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        val times = listOf(
            "08:00", "08:30", "09:00", "09:30", "10:00", "10:30"
        )
        recycler.adapter = ScheduleAdapter(times)
        view.findViewById<View>(R.id.progressSchedule).visibility = View.GONE
        view.findViewById<View>(R.id.txtScheduleEmpty).visibility =
            if (times.isEmpty()) View.VISIBLE else View.GONE
        return view
    }

    companion object {
        fun newInstance(shopId: String): DetailScheduleFragment {
            val fragment = DetailScheduleFragment()
            val args = Bundle()
            args.putString("SHOP_ID", shopId)
            fragment.arguments = args
            return fragment
        }
    }
}
