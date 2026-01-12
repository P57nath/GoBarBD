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
    private var typingListener: ListenerRegistration? = null

    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isTyping = MutableLiveData<Boolean>()
    val isTyping: LiveData<Boolean> = _isTyping

    fun load(chatId: String, currentUserId: String) {
        listener?.remove()
        typingListener?.remove()
        listener = repository.listenMessages(
            chatId = chatId,
            onUpdate = { _messages.postValue(it) },
            onError = { _error.postValue(it.message) }
        )
        typingListener = repository.listenTyping(
            chatId = chatId,
            onUpdate = { typingUserId, isTyping, typingAt ->
                val isOtherTyping = isTyping &&
                    !typingUserId.isNullOrBlank() &&
                    typingUserId != currentUserId &&
                    System.currentTimeMillis() - typingAt < 5000
                _isTyping.postValue(isOtherTyping)
            },
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

    fun updateTyping(chatId: String, senderId: String, isTyping: Boolean) {
        repository.updateTyping(chatId, senderId, isTyping)
    }

    override fun onCleared() {
        listener?.remove()
        typingListener?.remove()
        super.onCleared()
    }
}
