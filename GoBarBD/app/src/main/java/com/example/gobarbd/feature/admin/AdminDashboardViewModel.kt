package com.example.gobarbd.feature.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gobarbd.core.data.model.Booking
import com.example.gobarbd.core.data.model.Barbershop
import com.example.gobarbd.core.data.repository.BarbershopRepository
import com.example.gobarbd.core.data.repository.BookingRepository
import com.google.firebase.firestore.ListenerRegistration
import java.util.Calendar

class AdminDashboardViewModel : ViewModel() {

    private var shopsListener: ListenerRegistration? = null
    private var bookingsListener: ListenerRegistration? = null

    private val _summary = MutableLiveData<AdminSummary>()
    val summary: LiveData<AdminSummary> = _summary

    private val _recentBookings = MutableLiveData<List<Booking>>()
    val recentBookings: LiveData<List<Booking>> = _recentBookings

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun load() {
        shopsListener?.remove()
        bookingsListener?.remove()

        shopsListener = BarbershopRepository.listenAllShops(
            onUpdate = { shops ->
                updateSummary(shops, null)
            },
            onError = { _error.postValue(it.message) }
        )

        bookingsListener = BookingRepository.listenAllBookings(
            onUpdate = { bookings ->
                val sorted = bookings.sortedByDescending { it.startTimeMillis }
                _recentBookings.postValue(sorted.take(6))
                updateSummary(null, bookings)
            },
            onError = { _error.postValue(it.message) }
        )
    }

    private fun updateSummary(shops: List<Barbershop>?, bookings: List<Booking>?) {
        val current = _summary.value ?: AdminSummary()
        val totalShops = shops?.size ?: current.totalShops
        val allBookings = bookings ?: current.bookings

        val totalBookings = allBookings.size
        val activeBookings = allBookings.count {
            it.status == "ACTIVE" || it.status == "WAITING" || it.status == "ON_PROCESS"
        }
        val todayBookings = allBookings.count { isToday(it.startTimeMillis) }

        _summary.postValue(
            AdminSummary(
                totalShops = totalShops,
                totalBookings = totalBookings,
                activeBookings = activeBookings,
                todayBookings = todayBookings,
                bookings = allBookings
            )
        )
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
        shopsListener?.remove()
        bookingsListener?.remove()
        super.onCleared()
    }
}

data class AdminSummary(
    val totalShops: Int = 0,
    val totalBookings: Int = 0,
    val activeBookings: Int = 0,
    val todayBookings: Int = 0,
    val bookings: List<Booking> = emptyList()
)
