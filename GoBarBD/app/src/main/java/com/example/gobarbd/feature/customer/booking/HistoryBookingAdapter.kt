package com.example.gobarbd.feature.customer.booking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R
import com.example.gobarbd.core.data.model.Booking

class HistoryBookingAdapter(
    private val items: MutableList<Booking>
) : RecyclerView.Adapter<HistoryBookingAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgShop: ImageView = view.findViewById(R.id.imgShop)
        val txtName: TextView = view.findViewById(R.id.txtShopName)
        val txtLocation: TextView = view.findViewById(R.id.txtLocation)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_booking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.imgShop.setImageResource(item.imageRes)
        holder.txtName.text = item.shopName
        holder.txtLocation.text = item.shopLocation
        holder.txtRating.text = item.rating.toString()
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Booking>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
