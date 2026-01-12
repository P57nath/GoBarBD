package com.example.gobarbd.feature.barber

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class BarberBookingsFragment : Fragment() {

    private lateinit var viewModel: BarberBookingsViewModel
    private lateinit var adapter: BarberBookingsAdapter
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_barber_bookings, container, false)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerBarberBookings)
        val progress = view.findViewById<ProgressBar>(R.id.progressBarberBookings)
        val empty = view.findViewById<TextView>(R.id.txtBarberBookingsEmpty)

        adapter = BarberBookingsAdapter(
            mutableListOf(),
            onStatusClick = { booking, status ->
                viewModel.updateStatus(booking.id, status)
            },
            onRescheduleClick = { booking ->
                openReschedulePicker(booking)
            },
            onNoteClick = { booking ->
                openNoteDialog(booking)
            },
            onNoShowClick = { booking ->
                viewModel.updateStatus(booking.id, "NO_SHOW")
            }
        )
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        viewModel = ViewModelProvider(requireActivity())[BarberBookingsViewModel::class.java]
        viewModel.bookings.observe(viewLifecycleOwner) { bookings ->
            adapter.updateData(bookings)
            progress.visibility = View.GONE
            empty.visibility = if (bookings.isEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.error.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        progress.visibility = View.VISIBLE
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            progress.visibility = View.GONE
            empty.visibility = View.VISIBLE
            Toast.makeText(requireContext(), "Please login", Toast.LENGTH_SHORT).show()
        } else {
            firestore.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    val barberId = doc.getString("barberId") ?: user.uid
                    viewModel.load(barberId)
                }
                .addOnFailureListener {
                    viewModel.load(user.uid)
                }
        }

        return view
    }

    private fun openReschedulePicker(booking: com.example.gobarbd.core.data.model.Booking) {
        val calendar = Calendar.getInstance()
        val startMillis = if (booking.startTimeMillis > 0L) booking.startTimeMillis else System.currentTimeMillis()
        calendar.timeInMillis = startMillis
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                openTimePicker(booking, calendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun openTimePicker(
        booking: com.example.gobarbd.core.data.model.Booking,
        calendar: Calendar
    ) {
        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val newStart = calendar.timeInMillis
                val duration = (booking.endTimeMillis - booking.startTimeMillis)
                    .takeIf { it > 0L } ?: 30 * 60 * 1000L
                val newEnd = newStart + duration
                viewModel.updateSchedule(booking.id, newStart, newEnd)
                Toast.makeText(requireContext(), "Booking rescheduled", Toast.LENGTH_SHORT).show()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun openNoteDialog(booking: com.example.gobarbd.core.data.model.Booking) {
        val input = EditText(requireContext()).apply {
            hint = "Add note for this booking"
        }
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Booking note")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val note = input.text.toString().trim()
                if (note.isNotBlank()) {
                    viewModel.updateNote(booking.id, note)
                    Toast.makeText(requireContext(), "Note saved", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
