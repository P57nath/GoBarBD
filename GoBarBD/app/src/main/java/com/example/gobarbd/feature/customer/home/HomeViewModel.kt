package com.example.gobarbd.feature.customer.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gobarbd.core.data.model.Barbershop
import com.example.gobarbd.core.data.repository.BarbershopRepository

class HomeViewModel : ViewModel() {

    private val repository = BarbershopRepository

    private val _nearest = MutableLiveData<List<Barbershop>>()
    val nearest: LiveData<List<Barbershop>> = _nearest

    private val _recommended = MutableLiveData<List<Barbershop>>()
    val recommended: LiveData<List<Barbershop>> = _recommended

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadShops() {
        repository.fetchAllShops(
            onSuccess = { shops ->
                _nearest.postValue(repository.getNearestFrom(shops))
                _recommended.postValue(repository.getRecommendedFrom(shops))
            },
            onError = { exception ->
                _error.postValue(exception.message)
                val seed = repository.getSeedData()
                _nearest.postValue(repository.getNearestFrom(seed))
                _recommended.postValue(repository.getRecommendedFrom(seed))
            }
        )
    }
}
