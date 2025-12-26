package com.example.gobarbd
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gobarbd.R

class OnBoard1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_board1)

        val btn = findViewById<android.widget.Button>(R.id.btnNext)
        btn.setOnClickListener {
            startActivity(Intent(this, OnBoard2Activity::class.java))
        }
    }
}