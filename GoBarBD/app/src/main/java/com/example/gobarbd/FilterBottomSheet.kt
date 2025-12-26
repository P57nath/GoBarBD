package com.example.gobarbd

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.Slider

class FilterBottomSheet : BottomSheetDialogFragment() {

    private lateinit var chipGroupCategory: ChipGroup
    private lateinit var sliderRating: Slider
    private lateinit var txtRatingValue: TextView
    private lateinit var star1: ImageView
    private lateinit var star2: ImageView
    private lateinit var star3: ImageView
    private lateinit var star4: ImageView
    private lateinit var star5: ImageView
    private lateinit var chipNearest: Chip
    private lateinit var chipFarthest: Chip
    private lateinit var edtMinDistance: EditText
    private lateinit var edtMaxDistance: EditText
    private lateinit var btnApplyFilter: Button
    private lateinit var btnCloseFilter: ImageView

    private var onFilterApplied: ((FilterData) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupListeners()
    }

    private fun initViews(view: View) {
        chipGroupCategory = view.findViewById(R.id.chipGroupCategory)
        sliderRating = view.findViewById(R.id.sliderRating)
        txtRatingValue = view.findViewById(R.id.txtRatingValue)
        star1 = view.findViewById(R.id.star1)
        star2 = view.findViewById(R.id.star2)
        star3 = view.findViewById(R.id.star3)
        star4 = view.findViewById(R.id.star4)
        star5 = view.findViewById(R.id.star5)
        chipNearest = view.findViewById(R.id.chipNearest)
        chipFarthest = view.findViewById(R.id.chipFarthest)
        edtMinDistance = view.findViewById(R.id.edtMinDistance)
        edtMaxDistance = view.findViewById(R.id.edtMaxDistance)
        btnApplyFilter = view.findViewById(R.id.btnApplyFilter)
        btnCloseFilter = view.findViewById(R.id.btnCloseFilter)
    }

    private fun setupListeners() {
        // Rating slider change
        sliderRating.addOnChangeListener { slider, value, fromUser ->
            updateRatingDisplay(value)
        }

        // Close button
        btnCloseFilter.setOnClickListener {
            dismiss()
        }

        // Apply button
        btnApplyFilter.setOnClickListener {
            applyFilters()
        }

        // Star click listeners
        star1.setOnClickListener { updateRatingFromStar(1f) }
        star2.setOnClickListener { updateRatingFromStar(2f) }
        star3.setOnClickListener { updateRatingFromStar(3f) }
        star4.setOnClickListener { updateRatingFromStar(4f) }
        star5.setOnClickListener { updateRatingFromStar(5f) }

        // Initialize rating display
        updateRatingDisplay(sliderRating.value)
    }

    private fun updateRatingFromStar(rating: Float) {
        sliderRating.value = rating
        updateRatingDisplay(rating)
    }

    private fun updateRatingDisplay(rating: Float) {
        txtRatingValue.text = "(${"%.1f".format(rating)})"

        // Update star icons
        val stars = listOf(star1, star2, star3, star4, star5)
        stars.forEachIndexed { index, star ->
            if (index < rating.toInt()) {
                star.setImageResource(R.drawable.ic_star_filled)
                star.setColorFilter(resources.getColor(android.R.color.holo_orange_light, null))
            } else {
                star.setImageResource(R.drawable.ic_star_outline)
                star.setColorFilter(resources.getColor(android.R.color.darker_gray, null))
            }
        }
    }

    private fun applyFilters() {
        // Get selected categories
        val selectedCategories = mutableListOf<String>()
        val checkedIds = chipGroupCategory.checkedChipIds
        checkedIds.forEach { id ->
            val chip = view?.findViewById<Chip>(id)
            chip?.let { selectedCategories.add(it.text.toString()) }
        }

        // Get rating
        val rating = sliderRating.value

        // Get distance
        val minDistance = edtMinDistance.text.toString().toFloatOrNull() ?: 0.1f
        val maxDistance = edtMaxDistance.text.toString().toFloatOrNull() ?: 10f
        val isNearest = chipNearest.isChecked

        // Create filter data
        val filterData = FilterData(
            categories = selectedCategories,
            rating = rating,
            minDistance = minDistance,
            maxDistance = maxDistance,
            sortByNearest = isNearest
        )

        // Callback
        onFilterApplied?.invoke(filterData)

        // Close bottom sheet
        dismiss()
    }

    fun setOnFilterAppliedListener(listener: (FilterData) -> Unit) {
        onFilterApplied = listener
    }

    companion object {
        fun newInstance(): FilterBottomSheet {
            return FilterBottomSheet()
        }
    }
}

// Filter Data Class
data class FilterData(
    val categories: List<String>,
    val rating: Float,
    val minDistance: Float,
    val maxDistance: Float,
    val sortByNearest: Boolean
)