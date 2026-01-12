package com.example.gobarbd.feature.customer.booking

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gobarbd.R

class InvoiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)

        val shopName = intent.getStringExtra("SHOP_NAME") ?: "Barbershop"
        val serviceName = intent.getStringExtra("SERVICE_NAME") ?: ""
        val totalPrice = intent.getDoubleExtra("TOTAL_PRICE", 0.0)

        findViewById<TextView>(R.id.txtInvoiceShop).text = shopName
        findViewById<TextView>(R.id.txtInvoiceService).text = serviceName
        findViewById<TextView>(R.id.txtInvoiceTotal).text = "$${totalPrice.toInt()}"

        findViewById<Button>(R.id.btnInvoiceBack).setOnClickListener {
            finish()
        }
    }
}
