package com.example.gobarbd.feature.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.gobarbd.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null && user.isEmailVerified) {
                routeByRole(user.uid)
            } else {
                startActivity(Intent(this, OnBoard1Activity::class.java))
                finish()
            }
        }, 2000)
    }

    private fun routeByRole(userId: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val role = doc.getString("role") ?: "customer"
                val intent = Intent(this, com.example.gobarbd.app.MainActivity::class.java).apply {
                    putExtra("ROLE", role)
                }
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                val intent = Intent(this, com.example.gobarbd.app.MainActivity::class.java).apply {
                    putExtra("ROLE", "customer")
                }
                startActivity(intent)
                finish()
            }
    }
}
