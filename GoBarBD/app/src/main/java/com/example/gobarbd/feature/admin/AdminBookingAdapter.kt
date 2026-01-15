package com.example.gobarbd.feature.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R
import com.example.gobarbd.core.data.model.Booking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminBookingAdapter(
    private val items: MutableList<Booking>
) : RecyclerView.Adapter<AdminBookingAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtShop: TextView = view.findViewById(R.id.txtAdminBookingShop)
        val txtStatus: TextView = view.findViewById(R.id.txtAdminBookingStatus)
        val txtTime: TextView = view.findViewById(R.id.txtAdminBookingTime)
        val txtPrice: TextView = view.findViewById(R.id.txtAdminBookingPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_booking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.txtShop.text = item.shopName
        holder.txtStatus.text = item.status
        holder.txtTime.text = if (item.startTimeMillis > 0L) {
            dateFormat.format(Date(item.startTimeMillis))
        } else {
            "Scheduled"
        }
        holder.txtPrice.text = "$${item.totalPrice.toInt()}"
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Booking>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
