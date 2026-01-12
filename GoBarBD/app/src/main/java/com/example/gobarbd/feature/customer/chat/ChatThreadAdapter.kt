package com.example.gobarbd.feature.customer.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R
import com.example.gobarbd.core.data.model.ChatThread

class ChatThreadAdapter(
    private val items: MutableList<ChatThread>,
    private val onClick: (ChatThread) -> Unit
) : RecyclerView.Adapter<ChatThreadAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.imgChatAvatar)
        val name: TextView = itemView.findViewById(R.id.txtChatName)
        val message: TextView = itemView.findViewById(R.id.txtChatMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_thread, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val thread = items[position]
        holder.name.text = thread.shopName
        holder.message.text = thread.lastMessage
        holder.avatar.setImageResource(R.drawable.avatar1)
        holder.itemView.setOnClickListener { onClick(thread) }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<ChatThread>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
