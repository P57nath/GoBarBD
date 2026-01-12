package com.example.gobarbd.feature.customer.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R
import com.example.gobarbd.core.data.model.Barber

class BarberListAdapter(
    private val items: MutableList<Barber>
) : RecyclerView.Adapter<BarberListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.imgBarberAvatar)
        val name: TextView = itemView.findViewById(R.id.txtBarberName)
        val role: TextView = itemView.findViewById(R.id.txtBarberRole)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_barber, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val barber = items[position]
        holder.name.text = barber.displayName
        holder.role.text = "Specialist"
        holder.avatar.setImageResource(R.drawable.avatar1)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Barber>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
