package com.example.gobarbd.feature.customer.chat

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R
import com.google.firebase.auth.FirebaseAuth

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var viewModel: ChatRoomViewModel
    private lateinit var adapter: ChatRoomAdapter
    private var chatId: String = ""
    private var shopName: String = ""
    private var currentUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        chatId = intent.getStringExtra("CHAT_ID") ?: ""
        shopName = intent.getStringExtra("SHOP_NAME") ?: "Barbershop"
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (currentUserId.isBlank()) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        findViewById<TextView>(R.id.txtChatTitle).text = shopName
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        val recycler = findViewById<RecyclerView>(R.id.recyclerChatMessages)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = ChatRoomAdapter(mutableListOf(), currentUserId)
        recycler.adapter = adapter

        viewModel = ViewModelProvider(this)[ChatRoomViewModel::class.java]
        viewModel.messages.observe(this) { list ->
            adapter.updateData(list)
            recycler.scrollToPosition(list.size - 1)
        }
        viewModel.error.observe(this) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.load(chatId)

        val edtMessage = findViewById<EditText>(R.id.edtMessage)
        findViewById<ImageView>(R.id.btnSend).setOnClickListener {
            val text = edtMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                viewModel.send(chatId, currentUserId, text)
                edtMessage.setText("")
            }
        }
    }
}
