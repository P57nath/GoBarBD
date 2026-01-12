package com.example.gobarbd.feature.customer.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R
import com.google.firebase.auth.FirebaseAuth

class ChatFinishedFragment : Fragment() {

    private lateinit var viewModel: ChatListViewModel
    private lateinit var adapter: ChatThreadAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_chat_finished, container, false)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerChatFinished)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = ChatThreadAdapter(mutableListOf()) { thread ->
            val intent = Intent(requireContext(), ChatRoomActivity::class.java).apply {
                putExtra("CHAT_ID", thread.id)
                putExtra("SHOP_NAME", thread.shopName)
            }
            startActivity(intent)
        }
        recycler.adapter = adapter

        viewModel = ViewModelProvider(requireActivity())[ChatListViewModel::class.java]
        viewModel.finishedThreads.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
        }
        viewModel.error.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Please login", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.load(userId)
        }

        return view
    }
}
