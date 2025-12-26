package com.example.gobarbd
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gobarbd.R

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btn = findViewById<android.widget.Button>(R.id.btnRegister)
        btn.setOnClickListener {
            startActivity(Intent(this, Authentication::class.java))
        }

        val txt_login = findViewById<android.widget.TextView>(R.id.txtLogin)
        txt_login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}