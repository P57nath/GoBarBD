package com.example.gobarbd.feature.customer.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R
import com.example.gobarbd.core.data.model.Barbershop
import com.example.gobarbd.feature.customer.detail.BarbershopDetailActivity
import com.example.gobarbd.feature.customer.filter.FilterBottomSheet
import com.example.gobarbd.feature.customer.search.AllBarbershopsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var nearestAdapter: NearestBarbershopAdapter
    private lateinit var recommendedAdapter: RecommendedBarbershopAdapter
    private var nearestList: List<Barbershop> = emptyList()
    private var recommendedList: List<Barbershop> = emptyList()
    private var allList: List<Barbershop> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        setupNearest(view)
        setupRecommended(view)
        setupObservers()
        setupActions(view)
        view.findViewById<View>(R.id.progressNearest).visibility = View.VISIBLE
        view.findViewById<View>(R.id.progressRecommended).visibility = View.VISIBLE
        viewModel.loadShops()

        return view
    }

    /* -----------------------------
       NEAREST BARBERSHOPS
    ------------------------------ */
    private fun setupNearest(view: View) {
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerNearestShops)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        nearestAdapter = NearestBarbershopAdapter(mutableListOf()) { shop ->
            val intent = Intent(requireContext(), BarbershopDetailActivity::class.java).apply {
                putExtra("SHOP_ID", shop.id)
                putExtra("SHOP_NAME", shop.name)
            }
            startActivity(intent)
        }
        recycler.adapter = nearestAdapter
    }

    /* -----------------------------
       RECOMMENDED BARBERSHOPS
    ------------------------------ */
    private fun setupRecommended(view: View) {
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerRecommended)
        recycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        recommendedAdapter = RecommendedBarbershopAdapter(mutableListOf()) { shop ->
            val intent = Intent(requireContext(), BarbershopDetailActivity::class.java).apply {
                putExtra("SHOP_ID", shop.id)
                putExtra("SHOP_NAME", shop.name)
            }
            startActivity(intent)
        }
        recycler.adapter = recommendedAdapter
    }

    private fun setupObservers() {
        viewModel.nearest.observe(viewLifecycleOwner) { list ->
            nearestList = list
            nearestAdapter.updateData(list)
            val emptyView = requireView().findViewById<View>(R.id.txtNearestEmpty)
            val progress = requireView().findViewById<View>(R.id.progressNearest)
            progress.visibility = View.GONE
            emptyView.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.recommended.observe(viewLifecycleOwner) { list ->
            recommendedList = list
            recommendedAdapter.updateData(list)
            val emptyView = requireView().findViewById<View>(R.id.txtRecommendedEmpty)
            val progress = requireView().findViewById<View>(R.id.progressRecommended)
            progress.visibility = View.GONE
            emptyView.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            allList = (nearestList + recommendedList).distinctBy { it.id }
        }
        viewModel.error.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /* -----------------------------
       BUTTON ACTIONS
    ------------------------------ */
    private fun setupActions(view: View) {

        // SEE ALL NEAREST
        view.findViewById<Button>(R.id.btnSeeAllNearest).setOnClickListener {
            val intent = Intent(requireContext(), AllBarbershopsActivity::class.java)
            intent.putExtra("LIST_TYPE", "nearest")
            intent.putParcelableArrayListExtra(
                "BARBERSHOPS_LIST",
                ArrayList(nearestList)
            )
            startActivity(intent)
        }

        // SEE ALL RECOMMENDED
        view.findViewById<Button>(R.id.btnSeeAllRecommended).setOnClickListener {
            val intent = Intent(requireContext(), AllBarbershopsActivity::class.java)
            intent.putExtra("LIST_TYPE", "recommended")
            intent.putParcelableArrayListExtra(
                "BARBERSHOPS_LIST",
                ArrayList(recommendedList)
            )
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.btnFilter).setOnClickListener {
            val filterBottomSheet = FilterBottomSheet.newInstance()

            filterBottomSheet.setOnFilterAppliedListener { filterData ->
                // TEMP: Just verify filter is working
                Toast.makeText(
                    requireContext(),
                    "Filter applied\nRating: ${filterData.rating}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            filterBottomSheet.show(childFragmentManager, "FilterBottomSheet")
        }

        view.findViewById<EditText>(R.id.edtSearch).setOnEditorActionListener { _, _, _ ->
            val query = view.findViewById<EditText>(R.id.edtSearch).text.toString().trim()
            if (query.isNotEmpty()) {
                val intent = Intent(requireContext(), AllBarbershopsActivity::class.java)
                intent.putExtra("LIST_TYPE", "search")
                intent.putParcelableArrayListExtra("BARBERSHOPS_LIST", ArrayList(allList))
                intent.putExtra("SEARCH_QUERY", query)
                startActivity(intent)
            }
            true
        }

        view.findViewById<View>(R.id.imgProfile).setOnClickListener {
            requireActivity()
                .findViewById<BottomNavigationView>(R.id.bottom_navigation)
                ?.selectedItemId = R.id.nav_profile
        }

        // FIND NOW (GOOGLE MAPS)
        view.findViewById<Button>(R.id.btnFindNow).setOnClickListener {
            val mapIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("geo:0,0?q=barber+shop+near+me")
            )
            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(mapIntent)
            } else {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/barber+shop+near+me")
                    )
                )
            }
        }
    }
}
