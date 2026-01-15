package com.example.gobarbd.feature.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R
import com.example.gobarbd.core.data.model.Barbershop

class AdminShopAdapter(
    private val items: MutableList<Barbershop>,
    private val onEdit: (Barbershop) -> Unit
) : RecyclerView.Adapter<AdminShopAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtAdminShopName)
        val txtAddress: TextView = view.findViewById(R.id.txtAdminShopAddress)
        val txtStatus: TextView = view.findViewById(R.id.txtAdminShopStatus)
        val btnEdit: Button = view.findViewById(R.id.btnAdminShopEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_shop, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.txtName.text = item.name
        holder.txtAddress.text = item.location
        holder.txtStatus.text = if (item.isOpen) "Open" else "Closed"
        holder.btnEdit.setOnClickListener { onEdit(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Barbershop>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
