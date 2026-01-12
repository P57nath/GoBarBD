package com.example.gobarbd.feature.auth
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gobarbd.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.FirebaseNetworkException

class ForgetPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val btn = findViewById<android.widget.Button>(R.id.btnSend)
        btn.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Password reset email sent",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this,
                        mapAuthError(exception),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun mapAuthError(exception: Exception): String {
        return when (exception) {
            is FirebaseAuthInvalidUserException -> "Account not found."
            is FirebaseNetworkException -> "Network error. Check your connection."
            is FirebaseAuthException -> exception.message ?: "Failed to send reset email."
            else -> exception.message ?: "Failed to send reset email."
        }
    }
}
