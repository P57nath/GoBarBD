package com.example.gobarbd.feature.customer.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gobarbd.core.data.model.ChatThread
import com.example.gobarbd.core.data.repository.ChatRepository
import com.google.firebase.firestore.ListenerRegistration

class ChatListViewModel : ViewModel() {

    private val repository = ChatRepository
    private var activeListener: ListenerRegistration? = null
    private var finishedListener: ListenerRegistration? = null

    private val _activeThreads = MutableLiveData<List<ChatThread>>()
    val activeThreads: LiveData<List<ChatThread>> = _activeThreads

    private val _finishedThreads = MutableLiveData<List<ChatThread>>()
    val finishedThreads: LiveData<List<ChatThread>> = _finishedThreads

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun load(userId: String) {
        activeListener?.remove()
        finishedListener?.remove()
        activeListener = repository.listenChats(
            userId = userId,
            activeOnly = true,
            onUpdate = { _activeThreads.postValue(it) },
            onError = { _error.postValue(it.message) }
        )
        finishedListener = repository.listenChats(
            userId = userId,
            activeOnly = false,
            onUpdate = { _finishedThreads.postValue(it) },
            onError = { _error.postValue(it.message) }
        )
    }

    override fun onCleared() {
        activeListener?.remove()
        finishedListener?.remove()
        super.onCleared()
    }
}
