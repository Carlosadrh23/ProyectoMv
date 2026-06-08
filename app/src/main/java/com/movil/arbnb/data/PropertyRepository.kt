package com.movil.arbnb.data

import com.google.firebase.firestore.FirebaseFirestore
import com.movil.arbnb.Property
import com.movil.arbnb.Reservation

object PropertyRepository {
    private val db = FirebaseFirestore.getInstance()
    private val propertiesCollection = db.collection("properties")

    fun addProperty(property: Property, onResult: (Boolean, String?) -> Unit) {
        val newDoc = propertiesCollection.document()
        val propertyWithId = property.copy(id = newDoc.id)
        newDoc.set(propertyWithId)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    fun getAllActiveProperties(onResult: (List<Property>) -> Unit) {
        propertiesCollection
            .whereEqualTo("estado_publicacion", "Activo")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.toObjects(Property::class.java)
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun getPropertiesByHost(hostId: String, onResult: (List<Property>) -> Unit) {
        propertiesCollection
            .whereEqualTo("anfitrion_id", hostId)
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.toObjects(Property::class.java)
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun updateProperty(property: Property, onResult: (Boolean) -> Unit) {
        propertiesCollection.document(property.id)
            .set(property)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // Reservation Logic (Point 1)
    fun addReservation(reservation: Reservation, onResult: (Boolean, String?) -> Unit) {
        val reservationsCollection = db.collection("reservations")
        val newDoc = reservationsCollection.document()
        val reservationWithId = reservation.copy(id = newDoc.id)
        newDoc.set(reservationWithId)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    fun getReservationsByUser(userId: String, onResult: (List<Reservation>) -> Unit) {
        db.collection("reservations")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.toObjects(Reservation::class.java)
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }
}
