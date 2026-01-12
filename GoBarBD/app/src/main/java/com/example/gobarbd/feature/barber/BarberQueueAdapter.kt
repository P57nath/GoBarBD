package com.example.gobarbd.feature.barber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R
import com.example.gobarbd.core.data.model.Booking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BarberQueueAdapter(
    private val items: MutableList<Booking>
) : RecyclerView.Adapter<BarberQueueAdapter.ViewHolder>() {

    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgShop: ImageView = view.findViewById(R.id.imgShop)
        val txtTime: TextView = view.findViewById(R.id.txtTime)
        val txtShopName: TextView = view.findViewById(R.id.txtShopName)
        val txtCustomer: TextView = view.findViewById(R.id.txtCustomer)
        val txtStatus: TextView = view.findViewById(R.id.txtStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_barber_queue, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.imgShop.setImageResource(item.imageRes)
        holder.txtTime.text = if (item.startTimeMillis > 0L) {
            timeFormat.format(Date(item.startTimeMillis))
        } else {
            "Today"
        }
        holder.txtShopName.text = item.shopName
        holder.txtCustomer.text = if (item.customerId.isNotBlank()) {
            "Customer: ${item.customerId.take(6)}"
        } else {
            "Customer: Walk-in"
        }
        holder.txtStatus.text = item.status
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Booking>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
