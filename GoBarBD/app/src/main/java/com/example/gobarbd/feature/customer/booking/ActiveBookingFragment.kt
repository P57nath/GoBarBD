package com.example.gobarbd.feature.customer.booking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.gobarbd.R
import com.example.gobarbd.app.MainActivity
import com.example.gobarbd.feature.customer.review.RatingReviewActivity

class ActiveBookingFragment : Fragment() {

    private var currentStatus = BookingStatus.NOBOOKED

    enum class BookingStatus {
        NOBOOKED,BOOKED, WAITING, ON_PROCESS, FINISHED
    }

    private fun nextStatus() {
        currentStatus = when (currentStatus) {
            BookingStatus.NOBOOKED -> BookingStatus.BOOKED
            BookingStatus.BOOKED -> BookingStatus.WAITING
            BookingStatus.WAITING -> BookingStatus.ON_PROCESS
            BookingStatus.ON_PROCESS -> BookingStatus.FINISHED
            BookingStatus.FINISHED -> BookingStatus.NOBOOKED
            else -> BookingStatus.NOBOOKED
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_active_booking, container, false)

        setupProgressIndicator(view)
        // DEV ONLY: tap progress area to cycle states
        val bookingProcessLayout = view.findViewById<LinearLayout>(R.id.progressIndicator)
        bookingProcessLayout.setOnClickListener {
            nextStatus()
            Toast.makeText(
                requireContext(),
                "Status: $currentStatus",
                Toast.LENGTH_SHORT
            ).show()
            setupProgressIndicator(view)
        }
        setupActionButtons(view)
        setupCancelButton(view)

        return view
    }

    private fun setupProgressIndicator(view: View) {
        val iconBooked = view.findViewById<ImageView>(R.id.iconBooked)
        val iconWaiting = view.findViewById<ImageView>(R.id.iconWaiting)
        val iconProcess = view.findViewById<ImageView>(R.id.iconProcess)
        val iconFinished = view.findViewById<ImageView>(R.id.iconFinished)

        val lblBooked = view.findViewById<TextView>(R.id.lblBooked)
        val lblWaiting = view.findViewById<TextView>(R.id.lblWaiting)
        val lblProcess = view.findViewById<TextView>(R.id.lblProcess)
        val lblFinished = view.findViewById<TextView>(R.id.lblFinished)

        val progressLine1 = view.findViewById<View>(R.id.progressLine1)
        val progressLine2 = view.findViewById<View>(R.id.progressLine2)
        val progressLine3 = view.findViewById<View>(R.id.progressLine3)

        val bookingProcessLayout = view.findViewById<LinearLayout>(R.id.bookingProcessLayout)
        bookingProcessLayout.visibility = View.VISIBLE


        val timeEstimationLayout = view.findViewById<LinearLayout>(R.id.layoutTimeEstimation)
        timeEstimationLayout.visibility = View.GONE

        val onProcessLayout = view.findViewById<LinearLayout>(R.id.layoutOnProcess)
        onProcessLayout.visibility = View.GONE

        val finishedLayout = view.findViewById<LinearLayout>(R.id.finishedLayout)
        finishedLayout.visibility = View.GONE

        val noBookedLayout = view.findViewById<LinearLayout>(R.id.noBookedLayout)
        noBookedLayout.visibility = View.GONE






        // Reset all
        updateStepStatus(iconBooked, lblBooked, false)
        updateStepStatus(iconWaiting, lblWaiting, false)
        updateStepStatus(iconProcess, lblProcess, false)
        updateStepStatus(iconFinished, lblFinished, false)
        updateLineStatus(progressLine1, false)
        updateLineStatus(progressLine2, false)
        updateLineStatus(progressLine3, false)

        // Update based on current status
        // Test WAITING status
        // currentStatus = BookingStatus.WAITING

        // Test ON_PROCESS status
        // currentStatus = BookingStatus.ON_PROCESS

        // Test FINISHED status
        // currentStatus = BookingStatus.FINISHED
        when (currentStatus) {
            BookingStatus.NOBOOKED -> {
                updateStepStatus(iconBooked, lblBooked, false)

                bookingProcessLayout.visibility = View.GONE
                finishedLayout.visibility = View.GONE
                noBookedLayout.visibility = View.VISIBLE

                val btnBookNow = view.findViewById<Button>(R.id.btnBookNow)
                btnBookNow.setOnClickListener {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                }
            }
            BookingStatus.BOOKED -> {
                updateStepStatus(iconBooked, lblBooked, true)
            }
            BookingStatus.WAITING -> {
                updateStepStatus(iconBooked, lblBooked, true)
                updateStepStatus(iconWaiting, lblWaiting, true)
                updateLineStatus(progressLine1, true)

                timeEstimationLayout.visibility = View.VISIBLE
            }
            BookingStatus.ON_PROCESS -> {
                updateStepStatus(iconBooked, lblBooked, true)
                updateStepStatus(iconWaiting, lblWaiting, true)
                updateStepStatus(iconProcess, lblProcess, true)
                updateLineStatus(progressLine1, true)
                updateLineStatus(progressLine2, true)

                onProcessLayout.visibility = View.VISIBLE
            }
            BookingStatus.FINISHED -> {
                updateStepStatus(iconBooked, lblBooked, true)
                updateStepStatus(iconWaiting, lblWaiting, true)
                updateStepStatus(iconProcess, lblProcess, true)
                updateStepStatus(iconFinished, lblFinished, true)
                updateLineStatus(progressLine1, true)
                updateLineStatus(progressLine2, true)
                updateLineStatus(progressLine3, true)

                bookingProcessLayout.visibility = View.GONE

                finishedLayout.visibility = View.VISIBLE
                val btnRatingReview = view.findViewById<Button>(R.id.btnRatingReview)
                btnRatingReview.setOnClickListener {
                    val intent = Intent(requireContext(), RatingReviewActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun updateStepStatus(icon: ImageView, label: TextView, isActive: Boolean) {
        if (isActive) {
            icon.setBackgroundResource(R.drawable.progress_circle_active)
            icon.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.white))
            label.setBackgroundResource(R.drawable.badge_active)
        } else {
            icon.setBackgroundResource(R.drawable.progress_circle_inactive)
            icon.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            label.setBackgroundResource(R.drawable.badge_inactive)
        }
    }

    private fun updateLineStatus(line: View, isActive: Boolean) {
        line.setBackgroundColor(
            if (isActive) {
                ContextCompat.getColor(requireContext(), R.color.orange_primary)
            } else {
                ContextCompat.getColor(requireContext(), android.R.color.darker_gray)
            }
        )
    }

    private fun setupActionButtons(view: View) {
        val btnMaps = view.findViewById<LinearLayout>(R.id.btnMaps)
        val btnChat = view.findViewById<LinearLayout>(R.id.btnChat)

        btnMaps.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:0,0?q=X+Men+Barber")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(mapIntent)
            }
        }

        btnChat.setOnClickListener {
            android.widget.Toast.makeText(context, "Opening Chat...", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCancelButton(view: View) {
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        btnCancel.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel this booking?")
                .setPositiveButton("Yes") { dialog, _ ->
                    android.widget.Toast.makeText(context, "Booking cancelled", android.widget.Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }
}
