package com.example.gobarbd.feature.barber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R
import com.example.gobarbd.core.data.model.Booking

class BarberBookingsAdapter(
    private val items: MutableList<Booking>,
    private val onStatusClick: (Booking, String) -> Unit,
    private val onRescheduleClick: (Booking) -> Unit,
    private val onNoteClick: (Booking) -> Unit,
    private val onNoShowClick: (Booking) -> Unit
) : RecyclerView.Adapter<BarberBookingsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgShop: ImageView = view.findViewById(R.id.imgShop)
        val txtName: TextView = view.findViewById(R.id.txtShopName)
        val txtLocation: TextView = view.findViewById(R.id.txtLocation)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
        val txtStatus: TextView = view.findViewById(R.id.txtStatus)
        val btnWaiting: Button = view.findViewById(R.id.btnWaiting)
        val btnOnProcess: Button = view.findViewById(R.id.btnOnProcess)
        val btnComplete: Button = view.findViewById(R.id.btnComplete)
        val btnCancel: Button = view.findViewById(R.id.btnCancel)
        val btnReschedule: Button = view.findViewById(R.id.btnReschedule)
        val btnAddNote: Button = view.findViewById(R.id.btnAddNote)
        val btnNoShow: Button = view.findViewById(R.id.btnNoShow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_barber_booking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.imgShop.setImageResource(item.imageRes)
        holder.txtName.text = item.shopName
        holder.txtLocation.text = item.shopLocation
        holder.txtRating.text = item.rating.toString()
        holder.txtStatus.text = "Status: ${item.status}"
        holder.btnWaiting.setOnClickListener { onStatusClick(item, "WAITING") }
        holder.btnOnProcess.setOnClickListener { onStatusClick(item, "ON_PROCESS") }
        holder.btnComplete.setOnClickListener { onStatusClick(item, "COMPLETED") }
        holder.btnCancel.setOnClickListener { onStatusClick(item, "CANCELLED") }
        holder.btnReschedule.setOnClickListener { onRescheduleClick(item) }
        holder.btnAddNote.setOnClickListener { onNoteClick(item) }
        holder.btnNoShow.setOnClickListener { onNoShowClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Booking>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
