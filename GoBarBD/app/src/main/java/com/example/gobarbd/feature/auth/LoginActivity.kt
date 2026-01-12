package com.example.gobarbd.feature.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gobarbd.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val email = binding.edtUsername.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val user = result.user
                    if (user != null && user.isEmailVerified) {
                        routeByRole(user.uid)
                    } else {
                        user?.sendEmailVerification()
                        auth.signOut()
                        Toast.makeText(
                            this,
                            "Verification email sent. Please verify before login.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, exception.message ?: "Login failed", Toast.LENGTH_SHORT).show()
                }
        }

        binding.txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.txtForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgetPassword::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if (user != null && user.isEmailVerified) {
            routeByRole(user.uid)
        }
    }

    private fun routeByRole(userId: String) {
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
