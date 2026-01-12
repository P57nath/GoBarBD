package com.example.gobarbd.feature.customer.review

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.gobarbd.R
import com.example.gobarbd.core.data.repository.BookingRepository
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class RatingReviewActivity : AppCompatActivity() {

    private var selectedRating = 4
    private var shopId: String = ""
    private var bookingId: String = ""
    private var canReview = false
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating_review)

        shopId = intent.getStringExtra("SHOP_ID") ?: ""
        bookingId = intent.getStringExtra("BOOKING_ID") ?: ""

        setupBackButton()
        setupStarRating()
        setupReviewChips()
        checkReviewEligibility()
        setupSendButton()
    }

    private fun setupBackButton() {
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupStarRating() {
        val star1 = findViewById<ImageView>(R.id.star1)
        val star2 = findViewById<ImageView>(R.id.star2)
        val star3 = findViewById<ImageView>(R.id.star3)
        val star4 = findViewById<ImageView>(R.id.star4)
        val star5 = findViewById<ImageView>(R.id.star5)
        val txtRatingNumber = findViewById<TextView>(R.id.txtRatingNumber)

        val stars = listOf(star1, star2, star3, star4, star5)

        stars.forEachIndexed { index, star ->
            star.setOnClickListener {
                selectedRating = index + 1
                updateStarDisplay(stars, selectedRating, txtRatingNumber)
            }
        }

        // Initialize with default rating
        updateStarDisplay(stars, selectedRating, txtRatingNumber)
    }

    private fun updateStarDisplay(
        stars: List<ImageView>,
        rating: Int,
        txtRatingNumber: TextView
    ) {
        stars.forEachIndexed { index, star ->
            if (index < rating) {
                star.setImageResource(R.drawable.ic_star_filled)
                star.setColorFilter(
                    ContextCompat.getColor(this, R.color.orange_primary)
                )
            } else {
                star.setImageResource(R.drawable.ic_star_outline)
                star.setColorFilter(
                    ContextCompat.getColor(this, android.R.color.darker_gray)
                )
            }
        }
        txtRatingNumber.text = "($rating.0)"
    }

    private fun setupSendButton() {
        val btnSendReview = findViewById<Button>(R.id.btnSendReview)
        val edtReview = findViewById<EditText>(R.id.edtReview)
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroupReviewTags)

        btnSendReview.setOnClickListener {
            if (!canReview) {
                android.widget.Toast.makeText(
                    this,
                    "You can review only after booking is completed.",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (shopId.isBlank()) {
                android.widget.Toast.makeText(
                    this,
                    "Missing shop info",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val reviewText = edtReview.text.toString()
            val selectedTags = mutableListOf<String>()

            // Get selected chips
            val checkedIds = chipGroup.checkedChipIds
            checkedIds.forEach { id ->
                val chip = findViewById<Chip>(id)
                selectedTags.add(chip.text.toString())
            }

            val user = FirebaseAuth.getInstance().currentUser
            val data = hashMapOf(
                "shopId" to shopId,
                "userId" to (user?.uid ?: ""),
                "userName" to (user?.email ?: "Customer"),
                "rating" to selectedRating.toFloat(),
                "comment" to reviewText,
                "tags" to selectedTags,
                "createdAt" to FieldValue.serverTimestamp()
            )

            firestore.collection("reviews")
                .add(data)
                .addOnSuccessListener {
                    if (bookingId.isNotBlank()) {
                        BookingRepository.updateBookingStatus(
                            bookingId = bookingId,
                            status = "COMPLETED",
                            onSuccess = {},
                            onError = {}
                        )
                    }
                    android.widget.Toast.makeText(
                        this,
                        "Review submitted",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                .addOnFailureListener { exception ->
                    android.widget.Toast.makeText(
                        this,
                        exception.message ?: "Failed to send review",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun checkReviewEligibility() {
        if (bookingId.isBlank()) {
            canReview = false
            return
        }

        firestore.collection("bookings")
            .document(bookingId)
            .get()
            .addOnSuccessListener { doc ->
                val status = doc.getString("status") ?: ""
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                val ownerId = doc.getString("customerId")
                canReview = status == "COMPLETED" && userId != null && userId == ownerId
            }
            .addOnFailureListener {
                canReview = false
            }
    }
    private fun setupReviewChips() {
        val selectorGroup = findViewById<ChipGroup>(R.id.chipGroupReviewTags)

        // Default chip inside review field
        addReviewChip("Overall good")

        for (i in 0 until selectorGroup.childCount) {
            val selectorChip = selectorGroup.getChildAt(i) as Chip

            selectorChip.setOnClickListener {
                addReviewChip(selectorChip.text.toString())
            }
        }
    }

    private fun addReviewChip(text: String) {
        val selectedGroup = findViewById<ChipGroup>(R.id.chipGroupSelectedReview)

        // Prevent duplicates
        for (i in 0 until selectedGroup.childCount) {
            if ((selectedGroup.getChildAt(i) as Chip).text == text) return
        }

        val chip = Chip(this).apply {
            this.text = text
            isCloseIconVisible = true
            setChipBackgroundColorResource(R.color.chip_background_color_review)
            setTextColor(
                ContextCompat.getColor(
                    this@RatingReviewActivity,
                    R.color.chip_text_color_review
                )
            )
            setOnCloseIconClickListener {
                selectedGroup.removeView(this)
            }
        }

        selectedGroup.addView(chip)
    }


}
