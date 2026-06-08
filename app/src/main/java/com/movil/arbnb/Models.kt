package com.movil.arbnb

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
    val estado_publicacion: String = "Activo",
    val anfitrion_id: String = "",
    val rating: String = "5.0",
    val imageRes: Int = android.R.drawable.ic_menu_gallery
)

data class User(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val registrationDate: String = "2024-06-01",
    val esAnfitrion: Boolean = false
)

val propertiesList = emptyList<Property>()
