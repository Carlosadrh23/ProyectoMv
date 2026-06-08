package com.movil.arbnb.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.movil.arbnb.User

object UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
    var currentUser: User? = null

    fun register(user: User, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid != null) {
                    val userMap = hashMapOf(
                        "nombre" to user.fullName,
                        "email" to user.email,
                        "telefono" to user.phone,
                        "registrationDate" to user.registrationDate,
                        "es_anfitrion" to user.esAnfitrion
                    )
                    db.collection("users").document(uid).set(userMap)
                        .addOnSuccessListener {
                            currentUser = user
                            onResult(true, null)
                        }
                        .addOnFailureListener {
                            onResult(false, it.message)
                        }
                } else {
                    onResult(false, "Error al obtener ID de usuario")
                }
            }
            .addOnFailureListener {
                onResult(false, it.message)
            }
    }

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid != null) {
                    db.collection("users").document(uid).get()
                        .addOnSuccessListener { document ->
                            val map = document.data
                            currentUser = User(
                                fullName = map?.get("nombre") as? String ?: "",
                                email = map?.get("email") as? String ?: "",
                                phone = map?.get("telefono") as? String ?: "",
                                registrationDate = map?.get("registrationDate") as? String ?: "2024-06-01",
                                esAnfitrion = map?.get("es_anfitrion") as? Boolean ?: false
                            )
                            onResult(true, null)
                        }
                        .addOnFailureListener {
                            onResult(false, it.message)
                        }
                } else {
                    onResult(false, "Error al obtener ID de usuario")
                }
            }
            .addOnFailureListener {
                onResult(false, it.message)
            }
    }

    fun logout() {
        auth.signOut()
        currentUser = null
    }

    fun updateUser(updatedUser: User, onResult: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            val userMap = hashMapOf(
                "nombre" to updatedUser.fullName,
                "email" to updatedUser.email,
                "telefono" to updatedUser.phone,
                "registrationDate" to updatedUser.registrationDate,
                "es_anfitrion" to updatedUser.esAnfitrion
            )
            db.collection("users").document(uid).update(userMap as Map<String, Any>)
                .addOnSuccessListener {
                    currentUser = updatedUser
                    onResult(true)
                }
                .addOnFailureListener {
                    onResult(false)
                }
        } else {
            onResult(false)
        }
    }
}
