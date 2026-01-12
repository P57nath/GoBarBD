package com.example.gobarbd.core.data.repository

import com.example.gobarbd.R
import com.example.gobarbd.core.data.model.Barbershop
import com.example.gobarbd.core.data.model.Review
import com.google.firebase.firestore.FirebaseFirestore

object BarbershopRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val placeholderImages = listOf(
        R.drawable.shop1,
        R.drawable.shop2,
        R.drawable.shop3,
        R.drawable.shop4
    )

    fun fetchAllShops(
        onSuccess: (List<Barbershop>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        firestore.collection("shops")
            .get()
            .addOnSuccessListener { snapshot ->
                val shops = snapshot.documents.mapIndexed { index, doc ->
                    val name = doc.getString("name") ?: ""
                    val location = doc.getString("address") ?: ""
                    val rating = (doc.getDouble("ratingAvg") ?: 0.0).toFloat()
                    val ratingCount = (doc.getLong("ratingCount") ?: 0L).toInt()
                    val isOpen = doc.getBoolean("isOpen") ?: true
                    val description = doc.getString("description") ?: ""
                    val distance = (doc.getDouble("distanceKm") ?: 0.0).toFloat()
                    val categories = (doc.get("categories") as? List<*>)?.mapNotNull {
                        it?.toString()
                    } ?: emptyList()
                    val imageRes = placeholderImages[index % placeholderImages.size]

                    Barbershop(
                        name = name,
                        location = location,
                        rating = rating,
                        imageResource = imageRes,
                        distance = distance,
                        categories = categories,
                        id = doc.id,
                        ratingCount = ratingCount,
                        isOpen = isOpen,
                        description = description
                    )
                }

                if (shops.isEmpty()) {
                    onSuccess(getSeedData())
                } else {
                    onSuccess(shops)
                }
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }

    fun getNearestFrom(shops: List<Barbershop>): List<Barbershop> {
        return shops.sortedBy { it.distance }
    }

    fun getRecommendedFrom(shops: List<Barbershop>): List<Barbershop> {
        return shops.sortedByDescending { it.rating }
    }

    fun fetchShopById(
        shopId: String,
        onSuccess: (Barbershop) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (shopId.isBlank()) {
            onSuccess(getSeedData().first())
            return
        }

        firestore.collection("shops")
            .document(shopId)
            .get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("name") ?: "Barbershop"
                val location = doc.getString("address") ?: ""
                val rating = (doc.getDouble("ratingAvg") ?: 0.0).toFloat()
                val ratingCount = (doc.getLong("ratingCount") ?: 0L).toInt()
                val isOpen = doc.getBoolean("isOpen") ?: true
                val description = doc.getString("description") ?: ""
                val distance = (doc.getDouble("distanceKm") ?: 0.0).toFloat()
                val categories = (doc.get("categories") as? List<*>)?.mapNotNull {
                    it?.toString()
                } ?: emptyList()
                val imageRes = placeholderImages.first()

                onSuccess(
                    Barbershop(
                        name = name,
                        location = location,
                        rating = rating,
                        imageResource = imageRes,
                        distance = distance,
                        categories = categories,
                        id = doc.id,
                        ratingCount = ratingCount,
                        isOpen = isOpen,
                        description = description
                    )
                )
            }
            .addOnFailureListener { exception -> onError(exception) }
    }

    fun fetchReviews(
        shopId: String,
        onSuccess: (List<Review>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (shopId.isBlank()) {
            onSuccess(getSeedReviews())
            return
        }

        firestore.collection("reviews")
            .whereEqualTo("shopId", shopId)
            .get()
            .addOnSuccessListener { snapshot ->
                val reviews = snapshot.documents.map { doc ->
                    Review(
                        id = doc.id,
                        userName = doc.getString("userName") ?: "Customer",
                        rating = (doc.getDouble("rating") ?: 5.0).toFloat(),
                        comment = doc.getString("comment") ?: ""
                    )
                }
                onSuccess(if (reviews.isEmpty()) getSeedReviews() else reviews)
            }
            .addOnFailureListener { exception -> onError(exception) }
    }

    fun getSeedData(): List<Barbershop> = listOf(
        Barbershop(
            "Alana Barbershop - Haircut massage & Spa",
            "Banguntapan (5 km)",
            4.5f,
            R.drawable.shop1,
            5f,
            listOf("Basic haircut", "Massage")
        ),
        Barbershop(
            "Hercha Barbershop - Haircut & Styling",
            "Jalan Kaliurang (8 km)",
            5.0f,
            R.drawable.shop2,
            8f,
            listOf("Basic haircut", "Styling")
        ),
        Barbershop(
            "Barberking - Haircut styling & massage",
            "Jogja Expo Centre (12 km)",
            4.5f,
            R.drawable.shop3,
            12f,
            listOf("Basic haircut", "Massage")
        ),
        Barbershop(
            "Gentleman Barber Studio",
            "Seturan (6 km)",
            4.7f,
            R.drawable.shop4,
            6f,
            listOf("Haircut", "Beard trim")
        ),
        Barbershop(
            "Urban Cut Barbershop",
            "Gejayan (4 km)",
            4.6f,
            R.drawable.shop1,
            4f,
            listOf("Haircut", "Styling")
        ),
        Barbershop(
            "Classic Men Barber",
            "Maguwoharjo (9 km)",
            4.4f,
            R.drawable.shop2,
            9f,
            listOf("Haircut", "Massage")
        )
    )

    fun getSeedReviews(): List<Review> = listOf(
        Review(id = "r1", userName = "Alan Cartwright", rating = 4.0f, comment = "Good service."),
        Review(id = "r2", userName = "Robert Gleicher", rating = 5.0f, comment = "Great result."),
        Review(id = "r3", userName = "Sergio Wilderman", rating = 4.0f, comment = "Affordable and good.")
    )
}
