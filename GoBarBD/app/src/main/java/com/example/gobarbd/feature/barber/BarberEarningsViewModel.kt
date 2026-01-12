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

class BarberEarningsViewModel : ViewModel() {

    private val repository = BookingRepository
    private var listener: ListenerRegistration? = null

    private val _weekTotal = MutableLiveData<Double>()
    val weekTotal: LiveData<Double> = _weekTotal

    private val _monthTotal = MutableLiveData<Double>()
    val monthTotal: LiveData<Double> = _monthTotal

    private val _weeklyBreakdown = MutableLiveData<List<EarningsPeriod>>()
    val weeklyBreakdown: LiveData<List<EarningsPeriod>> = _weeklyBreakdown

    private val _monthlyBreakdown = MutableLiveData<List<EarningsPeriod>>()
    val monthlyBreakdown: LiveData<List<EarningsPeriod>> = _monthlyBreakdown

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun load(barberId: String) {
        listener?.remove()
        listener = repository.listenBarberBookings(
            barberId = barberId,
            onUpdate = { list -> buildEarnings(list) },
            onError = { _error.postValue(it.message) }
        )
    }

    private fun buildEarnings(bookings: List<Booking>) {
        val completed = bookings.filter { it.status == "COMPLETED" }
        val weekMap = linkedMapOf<String, EarningsAggregate>()
        val monthMap = linkedMapOf<String, EarningsAggregate>()
        val weekLabelFormat = SimpleDateFormat("'Week' w, yyyy", Locale.getDefault())
        val monthLabelFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())

        for (booking in completed) {
            val time = if (booking.startTimeMillis > 0L) booking.startTimeMillis else System.currentTimeMillis()
            val cal = Calendar.getInstance().apply { timeInMillis = time }
            val weekKey = "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.WEEK_OF_YEAR)}"
            val monthKey = "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}"
            val weekLabel = weekLabelFormat.format(Date(time))
            val monthLabel = monthLabelFormat.format(Date(time))
            val weekAgg = weekMap.getOrPut(weekKey) {
                EarningsAggregate(weekLabel, time, 0.0, 0)
            }
            weekAgg.total += booking.totalPrice
            weekAgg.count += 1
            val monthAgg = monthMap.getOrPut(monthKey) {
                EarningsAggregate(monthLabel, time, 0.0, 0)
            }
            monthAgg.total += booking.totalPrice
            monthAgg.count += 1
        }

        val weekly = weekMap.values.sortedByDescending { it.startMillis }.map {
            EarningsPeriod(it.label, it.total, it.count)
        }
        val monthly = monthMap.values.sortedByDescending { it.startMillis }.map {
            EarningsPeriod(it.label, it.total, it.count)
        }

        _weeklyBreakdown.postValue(weekly)
        _monthlyBreakdown.postValue(monthly)

        val now = Calendar.getInstance()
        val currentWeekKey = "${now.get(Calendar.YEAR)}-${now.get(Calendar.WEEK_OF_YEAR)}"
        val currentMonthKey = "${now.get(Calendar.YEAR)}-${now.get(Calendar.MONTH)}"
        _weekTotal.postValue(weekMap[currentWeekKey]?.total ?: 0.0)
        _monthTotal.postValue(monthMap[currentMonthKey]?.total ?: 0.0)
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}

data class EarningsPeriod(
    val label: String,
    val total: Double,
    val count: Int
)

private data class EarningsAggregate(
    val label: String,
    val startMillis: Long,
    var total: Double,
    var count: Int
)
