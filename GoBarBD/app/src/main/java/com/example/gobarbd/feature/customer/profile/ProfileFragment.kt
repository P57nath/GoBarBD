package com.example.gobarbd.feature.customer.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.gobarbd.R
import com.example.gobarbd.app.GoBarBdApp
import com.example.gobarbd.feature.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val user = FirebaseAuth.getInstance().currentUser
        view.findViewById<TextView>(R.id.txtProfileName).text =
            user?.displayName ?: "Customer"
        view.findViewById<TextView>(R.id.txtProfileEmail).text =
            user?.email ?: "Not signed in"
        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
            val intent = android.content.Intent(requireContext(), LoginActivity::class.java).apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
        }
        view.findViewById<Button>(R.id.btnTestNotification).setOnClickListener {
            if (ensureNotificationsPermission()) {
                val notification = NotificationCompat.Builder(
                    requireContext(),
                    GoBarBdApp.NOTIFICATION_CHANNEL_ID
                )
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("GoBarBD Test")
                    .setContentText("This is a local test notification.")
                    .setAutoCancel(true)
                    .build()
                NotificationManagerCompat.from(requireContext()).notify(1001, notification)
            }
        }
        return view
    }

    private fun ensureNotificationsPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }
        val granted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            return true
        }
        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 5001)
        Toast.makeText(requireContext(), "Allow notifications to test", Toast.LENGTH_SHORT).show()
        return false
    }
}
