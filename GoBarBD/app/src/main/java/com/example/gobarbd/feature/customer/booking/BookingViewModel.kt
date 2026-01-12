package com.example.gobarbd.feature.customer.booking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gobarbd.core.data.model.Barber
import com.example.gobarbd.core.data.model.BookingRequest
import com.example.gobarbd.core.data.model.Service
import com.example.gobarbd.core.data.repository.BookingRepository

class BookingViewModel : ViewModel() {

    private val repository = BookingRepository

    private val _services = MutableLiveData<List<Service>>()
    val services: LiveData<List<Service>> = _services

    private val _barbers = MutableLiveData<List<Barber>>()
    val barbers: LiveData<List<Barber>> = _barbers

    private val _bookingSuccess = MutableLiveData<Boolean>()
    val bookingSuccess: LiveData<Boolean> = _bookingSuccess

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadServices(shopId: String) {
        repository.fetchServices(
            shopId = shopId,
            onSuccess = { list -> _services.postValue(list) },
            onError = { exception -> _error.postValue(exception.message) }
        )
    }

    fun loadBarbers(shopId: String) {
        repository.fetchBarbers(
            shopId = shopId,
            onSuccess = { list -> _barbers.postValue(list) },
            onError = { exception -> _error.postValue(exception.message) }
        )
    }

    fun createBooking(request: BookingRequest) {
        repository.createBooking(
            request = request,
            onSuccess = { _bookingSuccess.postValue(true) },
            onError = { exception -> _error.postValue(exception.message) }
        )
    }
}
