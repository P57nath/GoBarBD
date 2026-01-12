package com.example.gobarbd.feature.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.gobarbd.R
import com.example.gobarbd.app.MainActivity

class RoleSelectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role_select)

        findViewById<Button>(R.id.btnRoleCustomer).setOnClickListener {
            openMain("customer")
        }
        findViewById<Button>(R.id.btnRoleBarber).setOnClickListener {
            openMain("barber")
        }
        findViewById<Button>(R.id.btnRoleAdmin).setOnClickListener {
            openMain("admin")
        }
    }

    private fun openMain(role: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("ROLE", role)
        }
        startActivity(intent)
        finish()
    }
}
