package com.example.gobarbd.feature.customer.booking

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.gobarbd.R
import com.example.gobarbd.core.data.model.Barber
import com.example.gobarbd.core.data.model.BookingRequest
import com.example.gobarbd.core.data.model.Service
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateBookingActivity : AppCompatActivity() {

    private lateinit var viewModel: BookingViewModel
    private val calendar = Calendar.getInstance()

    private var services: List<Service> = emptyList()
    private var barbers: List<Barber> = emptyList()

    private lateinit var txtShopName: TextView
    private lateinit var spinnerService: Spinner
    private lateinit var spinnerBarber: Spinner
    private lateinit var txtDate: TextView
    private lateinit var txtTime: TextView
    private lateinit var radioGroupPayment: RadioGroup
    private lateinit var btnConfirm: Button

    private var shopId: String = ""
    private var shopName: String = "Barbershop"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_booking)

        shopId = intent.getStringExtra("SHOP_ID") ?: ""
        shopName = intent.getStringExtra("SHOP_NAME") ?: "Barbershop"

        initViews()
        setupViewModel()
        setupDateTimePickers()

        txtShopName.text = shopName
        viewModel.loadServices(shopId)
        viewModel.loadBarbers(shopId)
    }

    private fun initViews() {
        txtShopName = findViewById(R.id.txtShopName)
        spinnerService = findViewById(R.id.spinnerService)
        spinnerBarber = findViewById(R.id.spinnerBarber)
        txtDate = findViewById(R.id.txtDate)
        txtTime = findViewById(R.id.txtTime)
        radioGroupPayment = findViewById(R.id.radioGroupPayment)
        btnConfirm = findViewById(R.id.btnConfirmBooking)

        btnConfirm.setOnClickListener { submitBooking() }
        updateDateTimeLabels()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[BookingViewModel::class.java]
        viewModel.services.observe(this) { list ->
            services = list
            val names = list.map { "${it.name} (${it.durationMin} min)" }
            spinnerService.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                names
            )
        }
        viewModel.barbers.observe(this) { list ->
            barbers = list
            val names = list.map { it.displayName }
            spinnerBarber.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                names
            )
        }
        viewModel.bookingSuccess.observe(this) { success ->
            if (success == true) {
                Toast.makeText(this, "Booking created", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        viewModel.error.observe(this) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupDateTimePickers() {
        findViewById<Button>(R.id.btnPickDate).setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    updateDateTimeLabels()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        findViewById<Button>(R.id.btnPickTime).setOnClickListener {
            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    updateDateTimeLabels()
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun updateDateTimeLabels() {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        txtDate.text = dateFormat.format(calendar.time)
        txtTime.text = timeFormat.format(calendar.time)
    }

    private fun submitBooking() {
        if (services.isEmpty() || barbers.isEmpty()) {
            Toast.makeText(this, "Please wait for data to load", Toast.LENGTH_SHORT).show()
            return
        }

        val service = services.getOrNull(spinnerService.selectedItemPosition)
        val barber = barbers.getOrNull(spinnerBarber.selectedItemPosition)
        if (service == null || barber == null) {
            Toast.makeText(this, "Select service and barber", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedPaymentId = radioGroupPayment.checkedRadioButtonId
        if (selectedPaymentId == -1) {
            Toast.makeText(this, "Select payment method", Toast.LENGTH_SHORT).show()
            return
        }

        val paymentMethod = findViewById<RadioButton>(selectedPaymentId).tag.toString()
        val startMillis = calendar.timeInMillis
        val endMillis = startMillis + service.durationMin * 60 * 1000L

        val request = BookingRequest(
            customerId = "guest",
            shopId = shopId,
            shopName = shopName,
            shopLocation = "",
            barberId = barber.id,
            serviceId = service.id,
            startTimeMillis = startMillis,
            endTimeMillis = endMillis,
            paymentMethod = paymentMethod
        )

        viewModel.createBooking(request)
    }
}
