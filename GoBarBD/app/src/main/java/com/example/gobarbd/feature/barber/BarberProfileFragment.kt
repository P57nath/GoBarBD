package com.example.gobarbd.feature.barber

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import com.example.gobarbd.R
import com.example.gobarbd.feature.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class BarberProfileFragment : Fragment() {

    private val firestore = FirebaseFirestore.getInstance()
    private var currentPhone: String = ""
    private var currentName: String = ""
    private var currentAvatarUri: String = ""
    private lateinit var imgAvatar: ImageView

    private val pickAvatar = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val user = FirebaseAuth.getInstance().currentUser ?: return@registerForActivityResult
            currentAvatarUri = uri.toString()
            imgAvatar.setImageURI(uri)
            firestore.collection("users").document(user.uid)
                .update("avatarUri", currentAvatarUri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val user = FirebaseAuth.getInstance().currentUser
        imgAvatar = view.findViewById(R.id.imgProfileAvatar)
        val txtName = view.findViewById<TextView>(R.id.txtProfileName)
        val txtEmail = view.findViewById<TextView>(R.id.txtProfileEmail)
        val txtPhone = view.findViewById<TextView>(R.id.txtProfilePhone)
        val txtAvailability = view.findViewById<TextView>(R.id.txtAvailability)

        txtName.text = user?.displayName ?: "Barber"
        txtEmail.text = user?.email ?: "Not signed in"
        txtPhone.text = "Phone: -"

        view.findViewById<Button>(R.id.btnEditProfile).setOnClickListener {
            openEditDialog(txtName, txtPhone)
        }
        imgAvatar.setOnClickListener {
            pickAvatar.launch("image/*")
        }

        if (user != null) {
            firestore.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    currentName = doc.getString("name") ?: (user.displayName ?: "Barber")
                    currentPhone = doc.getString("phone") ?: ""
                    currentAvatarUri = doc.getString("avatarUri") ?: ""
                    txtName.text = currentName
                    txtPhone.text = if (currentPhone.isNotBlank()) {
                        "Phone: $currentPhone"
                    } else {
                        "Phone: -"
                    }
                    val avatarUri = currentAvatarUri
                    if (avatarUri.isNotBlank()) {
                        imgAvatar.setImageURI(Uri.parse(avatarUri))
                    }
                    val startMinutes = getLongSafe(doc, "workingStartMinutes", 540L).toInt()
                    val endMinutes = getLongSafe(doc, "workingEndMinutes", 1200L).toInt()
                    val slotMinutes = getLongSafe(doc, "slotDurationMinutes", 30L).toInt()
                    txtAvailability.text = formatAvailability(startMinutes, endMinutes, slotMinutes)
                }
        }
        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
            val intent = android.content.Intent(requireContext(), LoginActivity::class.java).apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
        }
        return view
    }

    private fun openEditDialog(txtName: TextView, txtPhone: TextView) {
        val context = requireContext()
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val padding = (16 * resources.displayMetrics.density).toInt()
            setPadding(padding, padding, padding, padding)
        }
        val edtName = EditText(context).apply {
            hint = "Display name"
            setText(currentName)
        }
        val edtPhone = EditText(context).apply {
            hint = "Phone"
            inputType = InputType.TYPE_CLASS_PHONE
            setText(currentPhone)
        }
        container.addView(edtName)
        container.addView(edtPhone)

        AlertDialog.Builder(context)
            .setTitle("Edit profile")
            .setView(container)
            .setPositiveButton("Save") { _, _ ->
                val user = FirebaseAuth.getInstance().currentUser ?: return@setPositiveButton
                val newNameInput = edtName.text.toString().trim()
                val newPhoneInput = edtPhone.text.toString().trim()
                val newName = if (newNameInput.isNotBlank()) newNameInput else currentName
                val newPhone = if (newPhoneInput.isNotBlank()) newPhoneInput else currentPhone
                if (newName.isNotBlank()) {
                    val profileUpdate = UserProfileChangeRequest.Builder()
                        .setDisplayName(newName)
                        .build()
                    user.updateProfile(profileUpdate)
                }
                firestore.collection("users").document(user.uid)
                    .update(
                        mapOf(
                            "name" to newName,
                            "phone" to newPhone
                        )
                    )
                    .addOnSuccessListener {
                        currentName = newName
                        currentPhone = newPhone
                        txtName.text = if (newName.isNotBlank()) newName else "Barber"
                        txtPhone.text = if (newPhone.isNotBlank()) {
                            "Phone: $newPhone"
                        } else {
                            "Phone: -"
                        }
                        Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, exception.message ?: "Update failed", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun formatAvailability(startMinutes: Int, endMinutes: Int, slotMinutes: Int): String {
        return "Working: ${formatTime(startMinutes)} - ${formatTime(endMinutes)} - $slotMinutes min slots"
    }

    private fun formatTime(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return String.format(Locale.getDefault(), "%02d:%02d", hours, mins)
    }

    private fun getLongSafe(
        doc: com.google.firebase.firestore.DocumentSnapshot,
        field: String,
        fallback: Long
    ): Long {
        val raw = doc.get(field)
        return when (raw) {
            is Number -> raw.toLong()
            is String -> raw.toLongOrNull() ?: fallback
            else -> fallback
        }
    }

}
