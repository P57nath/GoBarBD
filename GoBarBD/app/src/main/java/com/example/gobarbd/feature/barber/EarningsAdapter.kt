package com.example.gobarbd.feature.barber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R

class EarningsAdapter(
    private val items: MutableList<EarningsPeriod>
) : RecyclerView.Adapter<EarningsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtLabel: TextView = view.findViewById(R.id.txtEarningsLabel)
        val txtCount: TextView = view.findViewById(R.id.txtEarningsCount)
        val txtTotal: TextView = view.findViewById(R.id.txtEarningsTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_earnings_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.txtLabel.text = item.label
        holder.txtCount.text = "${item.count} jobs"
        holder.txtTotal.text = "$${item.total.toInt()}"
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<EarningsPeriod>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
