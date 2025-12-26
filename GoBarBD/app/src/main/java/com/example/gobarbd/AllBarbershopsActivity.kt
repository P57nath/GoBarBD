package com.example.gobarbd

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class AllBarbershopsActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var edtSearch: EditText
    private lateinit var btnFilter: Button
    private lateinit var txtResultsCount: TextView
    private lateinit var recyclerAllBarbershops: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout

    private lateinit var barbershopAdapter: BarbershopAdapter
    private lateinit var allBarbershopsList: MutableList<Barbershop>
    private lateinit var filteredList: MutableList<Barbershop>

    private var listType: String = "nearest" // "nearest" or "recommended"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_barbershops)

        initViews()
        setupToolbar()
        loadData()
        setupRecyclerView()
        setupListeners()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        edtSearch = findViewById(R.id.edtSearch)
        btnFilter = findViewById(R.id.btnFilter)
        txtResultsCount = findViewById(R.id.txtResultsCount)
        recyclerAllBarbershops = findViewById(R.id.recyclerAllBarbershops)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        listType = intent.getStringExtra("LIST_TYPE") ?: "nearest"

        toolbar.title = if (listType == "nearest") {
            "Nearest Barbershops"
        } else {
            "Most Recommended"
        }

        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadData() {
        val barbershopsArray =
            intent.getParcelableArrayListExtra<Barbershop>("BARBERSHOPS_LIST")

        allBarbershopsList = barbershopsArray?.toMutableList() ?: loadDefaultData()
        filteredList = allBarbershopsList.toMutableList()

        updateResultsCount()
    }

    private fun loadDefaultData(): MutableList<Barbershop> {
        val list = mutableListOf(
            Barbershop(
                "Alana Barbershop - Haircut massage & Spa",
                "Banguntapan (5 km)",
                4.5f,
                R.drawable.shop1,
                5.0f,
                listOf("Basic haircut", "Massage")
            ),
            Barbershop(
                "Hercha Barbershop - Haircut & Styling",
                "Jalan Kaliurang (8 km)",
                5.0f,
                R.drawable.shop2,
                8.0f,
                listOf("Basic haircut", "Styling")
            ),
            Barbershop(
                "Barberking - Haircut styling & massage",
                "Jogja Expo Centre (12 km)",
                4.5f,
                R.drawable.shop3,
                12.0f,
                listOf("Basic haircut", "Styling")
            )
        )

        if (listType == "nearest") {
            list.sortBy { it.distance }
        }

        return list
    }

    private fun setupRecyclerView() {
        barbershopAdapter = BarbershopAdapter(filteredList) { shop ->
            // TODO: Navigate to barbershop detail screen
        }

        recyclerAllBarbershops.apply {
            layoutManager = GridLayoutManager(this@AllBarbershopsActivity, 1)
            adapter = barbershopAdapter
        }
    }

    private fun setupListeners() {
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterBarbershops(s.toString())
            }
        })

        btnFilter.setOnClickListener { showFilterBottomSheet() }
    }

    private fun filterBarbershops(query: String) {
        filteredList.clear()

        if (query.isEmpty()) {
            filteredList.addAll(allBarbershopsList)
        } else {
            val searchQuery = query.lowercase()
            filteredList.addAll(
                allBarbershopsList.filter {
                    it.name.lowercase().contains(searchQuery) ||
                            it.location.lowercase().contains(searchQuery)
                }
            )
        }

        updateUI()
    }

    private fun showFilterBottomSheet() {
        val filterBottomSheet = FilterBottomSheet.newInstance()
        filterBottomSheet.setOnFilterAppliedListener { applyFilters(it) }
        filterBottomSheet.show(supportFragmentManager, "FilterBottomSheet")
    }

    private fun applyFilters(filterData: FilterData) {
        filteredList.clear()

        filteredList.addAll(
            allBarbershopsList.filter { shop ->
                val matchesRating = shop.rating >= filterData.rating
                val matchesDistance =
                    shop.distance in filterData.minDistance..filterData.maxDistance
                val matchesCategory =
                    filterData.categories.isEmpty() ||
                            shop.categories.any { it in filterData.categories }

                matchesRating && matchesDistance && matchesCategory
            }
        )

        if (filterData.sortByNearest) {
            filteredList.sortBy { it.distance }
        } else {
            filteredList.sortByDescending { it.distance }
        }

        updateUI()
    }

    private fun updateUI() {
        updateResultsCount()
        barbershopAdapter.notifyDataSetChanged()

        val isEmpty = filteredList.isEmpty()
        recyclerAllBarbershops.visibility = if (isEmpty) View.GONE else View.VISIBLE
        layoutEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun updateResultsCount() {
        val count = filteredList.size
        txtResultsCount.text = "Found $count barbershop${if (count != 1) "s" else ""}"
    }
}
