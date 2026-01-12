package com.example.gobarbd.core.data.repository

import com.example.gobarbd.core.data.model.ChatMessage
import com.example.gobarbd.core.data.model.ChatThread
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

object ChatRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun fetchChats(
        userId: String,
        activeOnly: Boolean,
        onSuccess: (List<ChatThread>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        firestore.collection("chats")
            .whereArrayContains("participants", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val threads = snapshot.documents.map { doc ->
                    ChatThread(
                        id = doc.id,
                        shopName = doc.getString("shopName") ?: "Barbershop",
                        lastMessage = doc.getString("lastMessage") ?: "",
                        timestamp = doc.getLong("updatedAt") ?: 0L,
                        isActive = doc.getBoolean("isActive") ?: true
                    )
                }.filter { it.isActive == activeOnly }
                onSuccess(if (threads.isEmpty()) getSeedThreads(activeOnly) else threads)
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }

    fun listenChats(
        userId: String,
        activeOnly: Boolean,
        onUpdate: (List<ChatThread>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return firestore.collection("chats")
            .whereArrayContains("participants", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }
                val threads = snapshot?.documents?.map { doc ->
                    ChatThread(
                        id = doc.id,
                        shopName = doc.getString("shopName") ?: "Barbershop",
                        lastMessage = doc.getString("lastMessage") ?: "",
                        timestamp = doc.getLong("updatedAt") ?: 0L,
                        isActive = doc.getBoolean("isActive") ?: true
                    )
                }.orEmpty().filter { it.isActive == activeOnly }
                onUpdate(if (threads.isEmpty()) getSeedThreads(activeOnly) else threads)
            }
    }

    fun listenChatsForBarber(
        barberId: String,
        activeOnly: Boolean,
        onUpdate: (List<ChatThread>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return firestore.collection("chats")
            .whereEqualTo("barberId", barberId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }
                val threads = snapshot?.documents?.map { doc ->
                    ChatThread(
                        id = doc.id,
                        shopName = doc.getString("shopName") ?: "Barbershop",
                        lastMessage = doc.getString("lastMessage") ?: "",
                        timestamp = doc.getLong("updatedAt") ?: 0L,
                        isActive = doc.getBoolean("isActive") ?: true
                    )
                }.orEmpty().filter { it.isActive == activeOnly }
                onUpdate(if (threads.isEmpty()) getSeedThreads(activeOnly) else threads)
            }
    }

    fun fetchMessages(
        chatId: String,
        onSuccess: (List<ChatMessage>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (chatId.isBlank()) {
            onSuccess(getSeedMessages())
            return
        }

        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .get()
            .addOnSuccessListener { snapshot ->
                val messages = snapshot.documents.map { doc ->
                    ChatMessage(
                        id = doc.id,
                        senderId = doc.getString("senderId") ?: "",
                        message = doc.getString("message") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                }
                onSuccess(if (messages.isEmpty()) getSeedMessages() else messages)
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }

    fun listenMessages(
        chatId: String,
        onUpdate: (List<ChatMessage>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.map { doc ->
                    ChatMessage(
                        id = doc.id,
                        senderId = doc.getString("senderId") ?: "",
                        message = doc.getString("message") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                }.orEmpty()
                onUpdate(if (messages.isEmpty()) getSeedMessages() else messages)
            }
    }

    fun listenTyping(
        chatId: String,
        onUpdate: (String?, Boolean, Long) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return firestore.collection("chats")
            .document(chatId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }
                val typingUserId = snapshot?.getString("typingUserId")
                val isTyping = snapshot?.getBoolean("isTyping") ?: false
                val typingAt = snapshot?.getLong("typingAt") ?: 0L
                onUpdate(typingUserId, isTyping, typingAt)
            }
    }

    fun sendMessage(
        chatId: String,
        senderId: String,
        message: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val data = hashMapOf(
            "senderId" to senderId,
            "message" to message,
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .add(data)
            .addOnSuccessListener {
                firestore.collection("chats")
                    .document(chatId)
                    .update(
                        mapOf(
                            "lastMessage" to message,
                            "updatedAt" to System.currentTimeMillis()
                        )
                    )
                onSuccess()
            }
            .addOnFailureListener { exception -> onError(exception) }
    }

    fun updateTyping(
        chatId: String,
        userId: String,
        isTyping: Boolean
    ) {
        if (chatId.isBlank() || userId.isBlank()) {
            return
        }
        val data = hashMapOf(
            "typingUserId" to userId,
            "isTyping" to isTyping,
            "typingAt" to System.currentTimeMillis()
        )
        firestore.collection("chats")
            .document(chatId)
            .update(data)
    }

    private fun getSeedThreads(activeOnly: Boolean): List<ChatThread> {
        val list = listOf(
            ChatThread("c1", "Varcity Barbershop", "We are ready to serve you.", 0L, true),
            ChatThread("c2", "Twinsky Monkey Barber", "Let us know your time.", 0L, true),
            ChatThread("c3", "Barberman Haircut", "Thanks for visiting.", 0L, false)
        )
        return list.filter { it.isActive == activeOnly }
    }

    private fun getSeedMessages(): List<ChatMessage> = listOf(
        ChatMessage("m1", "shop", "Good morning, how can we help?", 0L),
        ChatMessage("m2", "guest", "I want a haircut this afternoon.", 0L)
    )
}
