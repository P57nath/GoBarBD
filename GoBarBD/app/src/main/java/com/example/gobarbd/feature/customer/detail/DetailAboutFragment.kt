package com.example.gobarbd.feature.customer.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R

class DetailAboutFragment : Fragment() {

    private lateinit var viewModel: BarbershopDetailViewModel
    private lateinit var barberAdapter: BarberListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_detail_about, container, false)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerBarbers)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        barberAdapter = BarberListAdapter(mutableListOf())
        recycler.adapter = barberAdapter
        view.findViewById<View>(R.id.progressBarbers).visibility = View.VISIBLE

        viewModel = ViewModelProvider(requireActivity())[BarbershopDetailViewModel::class.java]
        viewModel.shop.observe(viewLifecycleOwner) { shop ->
            view.findViewById<TextView>(R.id.txtAbout).text =
                if (shop.description.isNotBlank()) shop.description
                else "At ${shop.name}, our dedicated team delivers a premium experience."
        }
        viewModel.barbers.observe(viewLifecycleOwner) { list ->
            barberAdapter.updateData(list)
            view.findViewById<View>(R.id.progressBarbers).visibility = View.GONE
            view.findViewById<View>(R.id.txtBarbersEmpty).visibility =
                if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        return view
    }

    companion object {
        private const val ARG_SHOP_ID = "SHOP_ID"

        fun newInstance(shopId: String): DetailAboutFragment {
            val fragment = DetailAboutFragment()
            val args = Bundle()
            args.putString(ARG_SHOP_ID, shopId)
            fragment.arguments = args
            return fragment
        }
    }
}
