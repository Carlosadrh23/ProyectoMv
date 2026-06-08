package com.movil.arbnb

data class Property(
    val id: Int,
    val title: String,
    val price: String,
    val rating: String,
    val imageRes: Int,
    val description: String = "Disfruta de una estancia inolvidable en este alojamiento con vistas increíbles y todas las comodidades que necesitas para tu viaje."
)

val propertiesList = listOf(
    Property(1, "Casa del Mar, La Paz BCS", "$2500 MXN por 2 noches", "4.9", android.R.drawable.ic_menu_gallery),
    Property(2, "Cabaña, Mazamitla", "$2900 MXN por 2 noches", "4.89", android.R.drawable.ic_menu_gallery),
    Property(3, "Departamento en Guadalajara", "$3400 MXN por 1 noche", "3.65", android.R.drawable.ic_menu_gallery),
    Property(4, "Departamento en cabos san lucas", "$2500 MXN por 2 noches", "4.89", android.R.drawable.ic_menu_gallery)
)
