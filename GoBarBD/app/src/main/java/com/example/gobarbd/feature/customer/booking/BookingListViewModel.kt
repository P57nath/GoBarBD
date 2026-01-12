package com.example.gobarbd.feature.customer.booking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gobarbd.core.data.model.Booking
import com.example.gobarbd.core.data.repository.BookingRepository

class BookingListViewModel : ViewModel() {

    private val repository = BookingRepository

    private val _active = MutableLiveData<List<Booking>>()
    val active: LiveData<List<Booking>> = _active

    private val _history = MutableLiveData<List<Booking>>()
    val history: LiveData<List<Booking>> = _history

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun load(customerId: String) {
        repository.fetchCustomerBookings(
            customerId = customerId,
            onSuccess = { list ->
                _active.postValue(list.filter { it.status != "COMPLETED" })
                _history.postValue(list.filter { it.status == "COMPLETED" })
            },
            onError = { _error.postValue(it.message) }
        )
    }
}
