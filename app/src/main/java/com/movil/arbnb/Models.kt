package com.movil.arbnb

import androidx.compose.runtime.mutableStateListOf

data class Review(
    val userName: String,
    val rating: Int,
    val comment: String,
    val date: String = "Hoy"
)

data class Property(
    val id: Int,
    val title: String,
    val price: String,
    val rating: String,
    val imageRes: Int,
    val description: String = "Disfruta de una estancia inolvidable en este alojamiento con vistas increíbles y todas las comodidades que necesitas para tu viaje.",
    val reviews: List<Review> = emptyList()
)

val propertiesList = mutableStateListOf(
    Property(
        1, "Casa del Mar, La Paz BCS", "$2500 MXN por 2 noches", "5.0", android.R.drawable.ic_menu_gallery,
        reviews = listOf(Review("Juan Perez", 5, "Excelente ubicación y vista al mar."))
    ),
    Property(
        2, "Cabaña, Mazamitla", "$2900 MXN por 2 noches", "4.0", android.R.drawable.ic_menu_gallery,
        reviews = listOf(Review("Maria Garcia", 4, "Muy acogedora, perfecta para el frío."))
    ),
    Property(
        3, "Departamento en Guadalajara", "$3400 MXN por 1 noche", "3.0", android.R.drawable.ic_menu_gallery,
        reviews = listOf(Review("Carlos Lopez", 3, "Bien ubicado pero un poco ruidoso."))
    ),
    Property(
        4, "Departamento en cabos san lucas", "$2500 MXN por 2 noches", "5.0", android.R.drawable.ic_menu_gallery,
        reviews = listOf(Review("Ana Martinez", 5, "Increíble, volvería sin dudarlo."))
    )
)
