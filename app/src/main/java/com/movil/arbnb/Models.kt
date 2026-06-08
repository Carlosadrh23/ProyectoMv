package com.movil.arbnb

import androidx.compose.runtime.mutableStateListOf

data class Review(
    val userName: String,
    val rating: Int,
    val comment: String,
    val date: String = "Hoy"
)

data class ChatMessage(
    val text: String,
    val isFromMe: Boolean,
    val time: String = "Ahora"
)

data class MessagePreview(
    val id: Int,
    val initials: String,
    val name: String,
    val snippet: String,
    val time: String,
    val avatarColor: androidx.compose.ui.graphics.Color,
    val unreadCount: Int = 0,
    val messages: List<ChatMessage> = emptyList()
)

data class Property(
    val id: Int,
    val title: String,
    val price: String,
    val rating: String,
    val imageRes: Int,
    val description: String = "Disfruta de una estancia inolvidable en este alojamiento con vistas increíbles y todas las comodidades que necesitas para tu viaje.",
    val reviews: List<Review> = emptyList(),
    val isFavorite: Boolean = false
)

val messagesList = mutableStateListOf(
    MessagePreview(1, "SR", "Sofia R.", "¡Bienvenido! El código de acceso...", "10:32", androidx.compose.ui.graphics.Color(0xFF64B5F6), 2, 
        listOf(ChatMessage("Hola", false), ChatMessage("¡Bienvenido! El código de acceso es 1234", false))),
    MessagePreview(2, "MG", "Miguel G.", "Claro, el check-in es a las 3 p. m.", "Ayer", androidx.compose.ui.graphics.Color(0xFF81C784), 0,
        listOf(ChatMessage("¿A qué hora puedo llegar?", true), ChatMessage("Claro, el check-in es a las 3 p. m.", false))),
    MessagePreview(3, "AL", "Ana L.", "¿Necesitas más toallas? Con g...", "Lun", androidx.compose.ui.graphics.Color(0xFFBA68C8)),
    MessagePreview(4, "RV", "Roberto V.", "Gracias por tu estancia, espero...", "Dom", androidx.compose.ui.graphics.Color(0xFFDCE775)),
    MessagePreview(5, "CH", "Carmen H.", "Perfecto te espero a las 2 p. m.", "Sáb", androidx.compose.ui.graphics.Color(0xFFE57373))
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
