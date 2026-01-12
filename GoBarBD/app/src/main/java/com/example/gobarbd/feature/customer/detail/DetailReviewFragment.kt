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

class DetailReviewFragment : Fragment() {

    private lateinit var viewModel: BarbershopDetailViewModel
    private lateinit var adapter: ReviewListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_detail_review, container, false)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerReviews)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = ReviewListAdapter(mutableListOf())
        recycler.adapter = adapter
        view.findViewById<View>(R.id.progressReviews).visibility = View.VISIBLE

        viewModel = ViewModelProvider(requireActivity())[BarbershopDetailViewModel::class.java]
        viewModel.reviews.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
            view.findViewById<View>(R.id.progressReviews).visibility = View.GONE
            view.findViewById<View>(R.id.txtReviewsEmpty).visibility =
                if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        return view
    }

    companion object {
        fun newInstance(shopId: String): DetailReviewFragment {
            val fragment = DetailReviewFragment()
            val args = Bundle()
            args.putString("SHOP_ID", shopId)
            fragment.arguments = args
            return fragment
        }
    }
}
