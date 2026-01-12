package com.example.gobarbd.feature.customer.booking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.gobarbd.R
import com.example.gobarbd.core.data.model.Barber
import com.example.gobarbd.core.data.model.BookingRequest
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BookingDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: BookingViewModel
    private var barbers: List<Barber> = emptyList()

    private var shopId: String = ""
    private var shopName: String = ""
    private var serviceId: String = ""
    private var serviceName: String = ""
    private var servicePrice: Double = 0.0
    private var serviceDuration: Int = 30
    private var dateMillis: Long = 0L
    private var timeLabel: String = ""
    private var shopLocation: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_detail)

        shopId = intent.getStringExtra("SHOP_ID") ?: ""
        shopName = intent.getStringExtra("SHOP_NAME") ?: "Barbershop"
        shopLocation = intent.getStringExtra("SHOP_LOCATION") ?: ""
        serviceId = intent.getStringExtra("SERVICE_ID") ?: ""
        serviceName = intent.getStringExtra("SERVICE_NAME") ?: ""
        servicePrice = intent.getDoubleExtra("SERVICE_PRICE", 0.0)
        serviceDuration = intent.getIntExtra("SERVICE_DURATION", 30)
        dateMillis = intent.getLongExtra("DATE_MILLIS", 0L)
        timeLabel = intent.getStringExtra("TIME_LABEL") ?: ""

        findViewById<TextView>(R.id.txtShopName).text = shopName
        findViewById<TextView>(R.id.txtServiceName).text = serviceName
        findViewById<TextView>(R.id.txtServicePrice).text = "$${servicePrice.toInt()}"
        findViewById<TextView>(R.id.txtTotalPrice).text = "$${servicePrice.toInt()}"
        findViewById<TextView>(R.id.txtDateTime).text = buildDateTime()

        viewModel = ViewModelProvider(this)[BookingViewModel::class.java]
        viewModel.barbers.observe(this) { list -> barbers = list }
        viewModel.bookingSuccess.observe(this) { success ->
            if (success == true) {
                startActivity(Intent(this, InvoiceActivity::class.java).apply {
                    putExtra("SHOP_NAME", shopName)
                    putExtra("SERVICE_NAME", serviceName)
                    putExtra("TOTAL_PRICE", servicePrice)
                })
                finish()
            }
        }
        viewModel.error.observe(this) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.loadBarbers(shopId)

        findViewById<Button>(R.id.btnPayNow).setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId.isNullOrBlank()) {
                Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val barber = barbers.firstOrNull()
            if (barber == null) {
                Toast.makeText(this, "No barber available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val startMillis = buildStartMillis()
            if (startMillis <= System.currentTimeMillis()) {
                Toast.makeText(this, "Select a future time slot", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val endMillis = startMillis + serviceDuration * 60 * 1000L
            val request = BookingRequest(
                customerId = userId,
                shopId = shopId,
                shopName = shopName,
                shopLocation = shopLocation,
                barberId = barber.id,
                serviceId = serviceId,
                startTimeMillis = startMillis,
                endTimeMillis = endMillis,
                paymentMethod = "ONLINE",
                status = "ACTIVE"
            )
            viewModel.createBooking(request)
        }
    }

    private fun buildStartMillis(): Long {
        val parts = timeLabel.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateMillis
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun buildDateTime(): String {
        val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        val date = Date(dateMillis)
        return "${formatter.format(date)} - $timeLabel"
    }
}
