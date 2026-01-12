package com.example.gobarbd.core.data.repository

import com.example.gobarbd.core.data.model.Barber
import com.example.gobarbd.core.data.model.Booking
import com.example.gobarbd.core.data.model.BookingRequest
import com.example.gobarbd.core.data.model.Service
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

object BookingRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun fetchServices(
        shopId: String,
        onSuccess: (List<Service>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (shopId.isBlank()) {
            onSuccess(getSeedServices())
            return
        }

        firestore.collection("shops")
            .document(shopId)
            .collection("services")
            .whereEqualTo("isActive", true)
            .get()
            .addOnSuccessListener { snapshot ->
                val services = snapshot.documents.map { doc ->
                    Service(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        durationMin = (doc.getLong("durationMin") ?: 30L).toInt(),
                        price = doc.getDouble("price") ?: 0.0
                    )
                }
                onSuccess(if (services.isEmpty()) getSeedServices() else services)
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }

    fun fetchBarbers(
        shopId: String,
        onSuccess: (List<Barber>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (shopId.isBlank()) {
            onSuccess(getSeedBarbers())
            return
        }

        firestore.collection("shops")
            .document(shopId)
            .collection("barbers")
            .whereEqualTo("active", true)
            .get()
            .addOnSuccessListener { snapshot ->
                val barbers = snapshot.documents.map { doc ->
                    Barber(
                        id = doc.id,
                        displayName = doc.getString("displayName") ?: "Barber"
                    )
                }
                onSuccess(if (barbers.isEmpty()) getSeedBarbers() else barbers)
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }

    fun createBooking(
        request: BookingRequest,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val data = hashMapOf(
            "customerId" to request.customerId,
            "shopId" to request.shopId,
            "shopName" to request.shopName,
            "shopLocation" to request.shopLocation,
            "barberId" to request.barberId,
            "serviceId" to request.serviceId,
            "startTime" to request.startTimeMillis,
            "endTime" to request.endTimeMillis,
            "paymentMethod" to request.paymentMethod,
            "paymentStatus" to "PENDING",
            "status" to request.status,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        firestore.collection("bookings")
            .add(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception) }
    }

    fun fetchCustomerBookings(
        customerId: String,
        onSuccess: (List<Booking>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        firestore.collection("bookings")
            .whereEqualTo("customerId", customerId)
            .get()
            .addOnSuccessListener { snapshot ->
                val bookings = snapshot.documents.mapIndexed { index, doc ->
                    val status = doc.getString("status") ?: "PENDING"
                    Booking(
                        id = doc.id,
                        shopName = doc.getString("shopName") ?: "Barbershop",
                        shopLocation = doc.getString("shopLocation") ?: "",
                        rating = (doc.getDouble("rating") ?: 0.0).toFloat(),
                        status = status,
                        imageRes = getSeedImages()[index % getSeedImages().size]
                    )
                }
                onSuccess(if (bookings.isEmpty()) getSeedBookings() else bookings)
            }
            .addOnFailureListener { exception -> onError(exception) }
    }

    fun getSeedServices(): List<Service> = listOf(
        Service(id = "s1", name = "Basic Haircut", durationMin = 30, price = 200.0),
        Service(id = "s2", name = "Haircut + Beard", durationMin = 45, price = 300.0),
        Service(id = "s3", name = "Premium Style", durationMin = 60, price = 450.0)
    )

    fun getSeedBarbers(): List<Barber> = listOf(
        Barber(id = "b1", displayName = "Alex"),
        Barber(id = "b2", displayName = "Rafi"),
        Barber(id = "b3", displayName = "Nayeem")
    )

    private fun getSeedImages(): List<Int> = listOf(
        com.example.gobarbd.R.drawable.shop1,
        com.example.gobarbd.R.drawable.shop2,
        com.example.gobarbd.R.drawable.shop3,
        com.example.gobarbd.R.drawable.shop4
    )

    fun getSeedBookings(): List<Booking> = listOf(
        Booking(
            id = "bk1",
            shopName = "Varcity Barbershop",
            shopLocation = "Condongcatur (10 km)",
            rating = 4.5f,
            status = "ACTIVE",
            imageRes = com.example.gobarbd.R.drawable.shop1
        ),
        Booking(
            id = "bk2",
            shopName = "Twinsky Monkey Barber",
            shopLocation = "Jl Taman Siswa (8 km)",
            rating = 5.0f,
            status = "COMPLETED",
            imageRes = com.example.gobarbd.R.drawable.shop2
        )
    )
}
