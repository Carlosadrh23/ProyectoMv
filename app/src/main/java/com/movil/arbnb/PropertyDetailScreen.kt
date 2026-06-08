package com.movil.arbnb

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.movil.arbnb.ui.theme.*
import com.movil.arbnb.data.PropertyRepository
import com.movil.arbnb.data.UserRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(
    property: Property,
    onBack: () -> Unit,
    onMenuOptionClick: (String) -> Unit,
    onNavigateTo: (Screen) -> Unit,
    onContactHost: (String, String) -> Unit = { _, _ -> },
    onConfirmReservation: (Reservation) -> Unit = {}
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var currentProperty by remember { mutableStateOf(property) }
    var showReserveSuccess by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                ArbnbTopAppBar(
                    title = "Detalle",
                    onNavigationIconClick = onBack,
                    navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                    onMenuOptionClick = onMenuOptionClick,
                    onLogoClick = onBack
                )
            }
        },
        bottomBar = { 
            HomeBottomNavigation(
                currentScreen = Screen.PROPERTY_DETAIL,
                onNavigateTo = onNavigateTo
            ) 
        },
        containerColor = ArbnbBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column {
                    Box {
                        Image(
                            painter = painterResource(id = currentProperty.imageRes),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${currentProperty.tipo_alojamiento} en ${currentProperty.ciudad}", 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 16.sp
                            )
                            StaticRatingBar(rating = currentProperty.averageRating)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentProperty.descripcion,
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text("Tu estancia incluye:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        currentProperty.amenidades.forEach { item ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = item, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val user = UserRepository.currentUser
                                    if (user != null) {
                                        val reservation = Reservation(
                                            propertyId = currentProperty.id,
                                            userId = user.email,
                                            startDate = "2024-06-15",
                                            endDate = "2024-06-20",
                                            totalAmount = currentProperty.precio_noche,
                                            status = "Próximo"
                                        )
                                        onConfirmReservation(reservation)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = ArbnbBlue),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Reservar Ahora", color = Color.White, fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    val user = UserRepository.currentUser
                                    if (user != null) {
                                        com.movil.arbnb.data.ChatRepository.getOrCreateChat(
                                            user.email, user.fullName,
                                            currentProperty.anfitrion_id, "Anfitrión"
                                        ) { chatId ->
                                            onContactHost(chatId, "Anfitrión")
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = ArbnbTeal),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Contactar", color = Color.White, fontSize = 12.sp)
                            }
                            
                            Button(
                                onClick = { showCancelDialog = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Cancelar", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (currentProperty.reviews.isNotEmpty()) {
                Text(
                    "Reseñas (${currentProperty.reviews.size})",
                    modifier = Modifier.padding(horizontal = 24.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                currentProperty.reviews.forEach { review ->
                    ReviewItem(review)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ReviewSection(
                onReviewSent = { newReview ->
                    val updatedReviews = currentProperty.reviews + newReview
                    val updatedProperty = currentProperty.copy(reviews = updatedReviews)
                    PropertyRepository.updateProperty(updatedProperty) { success ->
                        if (success) currentProperty = updatedProperty
                    }
                }
            )
        }

        if (showCancelDialog) {
            CancelConfirmationDialog(
                onConfirm = {
                    showCancelDialog = false
                    showSuccessDialog = true
                },
                onDismiss = { showCancelDialog = false }
            )
        }

        if (showReserveSuccess) {
            Dialog(onDismissRequest = { showReserveSuccess = false }) {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(60.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("¡Reservación realizada!", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = { showReserveSuccess = false; onBack() }) {
                            Text("Aceptar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(review.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                StaticRatingBar(rating = review.rating.toDouble(), size = 12.dp)
                Spacer(modifier = Modifier.weight(1f))
                Text(review.date, fontSize = 11.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(review.comment, fontSize = 13.sp)
        }
    }
}

@Composable
fun ReviewSection(onReviewSent: (Review) -> Unit) {
    var comment by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(0) }
    
    Card(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Deja un comentario:", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                placeholder = { Text("Escribe tu experiencia...") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                InteractiveRatingBar(rating = rating, onRatingChange = { rating = it })
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        if (comment.isNotBlank() && rating > 0) {
                            onReviewSent(Review(UserRepository.currentUser?.fullName ?: "Usuario", rating, comment))
                            comment = ""; rating = 0
                        }
                    }
                ) { Text("Enviar") }
            }
        }
    }
}

@Composable
fun CancelConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Cancelar reservación?") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Sí") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("No") } }
    )
}

@Composable
fun CancelSuccessDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Éxito") },
        text = { Text("Reservación cancelada") },
        confirmButton = { Button(onClick = onDismiss) { Text("Aceptar") } }
    )
}
