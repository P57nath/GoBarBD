package com.example.gobarbd

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gobarbd.R

class OnBoard2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_board2)

        val btn = findViewById<android.widget.Button>(R.id.btnNext2)
        btn.setOnClickListener {
            startActivity(Intent(this, OnBoard3Activity::class.java))
            finish()
        }
    }
}