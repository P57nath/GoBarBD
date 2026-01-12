package com.example.gobarbd.feature.barber

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gobarbd.core.data.model.Booking
import com.example.gobarbd.core.data.repository.BookingRepository
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BarberScheduleViewModel : ViewModel() {

    private val repository = BookingRepository
    private var listener: ListenerRegistration? = null
    private var currentSettings = AvailabilitySettings(
        startMinutes = 9 * 60,
        endMinutes = 20 * 60,
        slotMinutes = 30
    )
    private var lastBookings: List<Booking> = emptyList()

    private val _slots = MutableLiveData<List<ScheduleSlot>>()
    val slots: LiveData<List<ScheduleSlot>> = _slots

    private val _summary = MutableLiveData<ScheduleSummary>()
    val summary: LiveData<ScheduleSummary> = _summary

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun load(barberId: String) {
        listener?.remove()
        listener = repository.listenBarberBookings(
            barberId = barberId,
            onUpdate = { list ->
                lastBookings = list
                val slots = buildSlotsForToday(list, currentSettings)
                _slots.postValue(slots)
                _summary.postValue(buildSummary(slots))
            },
            onError = { _error.postValue(it.message) }
        )
    }

    fun applySettings(settings: AvailabilitySettings) {
        currentSettings = settings
        val slots = buildSlotsForToday(lastBookings, currentSettings)
        _slots.postValue(slots)
        _summary.postValue(buildSummary(slots))
    }

    private fun buildSlotsForToday(
        bookings: List<Booking>,
        settings: AvailabilitySettings
    ): List<ScheduleSlot> {
        val todayBookings = bookings.filter { isToday(it.startTimeMillis) }
        val bookingMap = todayBookings.associateBy { minutesOfDay(it.startTimeMillis) }
        val slots = mutableListOf<ScheduleSlot>()
        var minutes = settings.startMinutes
        val endMinutes = settings.endMinutes
        val slotMinutes = settings.slotMinutes.coerceAtLeast(15)
        while (minutes < endMinutes) {
            val booking = bookingMap[minutes]
            val label = timeLabel(minutes)
            if (booking == null) {
                slots.add(
                    ScheduleSlot(
                        timeLabel = label,
                        statusLabel = "Available",
                        customerLabel = "Walk-in",
                        isBooked = false
                    )
                )
            } else {
                val statusLabel = when (booking.status) {
                    "COMPLETED" -> "Completed"
                    "CANCELLED" -> "Available"
                    else -> "Booked"
                }
                val isBooked = booking.status == "ACTIVE" ||
                    booking.status == "WAITING" ||
                    booking.status == "ON_PROCESS" ||
                    booking.status == "COMPLETED"
                val customerLabel = if (booking.customerId.isNotBlank()) {
                    "Customer: ${booking.customerId.take(6)}"
                } else {
                    "Customer: Walk-in"
                }
                slots.add(
                    ScheduleSlot(
                        timeLabel = label,
                        statusLabel = statusLabel,
                        customerLabel = customerLabel,
                        isBooked = isBooked
                    )
                )
            }
            minutes += slotMinutes
        }
        return slots
    }

    private fun buildSummary(slots: List<ScheduleSlot>): ScheduleSummary {
        val booked = slots.count { it.isBooked }
        val available = slots.size - booked
        return ScheduleSummary(available = available, booked = booked)
    }

    private fun minutesOfDay(timeMillis: Long): Int {
        if (timeMillis <= 0L) {
            return -1
        }
        val cal = Calendar.getInstance().apply { timeInMillis = timeMillis }
        return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
    }

    private fun timeLabel(minutes: Int): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, minutes / 60)
        cal.set(Calendar.MINUTE, minutes % 60)
        cal.set(Calendar.SECOND, 0)
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(cal.timeInMillis))
    }

    private fun isToday(timeMillis: Long): Boolean {
        if (timeMillis <= 0L) {
            return true
        }
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = timeMillis }
        return now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}

data class ScheduleSlot(
    val timeLabel: String,
    val statusLabel: String,
    val customerLabel: String,
    val isBooked: Boolean
)

data class AvailabilitySettings(
    val startMinutes: Int,
    val endMinutes: Int,
    val slotMinutes: Int
)

data class ScheduleSummary(
    val available: Int,
    val booked: Int
)
