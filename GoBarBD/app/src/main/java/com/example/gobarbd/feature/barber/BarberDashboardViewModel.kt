package com.example.gobarbd.feature.barber

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gobarbd.core.data.model.Booking
import com.example.gobarbd.core.data.repository.BookingRepository
import com.google.firebase.firestore.ListenerRegistration
import java.util.Calendar

class BarberDashboardViewModel : ViewModel() {

    private val repository = BookingRepository
    private var listener: ListenerRegistration? = null

    private val _todayQueue = MutableLiveData<List<Booking>>()
    val todayQueue: LiveData<List<Booking>> = _todayQueue

    private val _summary = MutableLiveData<BarberSummary>()
    val summary: LiveData<BarberSummary> = _summary

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun load(barberId: String) {
        listener?.remove()
        listener = repository.listenBarberBookings(
            barberId = barberId,
            onUpdate = { list ->
                val todays = list.filter { isToday(it.startTimeMillis) }
                _todayQueue.postValue(todays)
                _summary.postValue(buildSummary(list, todays))
            },
            onError = { _error.postValue(it.message) }
        )
    }

    private fun buildSummary(allBookings: List<Booking>, todays: List<Booking>): BarberSummary {
        val total = todays.size
        val active = todays.count { it.status == "ACTIVE" || it.status == "WAITING" || it.status == "ON_PROCESS" }
        val completed = todays.count { it.status == "COMPLETED" }
        val completedWeek = allBookings.count { it.status == "COMPLETED" && isThisWeek(it.startTimeMillis) }
        val earningsWeek = allBookings
            .filter { it.status == "COMPLETED" && isThisWeek(it.startTimeMillis) }
            .sumOf { it.totalPrice }
        return BarberSummary(
            total = total,
            active = active,
            completed = completed,
            completedWeek = completedWeek,
            earningsWeek = earningsWeek
        )
    }

    private fun isToday(startTimeMillis: Long): Boolean {
        if (startTimeMillis <= 0L) {
            return true
        }
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = startTimeMillis }
        return now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
    }

    private fun isThisWeek(startTimeMillis: Long): Boolean {
        if (startTimeMillis <= 0L) {
            return true
        }
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = startTimeMillis }
        return now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
            now.get(Calendar.WEEK_OF_YEAR) == target.get(Calendar.WEEK_OF_YEAR)
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}

data class BarberSummary(
    val total: Int,
    val active: Int,
    val completed: Int,
    val completedWeek: Int,
    val earningsWeek: Double
)
