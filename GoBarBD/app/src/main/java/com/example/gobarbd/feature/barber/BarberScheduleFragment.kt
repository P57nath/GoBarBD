package com.example.gobarbd.feature.barber

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.AlertDialog
import android.app.TimePickerDialog
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BarberScheduleFragment : Fragment() {

    private lateinit var viewModel: BarberScheduleViewModel
    private lateinit var adapter: BarberScheduleAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private var currentSettings = AvailabilitySettings(
        startMinutes = 9 * 60,
        endMinutes = 20 * 60,
        slotMinutes = 30
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_barber_schedule, container, false)
        val txtName = view.findViewById<TextView>(R.id.txtScheduleName)
        val txtDate = view.findViewById<TextView>(R.id.txtScheduleDate)
        val txtWorking = view.findViewById<TextView>(R.id.txtWorkingHours)
        val btnEdit = view.findViewById<View>(R.id.btnEditAvailability)
        val txtAvailable = view.findViewById<TextView>(R.id.txtAvailableCount)
        val txtBooked = view.findViewById<TextView>(R.id.txtBookedCount)
        val progress = view.findViewById<ProgressBar>(R.id.progressBarberSchedule)
        val empty = view.findViewById<TextView>(R.id.txtBarberScheduleEmpty)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerBarberSchedule)

        adapter = BarberScheduleAdapter(mutableListOf())
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        txtDate.text = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(Date())

        viewModel = ViewModelProvider(requireActivity())[BarberScheduleViewModel::class.java]
        viewModel.summary.observe(viewLifecycleOwner) { summary ->
            txtAvailable.text = summary.available.toString()
            txtBooked.text = summary.booked.toString()
        }
        viewModel.slots.observe(viewLifecycleOwner) { slots ->
            adapter.updateData(slots)
            progress.visibility = View.GONE
            empty.visibility = if (slots.isEmpty()) View.VISIBLE else View.GONE
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
            txtName.text = "Barber"
            Toast.makeText(requireContext(), "Please login", Toast.LENGTH_SHORT).show()
        } else {
            txtName.text = user.displayName ?: "Barber"
            firestore.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    val barberId = doc.getString("barberId") ?: user.uid
                    val startMinutes = (doc.getLong("workingStartMinutes") ?: 540L).toInt()
                    val endMinutes = (doc.getLong("workingEndMinutes") ?: 1200L).toInt()
                    val slotMinutes = (doc.getLong("slotDurationMinutes") ?: 30L).toInt()
                    currentSettings = AvailabilitySettings(
                        startMinutes = startMinutes,
                        endMinutes = endMinutes,
                        slotMinutes = slotMinutes
                    )
                    viewModel.applySettings(currentSettings)
                    txtWorking.text = formatWorkingHours(currentSettings)
                    viewModel.load(barberId)
                }
                .addOnFailureListener {
                    viewModel.load(user.uid)
                }
        }

        btnEdit.setOnClickListener {
            if (user == null) {
                Toast.makeText(requireContext(), "Please login", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            openStartTimePicker(user.uid, txtWorking)
        }

        return view
    }

    private fun openStartTimePicker(userId: String, txtWorking: TextView) {
        val hour = currentSettings.startMinutes / 60
        val minute = currentSettings.startMinutes % 60
        TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            val startMinutes = selectedHour * 60 + selectedMinute
            openEndTimePicker(userId, txtWorking, startMinutes)
        }, hour, minute, false).show()
    }

    private fun openEndTimePicker(userId: String, txtWorking: TextView, startMinutes: Int) {
        val hour = currentSettings.endMinutes / 60
        val minute = currentSettings.endMinutes % 60
        TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            val endMinutes = selectedHour * 60 + selectedMinute
            if (endMinutes <= startMinutes) {
                Toast.makeText(requireContext(), "End time must be after start time", Toast.LENGTH_SHORT).show()
                return@TimePickerDialog
            }
            openSlotDurationPicker(userId, txtWorking, startMinutes, endMinutes)
        }, hour, minute, false).show()
    }

    private fun openSlotDurationPicker(
        userId: String,
        txtWorking: TextView,
        startMinutes: Int,
        endMinutes: Int
    ) {
        val options = arrayOf("15", "30", "45", "60")
        AlertDialog.Builder(requireContext())
            .setTitle("Slot duration (minutes)")
            .setItems(options) { _, which ->
                val slotMinutes = options[which].toInt()
                val newSettings = AvailabilitySettings(
                    startMinutes = startMinutes,
                    endMinutes = endMinutes,
                    slotMinutes = slotMinutes
                )
                saveAvailability(userId, newSettings, txtWorking)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveAvailability(
        userId: String,
        settings: AvailabilitySettings,
        txtWorking: TextView
    ) {
        firestore.collection("users").document(userId)
            .update(
                mapOf(
                    "workingStartMinutes" to settings.startMinutes,
                    "workingEndMinutes" to settings.endMinutes,
                    "slotDurationMinutes" to settings.slotMinutes
                )
            )
            .addOnSuccessListener {
                currentSettings = settings
                viewModel.applySettings(settings)
                txtWorking.text = formatWorkingHours(settings)
                Toast.makeText(requireContext(), "Availability updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.message ?: "Update failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun formatWorkingHours(settings: AvailabilitySettings): String {
        return "Working: ${formatTime(settings.startMinutes)} - " +
            "${formatTime(settings.endMinutes)} - ${settings.slotMinutes} min slots"
    }

    private fun formatTime(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return String.format(Locale.getDefault(), "%02d:%02d", hours, mins)
    }
}
