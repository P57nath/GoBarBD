package com.example.gobarbd.feature.customer.booking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gobarbd.core.data.model.Booking
import com.example.gobarbd.core.data.repository.BookingRepository
import com.google.firebase.firestore.ListenerRegistration

class BookingListViewModel : ViewModel() {

    private val repository = BookingRepository
    private var listener: ListenerRegistration? = null

    private val _active = MutableLiveData<List<Booking>>()
    val active: LiveData<List<Booking>> = _active

    private val _history = MutableLiveData<List<Booking>>()
    val history: LiveData<List<Booking>> = _history

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _statusUpdated = MutableLiveData<Boolean>()
    val statusUpdated: LiveData<Boolean> = _statusUpdated

    fun load(customerId: String) {
        listener?.remove()
        listener = repository.listenCustomerBookings(
            customerId = customerId,
            onUpdate = { list ->
                _active.postValue(
                    list.filter { it.status == "ACTIVE" || it.status == "WAITING" || it.status == "ON_PROCESS" }
                )
                _history.postValue(
                    list.filter { it.status == "COMPLETED" || it.status == "CANCELLED" || it.status == "NO_SHOW" }
                )
            },
            onError = { _error.postValue(it.message) }
        )
    }

    fun updateStatus(bookingId: String, status: String) {
        repository.updateBookingStatus(
            bookingId = bookingId,
            status = status,
            onSuccess = { _statusUpdated.postValue(true) },
            onError = { _error.postValue(it.message) }
        )
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}
