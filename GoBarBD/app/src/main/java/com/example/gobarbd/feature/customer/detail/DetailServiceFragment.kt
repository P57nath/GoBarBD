package com.example.gobarbd.feature.customer.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R

class DetailServiceFragment : Fragment() {

    private lateinit var viewModel: BarbershopDetailViewModel
    private lateinit var adapter: ServiceListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_detail_service, container, false)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerServices)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = ServiceListAdapter(mutableListOf())
        recycler.adapter = adapter

        viewModel = ViewModelProvider(requireActivity())[BarbershopDetailViewModel::class.java]
        viewModel.services.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
        }

        return view
    }

    companion object {
        fun newInstance(shopId: String): DetailServiceFragment {
            val fragment = DetailServiceFragment()
            val args = Bundle()
            args.putString("SHOP_ID", shopId)
            fragment.arguments = args
            return fragment
        }
    }
}
