package com.example.gobarbd.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gobarbd.R
import com.example.gobarbd.feature.admin.AdminBookingsFragment
import com.example.gobarbd.feature.admin.AdminDashboardFragment
import com.example.gobarbd.feature.admin.AdminSettingsFragment
import com.example.gobarbd.feature.admin.AdminShopsFragment
import com.example.gobarbd.feature.barber.BarberBookingsFragment
import com.example.gobarbd.feature.barber.BarberDashboardFragment
import com.example.gobarbd.feature.barber.BarberProfileFragment
import com.example.gobarbd.feature.barber.BarberScheduleFragment
import com.example.gobarbd.feature.customer.booking.BookingFragment
import com.example.gobarbd.feature.customer.chat.ChatFragment
import com.example.gobarbd.feature.customer.home.HomeFragment
import com.example.gobarbd.feature.customer.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var role: String = "customer"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        role = savedInstanceState?.getString("ROLE")
            ?: intent.getStringExtra("ROLE")
            ?: "customer"

        setupMenuForRole(bottomNav, role)

        // Load Home Fragment by default
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, defaultFragmentForRole(role))
                .commit()
        }

        // Bottom Navigation Click Listener
        bottomNav.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment? = when (role) {
                "barber" -> when (item.itemId) {
                    R.id.nav_barber_dashboard -> BarberDashboardFragment()
                    R.id.nav_barber_bookings -> BarberBookingsFragment()
                    R.id.nav_barber_chat -> ChatFragment()
                    R.id.nav_barber_schedule -> BarberScheduleFragment()
                    R.id.nav_barber_profile -> BarberProfileFragment()
                    else -> null
                }
                "admin" -> when (item.itemId) {
                    R.id.nav_admin_dashboard -> AdminDashboardFragment()
                    R.id.nav_admin_shops -> AdminShopsFragment()
                    R.id.nav_admin_bookings -> AdminBookingsFragment()
                    R.id.nav_admin_settings -> AdminSettingsFragment()
                    else -> null
                }
                else -> when (item.itemId) {
                    R.id.nav_home -> HomeFragment()
                    R.id.nav_booking -> BookingFragment()
                    R.id.nav_chat -> ChatFragment()
                    R.id.nav_profile -> ProfileFragment()
                    else -> null
                }
            }

            selectedFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }

            true
        }
    }

    private fun setupMenuForRole(bottomNav: BottomNavigationView, role: String) {
        bottomNav.menu.clear()
        when (role) {
            "barber" -> bottomNav.inflateMenu(R.menu.bottom_nav_menu_barber)
            "admin" -> bottomNav.inflateMenu(R.menu.bottom_nav_menu_admin)
            else -> bottomNav.inflateMenu(R.menu.bottom_nav_menu)
        }
    }

    private fun defaultFragmentForRole(role: String): Fragment {
        return when (role) {
            "barber" -> BarberDashboardFragment()
            "admin" -> AdminDashboardFragment()
            else -> HomeFragment()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("ROLE", role)
    }
}
