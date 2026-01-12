package com.example.gobarbd.feature.customer.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R
import com.example.gobarbd.core.data.model.Review

class ReviewListAdapter(
    private val items: MutableList<Review>
) : RecyclerView.Adapter<ReviewListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.imgReviewer)
        val name: TextView = itemView.findViewById(R.id.txtReviewerName)
        val rating: TextView = itemView.findViewById(R.id.txtReviewerRating)
        val comment: TextView = itemView.findViewById(R.id.txtReviewerComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = items[position]
        holder.name.text = review.userName
        holder.rating.text = "${review.rating}"
        holder.comment.text = review.comment
        holder.avatar.setImageResource(R.drawable.avatar2)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Review>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
