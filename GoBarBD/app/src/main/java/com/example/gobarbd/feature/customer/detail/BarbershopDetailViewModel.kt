package com.example.gobarbd.feature.customer.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gobarbd.core.data.model.Barber
import com.example.gobarbd.core.data.model.Barbershop
import com.example.gobarbd.core.data.model.Review
import com.example.gobarbd.core.data.model.Service
import com.example.gobarbd.core.data.repository.BarbershopRepository
import com.example.gobarbd.core.data.repository.BookingRepository

class BarbershopDetailViewModel : ViewModel() {

    private val shopRepository = BarbershopRepository
    private val bookingRepository = BookingRepository

    private val _shop = MutableLiveData<Barbershop>()
    val shop: LiveData<Barbershop> = _shop

    private val _services = MutableLiveData<List<Service>>()
    val services: LiveData<List<Service>> = _services

    private val _barbers = MutableLiveData<List<Barber>>()
    val barbers: LiveData<List<Barber>> = _barbers

    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> = _reviews

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun load(shopId: String) {
        shopRepository.fetchShopById(
            shopId = shopId,
            onSuccess = { _shop.postValue(it) },
            onError = { _error.postValue(it.message) }
        )
        bookingRepository.fetchServices(
            shopId = shopId,
            onSuccess = { _services.postValue(it) },
            onError = { _error.postValue(it.message) }
        )
        bookingRepository.fetchBarbers(
            shopId = shopId,
            onSuccess = { _barbers.postValue(it) },
            onError = { _error.postValue(it.message) }
        )
        shopRepository.fetchReviews(
            shopId = shopId,
            onSuccess = { _reviews.postValue(it) },
            onError = { _error.postValue(it.message) }
        )
    }
}
