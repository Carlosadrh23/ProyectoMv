package com.movil.arbnb.data

import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.mutableStateListOf

object FavoriteRepository {
    private val db = FirebaseFirestore.getInstance()
    private val favoritesCollection = db.collection("favorites")
    
    val favoriteIds = mutableStateListOf<String>()

    fun toggleFavorite(userId: String, propertyId: String) {
        if (favoriteIds.contains(propertyId)) {
            removeFavorite(userId, propertyId)
        } else {
            addFavorite(userId, propertyId)
        }
    }

    private fun addFavorite(userId: String, propertyId: String) {
        val favorite = hashMapOf(
            "userId" to userId,
            "propertyId" to propertyId
        )
        favoritesCollection.add(favorite)
            .addOnSuccessListener {
                if (!favoriteIds.contains(propertyId)) favoriteIds.add(propertyId)
            }
    }

    private fun removeFavorite(userId: String, propertyId: String) {
        favoritesCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("propertyId", propertyId)
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    doc.reference.delete()
                }
                favoriteIds.remove(propertyId)
            }
    }

    fun loadFavorites(userId: String) {
        favoritesCollection
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                favoriteIds.clear()
                val ids = snapshot.documents.mapNotNull { it.getString("propertyId") }
                favoriteIds.addAll(ids)
            }
    }
}
