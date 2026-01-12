package com.example.gobarbd.feature.barber

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BarberEarningsActivity : AppCompatActivity() {

    private lateinit var viewModel: BarberEarningsViewModel
    private lateinit var weeklyAdapter: EarningsAdapter
    private lateinit var monthlyAdapter: EarningsAdapter
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barber_earnings)

        findViewById<TextView>(R.id.btnBackEarnings).setOnClickListener { finish() }

        val txtWeek = findViewById<TextView>(R.id.txtEarningsWeek)
        val txtMonth = findViewById<TextView>(R.id.txtEarningsMonth)
        val txtWeeklyEmpty = findViewById<TextView>(R.id.txtWeeklyEmpty)
        val txtMonthlyEmpty = findViewById<TextView>(R.id.txtMonthlyEmpty)

        val recyclerWeekly = findViewById<RecyclerView>(R.id.recyclerWeekly)
        val recyclerMonthly = findViewById<RecyclerView>(R.id.recyclerMonthly)
        weeklyAdapter = EarningsAdapter(mutableListOf())
        monthlyAdapter = EarningsAdapter(mutableListOf())
        recyclerWeekly.layoutManager = LinearLayoutManager(this)
        recyclerMonthly.layoutManager = LinearLayoutManager(this)
        recyclerWeekly.adapter = weeklyAdapter
        recyclerMonthly.adapter = monthlyAdapter

        viewModel = ViewModelProvider(this)[BarberEarningsViewModel::class.java]
        viewModel.weekTotal.observe(this) { total ->
            txtWeek.text = "$${total.toInt()}"
        }
        viewModel.monthTotal.observe(this) { total ->
            txtMonth.text = "$${total.toInt()}"
        }
        viewModel.weeklyBreakdown.observe(this) { list ->
            weeklyAdapter.updateData(list)
            txtWeeklyEmpty.visibility = if (list.isEmpty()) TextView.VISIBLE else TextView.GONE
        }
        viewModel.monthlyBreakdown.observe(this) { list ->
            monthlyAdapter.updateData(list)
            txtMonthlyEmpty.visibility = if (list.isEmpty()) TextView.VISIBLE else TextView.GONE
        }
        viewModel.error.observe(this) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        firestore.collection("users").document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                val barberId = doc.getString("barberId") ?: user.uid
                viewModel.load(barberId)
            }
            .addOnFailureListener {
                viewModel.load(user.uid)
            }
    }
}
