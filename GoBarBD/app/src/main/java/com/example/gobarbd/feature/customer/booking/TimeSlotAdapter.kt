package com.example.gobarbd.feature.customer.booking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R

class TimeSlotAdapter(
    private val items: List<String>,
    private val onSelect: (String) -> Unit
) : RecyclerView.Adapter<TimeSlotAdapter.ViewHolder>() {

    private var selectedPosition = 0

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.txtTimeSlot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_time_slot, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val time = items[position]
        holder.time.text = time
        holder.itemView.isSelected = position == selectedPosition
        holder.itemView.setOnClickListener {
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged()
            onSelect(time)
        }
    }

    override fun getItemCount(): Int = items.size
}
