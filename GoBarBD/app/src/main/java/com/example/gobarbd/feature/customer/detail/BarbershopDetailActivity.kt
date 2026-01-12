package com.example.gobarbd.feature.customer.detail

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.gobarbd.R
import com.example.gobarbd.feature.customer.booking.BookAppointmentActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class BarbershopDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: BarbershopDetailViewModel
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private var shopId: String = ""
    private var shopName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barbershop_detail)

        shopId = intent.getStringExtra("SHOP_ID") ?: ""
        shopName = intent.getStringExtra("SHOP_NAME") ?: ""

        setupHeader()
        setupTabs()
        setupBookingButton()

        viewModel = ViewModelProvider(this)[BarbershopDetailViewModel::class.java]
        viewModel.shop.observe(this) { shop ->
            findViewById<TextView>(R.id.txtShopName).text = shop.name
            findViewById<TextView>(R.id.txtShopLocation).text = shop.location
            findViewById<TextView>(R.id.txtShopRating).text =
                "${shop.rating} (${shop.ratingCount})"
            findViewById<TextView>(R.id.txtOpenStatus).text =
                if (shop.isOpen) "Open" else "Closed"
            val headerImage = findViewById<ImageView>(R.id.imgShopHeader)
            headerImage.setImageResource(shop.imageResource)
        }
        viewModel.error.observe(this) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.load(shopId)
    }

    private fun setupHeader() {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<TextView>(R.id.txtShopName).text = shopName
    }

    private fun setupTabs() {
        tabLayout = findViewById(R.id.tabLayoutDetail)
        viewPager = findViewById(R.id.viewPagerDetail)
        viewPager.adapter = DetailPagerAdapter(this, shopId)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "About"
                1 -> "Service"
                2 -> "Schedule"
                else -> "Review"
            }
        }.attach()
    }

    private fun setupBookingButton() {
        findViewById<Button>(R.id.btnBookingNow).setOnClickListener {
            val intent = Intent(this, BookAppointmentActivity::class.java).apply {
                putExtra("SHOP_ID", shopId)
                putExtra("SHOP_NAME", shopName)
            }
            startActivity(intent)
        }
    }
}
