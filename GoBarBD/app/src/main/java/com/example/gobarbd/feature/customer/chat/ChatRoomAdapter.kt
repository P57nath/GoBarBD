package com.example.gobarbd.feature.customer.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R
import com.example.gobarbd.core.data.model.ChatMessage

class ChatRoomAdapter(
    private val items: MutableList<ChatMessage>,
    private val currentUserId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewTypeMine = 1
    private val viewTypeOther = 2

    override fun getItemViewType(position: Int): Int {
        return if (items[position].senderId == currentUserId) viewTypeMine else viewTypeOther
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = if (viewType == viewTypeMine) {
            R.layout.item_chat_message_mine
        } else {
            R.layout.item_chat_message_other
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = items[position]
        (holder as MessageViewHolder).txtMessage.text = message.message
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<ChatMessage>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtMessage: TextView = itemView.findViewById(R.id.txtMessage)
    }
}
