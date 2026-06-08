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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(
    property: Property,
    onBack: () -> Unit,
    onMenuOptionClick: (String) -> Unit,
    onNavigateTo: (Screen) -> Unit
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var currentProperty by remember { mutableStateOf(property) }

    Scaffold(
        topBar = {
            Column {
                ArbnbTopAppBar(
                    title = "Viajes",
                    onNavigationIconClick = onBack,
                    navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                    onMenuOptionClick = onMenuOptionClick,
                    onLogoClick = onBack // Logo returns to Home
                )
                SecondaryTabRow(
                    selectedTabIndex = 0,
                    containerColor = ArbnbTeal,
                    contentColor = Color.White,
                    indicator = {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(0),
                            color = Color.White
                        )
                    }
                ) {
                    Tab(selected = true, onClick = {}, text = { Text("Próximos", fontSize = 12.sp) })
                    Tab(selected = false, onClick = {}, text = { Text("Pasados", fontSize = 12.sp) })
                    Tab(selected = false, onClick = {}, text = { Text("Cancelados", fontSize = 12.sp) })
                }
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
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.2f), CircleShape)
                                .padding(4.dp)
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
                        
                        Button(
                            onClick = { showCancelDialog = true },
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("Cancelar Reservación", color = Color.White, fontSize = 12.sp)
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
                    com.movil.arbnb.data.PropertyRepository.updateProperty(updatedProperty) { success ->
                        if (success) {
                            currentProperty = updatedProperty
                        }
                    }
                }
            )
        }

        if (showCancelDialog) {
// ... existing dialogs ...
            CancelConfirmationDialog(
                onConfirm = {
                    showCancelDialog = false
                    showSuccessDialog = true
                },
                onDismiss = { showCancelDialog = false }
            )
        }

        if (showSuccessDialog) {
            CancelSuccessDialog(
                onDismiss = {
                    showSuccessDialog = false
                    onBack()
                }
            )
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
    var error by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().padding(bottom = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Deja un comentario:", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = comment,
                onValueChange = { 
                    comment = it
                    if (it.isNotEmpty()) error = null
                },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                placeholder = { Text("Escribe tu experiencia aquí...", fontSize = 14.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFEEEEEE),
                    unfocusedContainerColor = Color(0xFFEEEEEE),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = ArbnbBlue
                ),
                shape = RoundedCornerShape(8.dp)
            )
            
            if (error != null) {
                Text(error!!, color = Color.Red, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                InteractiveRatingBar(
                    rating = rating,
                    onRatingChange = { 
                        rating = it
                        error = null
                    }
                )
                Text(" ${rating.toDouble()}", fontSize = 14.sp)
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        if (comment.isBlank()) {
                            error = "Por favor escribe un comentario"
                        } else if (rating == 0) {
                            error = "Por favor selecciona una valoración"
                        } else {
                            val user = com.movil.arbnb.data.UserRepository.currentUser
                            val newReview = Review(
                                userName = user?.fullName ?: "Usuario Anónimo",
                                rating = rating,
                                comment = comment,
                                date = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
                            )
                            onReviewSent(newReview)
                            comment = ""
                            rating = 0
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.Gray),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Enviar Reseña", color = Color.Black, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun CancelConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ArbnbTealLight.copy(alpha = 0.95f)),
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "¿Desea cancelar su reservación?",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Confirmar", color = Color.White)
                    }
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancelar", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CancelSuccessDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ArbnbTealLight.copy(alpha = 0.95f)),
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "¡Se ha cancelado tu reservación con éxito!",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Volver", color = Color.White)
                }
            }
        }
    }
}
