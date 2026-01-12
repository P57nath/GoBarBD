package com.example.gobarbd.feature.customer.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R
import com.example.gobarbd.core.data.model.Barbershop

class BarbershopAdapter(
    private val barbershopList: List<Barbershop>,
    private val onItemClick: (Barbershop) -> Unit
) : RecyclerView.Adapter<BarbershopAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_barbershop, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shop = barbershopList[position]

        holder.txtShopName.text = shop.name
        holder.txtLocation.text = shop.location
        holder.txtRating.text = shop.rating.toString()
        holder.imgShop.setImageResource(shop.imageResource)

        holder.itemView.setOnClickListener {
            onItemClick(shop)
        }
    }

    override fun getItemCount(): Int = barbershopList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgShop: ImageView = itemView.findViewById(R.id.imgShop)
        val txtShopName: TextView = itemView.findViewById(R.id.txtShopName)
        val txtLocation: TextView = itemView.findViewById(R.id.txtLocation)
        val txtRating: TextView = itemView.findViewById(R.id.txtRating)
    }
}
