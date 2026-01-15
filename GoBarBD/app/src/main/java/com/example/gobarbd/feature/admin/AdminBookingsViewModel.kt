package com.example.gobarbd.feature.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gobarbd.core.data.model.Booking
import com.example.gobarbd.core.data.repository.BookingRepository
import com.google.firebase.firestore.ListenerRegistration

class AdminBookingsViewModel : ViewModel() {

    private var listener: ListenerRegistration? = null

    private val _bookings = MutableLiveData<List<Booking>>()
    val bookings: LiveData<List<Booking>> = _bookings

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var allBookings: List<Booking> = emptyList()

    fun load() {
        listener?.remove()
        listener = BookingRepository.listenAllBookings(
            onUpdate = { list ->
                allBookings = list
                _bookings.postValue(list)
            },
            onError = { _error.postValue(it.message) }
        )
    }

    fun filterBy(status: String) {
        when (status) {
            "ALL" -> _bookings.postValue(allBookings)
            "ACTIVE" -> _bookings.postValue(
                allBookings.filter {
                    it.status == "ACTIVE" || it.status == "WAITING" || it.status == "ON_PROCESS"
                }
            )
            else -> _bookings.postValue(allBookings.filter { it.status == status })
        }
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}
