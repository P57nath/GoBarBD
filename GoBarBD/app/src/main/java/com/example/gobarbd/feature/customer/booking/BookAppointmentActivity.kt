package com.example.gobarbd.feature.customer.booking

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R
import com.example.gobarbd.core.data.model.Service
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookAppointmentActivity : AppCompatActivity() {

    private lateinit var viewModel: BookingViewModel
    private val calendar = Calendar.getInstance()

    private var selectedService: Service? = null
    private var selectedTime: String = "08:00"

    private var shopId: String = ""
    private var shopName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_appointment)

        shopId = intent.getStringExtra("SHOP_ID") ?: ""
        shopName = intent.getStringExtra("SHOP_NAME") ?: "Barbershop"

        val txtDate = findViewById<TextView>(R.id.txtSelectedDate)
        val btnPickDate = findViewById<Button>(R.id.btnPickDate)
        val txtShop = findViewById<TextView>(R.id.txtShopTitle)
        val txtServicePrice = findViewById<TextView>(R.id.txtServicePrice)

        txtShop.text = shopName
        updateDateLabel(txtDate)

        btnPickDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, day)
                    updateDateLabel(txtDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val serviceRecycler = findViewById<RecyclerView>(R.id.recyclerServices)
        serviceRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val serviceAdapter = ServiceSelectAdapter(mutableListOf()) { service ->
            selectedService = service
            txtServicePrice.text = "$${service.price.toInt()}"
        }
        serviceRecycler.adapter = serviceAdapter

        val times = listOf("08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00")
        val timeRecycler = findViewById<RecyclerView>(R.id.recyclerTimes)
        timeRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        timeRecycler.adapter = TimeSlotAdapter(times) { time ->
            selectedTime = time
        }

        viewModel = ViewModelProvider(this)[BookingViewModel::class.java]
        viewModel.services.observe(this) { list ->
            serviceAdapter.updateData(list)
        }
        viewModel.error.observe(this) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.loadServices(shopId)

        findViewById<Button>(R.id.btnDealBooking).setOnClickListener {
            val service = selectedService
            if (service == null) {
                Toast.makeText(this, "Select service", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, BookingDetailActivity::class.java).apply {
                putExtra("SHOP_ID", shopId)
                putExtra("SHOP_NAME", shopName)
                putExtra("SERVICE_ID", service.id)
                putExtra("SERVICE_NAME", service.name)
                putExtra("SERVICE_PRICE", service.price)
                putExtra("SERVICE_DURATION", service.durationMin)
                putExtra("DATE_MILLIS", calendar.timeInMillis)
                putExtra("TIME_LABEL", selectedTime)
            }
            startActivity(intent)
        }
    }

    private fun updateDateLabel(target: TextView) {
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        target.text = formatter.format(calendar.time)
    }
}
