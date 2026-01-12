package com.example.gobarbd.feature.auth
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gobarbd.R

class ForgetPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        val btn = findViewById<android.widget.Button>(R.id.btnSend)
        btn.setOnClickListener {
            startActivity(Intent(this, Authentication::class.java))
        }
    }
}
