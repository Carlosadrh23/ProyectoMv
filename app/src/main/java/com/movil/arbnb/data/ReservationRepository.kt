package com.movil.arbnb.data

import com.google.firebase.firestore.FirebaseFirestore
import com.movil.arbnb.Reservation

object ReservationRepository {
    private val db = FirebaseFirestore.getInstance()
    private val reservationsCollection = db.collection("reservations")

    fun addReservation(reservation: Reservation, onResult: (Boolean, String?) -> Unit) {
        val newDoc = reservationsCollection.document()
        val reservationWithId = reservation.copy(id = newDoc.id)
        newDoc.set(reservationWithId)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    fun getReservationsByUser(userId: String, onResult: (List<Reservation>) -> Unit) {
        reservationsCollection
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.toObjects(Reservation::class.java)
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun getReservationsByProperty(propertyId: String, onResult: (List<Reservation>) -> Unit) {
        reservationsCollection
            .whereEqualTo("propertyId", propertyId)
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.toObjects(Reservation::class.java)
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun updateReservationStatus(reservationId: String, newStatus: String, onResult: (Boolean) -> Unit) {
        reservationsCollection.document(reservationId)
            .update("status", newStatus)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}
