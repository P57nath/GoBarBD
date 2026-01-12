package com.example.gobarbd.feature.barber

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gobarbd.core.data.model.Booking
import com.example.gobarbd.core.data.repository.BookingRepository
import com.google.firebase.firestore.ListenerRegistration

class BarberBookingsViewModel : ViewModel() {

    private val repository = BookingRepository
    private var listener: ListenerRegistration? = null

    private val _bookings = MutableLiveData<List<Booking>>()
    val bookings: LiveData<List<Booking>> = _bookings

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun load(barberId: String) {
        listener?.remove()
        listener = repository.listenBarberBookings(
            barberId = barberId,
            onUpdate = { list -> _bookings.postValue(list) },
            onError = { _error.postValue(it.message) }
        )
    }

    fun updateStatus(bookingId: String, status: String) {
        repository.updateBookingStatus(
            bookingId = bookingId,
            status = status,
            onSuccess = {},
            onError = { _error.postValue(it.message) }
        )
    }

    fun updateSchedule(bookingId: String, startTimeMillis: Long, endTimeMillis: Long) {
        repository.updateBookingSchedule(
            bookingId = bookingId,
            startTimeMillis = startTimeMillis,
            endTimeMillis = endTimeMillis,
            onSuccess = {},
            onError = { _error.postValue(it.message) }
        )
    }

    fun updateNote(bookingId: String, note: String) {
        repository.updateBookingNote(
            bookingId = bookingId,
            note = note,
            onSuccess = {},
            onError = { _error.postValue(it.message) }
        )
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}
