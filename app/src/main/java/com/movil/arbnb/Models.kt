package com.movil.arbnb

import androidx.compose.runtime.mutableStateListOf

data class User(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val registrationDate: String = "2024-06-01",
    val esAnfitrion: Boolean = false
)

data class Review(
    val userName: String = "",
    val rating: Int = 5,
    val comment: String = "",
    val date: String = "Hoy"
)

data class Reservation(
    val id: String = "",
    val propertyId: String = "",
    val userId: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val guests: Int = 1,
    val totalAmount: String = "",
    val status: String = "Próximo" // Próximo, Pasado, Cancelado
)

data class Chat(
    val id: String = "",
    val participantIds: List<String> = emptyList(),
    val participantNames: Map<String, String> = emptyMap(),
    val lastMessage: String = "",
    val lastTimestamp: Long = 0
)

data class Message(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0
)

data class Property(
    val id: String = "",
    val tipo_alojamiento: String = "",
    val direccion: String = "",
    val zona: String = "",
    val ciudad: String = "",
    val estado: String = "",
    val precio_noche: String = "",
    val noches_minimas: Int = 1,
    val descripcion: String = "",
    val amenidades: List<String> = emptyList(),
    val anfitrion_id: String = "",
    val estado_publicacion: String = "Activo",
    val imageRes: Int = android.R.drawable.ic_menu_gallery,
    val reviews: List<Review> = emptyList()
) {
    val averageRating: Double
        get() = if (reviews.isEmpty()) 0.0 else reviews.map { it.rating }.average()
}

val propertiesList = mutableStateListOf(
    Property(
        id = "1",
        tipo_alojamiento = "Casa",
        ciudad = "La Paz BCS",
        precio_noche = "1250",
        descripcion = "Casa del Mar, excelente ubicación y vista al mar.",
        imageRes = android.R.drawable.ic_menu_gallery,
        reviews = listOf(Review("Juan Perez", 5, "Excelente ubicación y vista al mar."))
    ),
    Property(
        id = "2",
        tipo_alojamiento = "Cabaña",
        ciudad = "Mazamitla",
        precio_noche = "1450",
        descripcion = "Muy acogedora, perfecta para el frío.",
        imageRes = android.R.drawable.ic_menu_gallery,
        reviews = listOf(Review("Maria Garcia", 4, "Muy acogedora, perfecta para el frío."))
    )
)
