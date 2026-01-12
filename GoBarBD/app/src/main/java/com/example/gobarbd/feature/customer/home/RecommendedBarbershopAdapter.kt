package com.example.gobarbd.feature.customer.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R
import com.example.gobarbd.core.data.model.Barbershop

class RecommendedBarbershopAdapter(
    private val items: MutableList<Barbershop>,
    private val onBookingClick: (Barbershop) -> Unit
) : RecyclerView.Adapter<RecommendedBarbershopAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgRecommendedShop)
        val name: TextView = view.findViewById(R.id.txtRecommendedName)
        val location: TextView = view.findViewById(R.id.txtRecommendedLocation)
        val rating: TextView = view.findViewById(R.id.txtRecommendedRating)
        val btnBooking: Button = view.findViewById(R.id.btnBooking)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recommended, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val shop = items[position]
        holder.img.setImageResource(shop.imageResource)
        holder.name.text = shop.name
        holder.location.text = shop.location
        holder.rating.text = shop.rating.toString()
        holder.btnBooking.setOnClickListener { onBookingClick(shop) }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<Barbershop>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
