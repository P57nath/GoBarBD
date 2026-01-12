package com.example.gobarbd.feature.barber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R

class BarberScheduleAdapter(
    private val items: MutableList<ScheduleSlot>
) : RecyclerView.Adapter<BarberScheduleAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTime: TextView = view.findViewById(R.id.txtScheduleTime)
        val txtStatus: TextView = view.findViewById(R.id.txtScheduleStatus)
        val txtCustomer: TextView = view.findViewById(R.id.txtScheduleCustomer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_barber_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.txtTime.text = item.timeLabel
        holder.txtStatus.text = item.statusLabel
        holder.txtCustomer.text = item.customerLabel
        if (item.isBooked) {
            holder.txtStatus.setBackgroundResource(R.drawable.bg_status_booked)
            holder.txtStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
        } else {
            holder.txtStatus.setBackgroundResource(R.drawable.bg_status_available)
            holder.txtStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.purple_primary))
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<ScheduleSlot>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
