package com.movil.arbnb.data

import com.google.firebase.firestore.FirebaseFirestore
import com.movil.arbnb.Property

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
}
