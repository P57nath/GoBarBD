package com.example.gobarbd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NearestBarbershopAdapter(
    private val items: List<Barbershop>,
    private val onClick: (Barbershop) -> Unit
) : RecyclerView.Adapter<NearestBarbershopAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgShop)
        val name: TextView = view.findViewById(R.id.txtShopName)
        val location: TextView = view.findViewById(R.id.txtLocation)
        val rating: TextView = view.findViewById(R.id.txtRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_barbershop, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val shop = items[position]
        holder.img.setImageResource(shop.imageResource)
        holder.name.text = shop.name
        holder.location.text = shop.location
        holder.rating.text = shop.rating.toString()
        holder.itemView.setOnClickListener { onClick(shop) }
    }

    override fun getItemCount() = items.size
}
