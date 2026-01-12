package com.example.gobarbd.feature.customer.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gobarbd.core.data.model.ChatThread
import com.example.gobarbd.core.data.repository.ChatRepository

class ChatListViewModel : ViewModel() {

    private val repository = ChatRepository

    private val _activeThreads = MutableLiveData<List<ChatThread>>()
    val activeThreads: LiveData<List<ChatThread>> = _activeThreads

    private val _finishedThreads = MutableLiveData<List<ChatThread>>()
    val finishedThreads: LiveData<List<ChatThread>> = _finishedThreads

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun load(userId: String) {
        repository.fetchChats(
            userId = userId,
            activeOnly = true,
            onSuccess = { _activeThreads.postValue(it) },
            onError = { _error.postValue(it.message) }
        )
        repository.fetchChats(
            userId = userId,
            activeOnly = false,
            onSuccess = { _finishedThreads.postValue(it) },
            onError = { _error.postValue(it.message) }
        )
    }
}
