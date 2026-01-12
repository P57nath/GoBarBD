package com.example.gobarbd.feature.customer.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gobarbd.core.data.model.Barbershop
import com.example.gobarbd.core.data.repository.BarbershopRepository

class AllBarbershopsViewModel : ViewModel() {

    private val repository = BarbershopRepository

    private val _shops = MutableLiveData<List<Barbershop>>()
    val shops: LiveData<List<Barbershop>> = _shops

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadShops() {
        repository.fetchAllShops(
            onSuccess = { list ->
                _shops.postValue(list)
            },
            onError = { exception ->
                _error.postValue(exception.message)
                _shops.postValue(repository.getSeedData())
            }
        )
    }
}
