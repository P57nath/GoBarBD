package com.example.gobarbd.feature.customer.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gobarbd.core.data.model.ChatMessage
import com.example.gobarbd.core.data.repository.ChatRepository
import com.google.firebase.firestore.ListenerRegistration

class ChatRoomViewModel : ViewModel() {

    private val repository = ChatRepository
    private var listener: ListenerRegistration? = null

    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun load(chatId: String) {
        listener?.remove()
        listener = repository.listenMessages(
            chatId = chatId,
            onUpdate = { _messages.postValue(it) },
            onError = { _error.postValue(it.message) }
        )
    }

    fun send(chatId: String, senderId: String, message: String) {
        repository.sendMessage(
            chatId = chatId,
            senderId = senderId,
            message = message,
            onSuccess = {},
            onError = { _error.postValue(it.message) }
        )
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}
