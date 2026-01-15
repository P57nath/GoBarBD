package com.example.gobarbd.feature.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gobarbd.core.data.model.Barbershop
import com.example.gobarbd.core.data.repository.BarbershopRepository
import com.google.firebase.firestore.ListenerRegistration

class AdminShopsViewModel : ViewModel() {

    private var listener: ListenerRegistration? = null

    private val _shops = MutableLiveData<List<Barbershop>>()
    val shops: LiveData<List<Barbershop>> = _shops

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun load() {
        listener?.remove()
        listener = BarbershopRepository.listenAllShops(
            onUpdate = { _shops.postValue(it) },
            onError = { _error.postValue(it.message) }
        )
    }

    fun saveShop(
        shopId: String?,
        name: String,
        address: String,
        description: String,
        isOpen: Boolean
    ) {
        BarbershopRepository.saveShop(
            shopId = shopId,
            name = name,
            address = address,
            description = description,
            isOpen = isOpen,
            onSuccess = {},
            onError = { _error.postValue(it.message) }
        )
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}
