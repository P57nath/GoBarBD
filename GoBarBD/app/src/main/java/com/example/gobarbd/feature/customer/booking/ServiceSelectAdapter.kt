package com.example.gobarbd.feature.customer.booking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R
import com.example.gobarbd.core.data.model.Service

class ServiceSelectAdapter(
    private val items: MutableList<Service>,
    private val onSelect: (Service) -> Unit
) : RecyclerView.Adapter<ServiceSelectAdapter.ViewHolder>() {

    private var selectedPosition = 0

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.imgServiceIcon)
        val name: TextView = itemView.findViewById(R.id.txtServiceName)
        val price: TextView = itemView.findViewById(R.id.txtServicePrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service_select, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val service = items[position]
        holder.name.text = service.name
        holder.price.text = "$${service.price.toInt()}"
        holder.icon.setImageResource(R.drawable.ic_scissors)
        holder.itemView.isSelected = position == selectedPosition
        holder.itemView.setOnClickListener {
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged()
            onSelect(service)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Service>) {
        items.clear()
        items.addAll(newItems)
        selectedPosition = 0
        notifyDataSetChanged()
        if (items.isNotEmpty()) {
            onSelect(items.first())
        }
    }
}
