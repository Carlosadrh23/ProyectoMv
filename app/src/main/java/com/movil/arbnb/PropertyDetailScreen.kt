package com.movil.arbnb

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import java.util.Locale

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
    
    // Obtenemos la propiedad directamente de la lista global para que sea reactiva
    val currentProperty = propertiesList.find { it.id == property.id } ?: property

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
                TabRow(
                    selectedTabIndex = 0,
                    containerColor = ArbnbTeal,
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[0]),
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
                            painter = painterResource(id = property.imageRes),
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
                            Column {
                                Text(text = currentProperty.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                                    Text(text = " ${currentProperty.rating} (${currentProperty.reviews.size} reseñas)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Icon(imageVector = Icons.Default.OpenInFull, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentProperty.description,
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text("Tu estancia incluye:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        val items = listOf("Wifi rápido", "Estacionamiento", "Cocina equipada", "TV por cable")
                        items.forEach { item ->
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
            
            ReviewSection(
                reviews = currentProperty.reviews,
                onAddReview = { newReview -> 
                    val index = propertiesList.indexOfFirst { it.id == currentProperty.id }
                    if (index != -1) {
                        val updatedReviews = propertiesList[index].reviews + newReview
                        val newAverage = updatedReviews.map { it.rating }.average()
                        propertiesList[index] = propertiesList[index].copy(
                            reviews = updatedReviews,
                            rating = String.format(Locale.US, "%.1f", newAverage)
                        )
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
fun ReviewSection(
    reviews: List<Review>,
    onAddReview: (Review) -> Unit
) {
    var comment by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(0) }
    var commentError by remember { mutableStateOf<String?>(null) }
    var ratingError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        var isValid = true
        if (comment.isBlank()) {
            commentError = "El comentario es obligatorio"
            isValid = false
        } else {
            commentError = null
        }

        if (rating == 0) {
            ratingError = "Selecciona una calificación"
            isValid = false
        } else {
            ratingError = null
        }
        return isValid
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 24.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Deja un comentario:", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = comment,
                    onValueChange = { 
                        comment = it
                        if (it.isNotBlank()) commentError = null 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFEEEEEE),
                        focusedContainerColor = Color(0xFFEEEEEE),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = ErrorRed
                    ),
                    shape = RoundedCornerShape(4.dp),
                    isError = commentError != null,
                    placeholder = { Text("Escribe tu experiencia aquí...", fontSize = 12.sp) }
                )

                if (commentError != null) {
                    Text(
                        text = commentError!!,
                        color = ErrorRed,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row {
                        repeat(5) { index ->
                            val starIndex = index + 1
                            Icon(
                                imageVector = if (starIndex <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Calificar con $starIndex estrellas",
                                tint = if (starIndex <= rating) Color(0xFFFFB300) else Color.Gray,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable { 
                                        rating = starIndex
                                        ratingError = null
                                    }
                            )
                        }
                    }
                    Text(
                        text = " ${rating.toDouble()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            if (validate()) {
                                onAddReview(Review("Tú", rating, comment))
                                comment = ""
                                rating = 0
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color.LightGray),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Enviar Reseña", color = Color.Black, fontSize = 12.sp)
                    }
                }
                if (ratingError != null) {
                    Text(
                        text = ratingError!!,
                        color = ErrorRed,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        if (reviews.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Reseñas de otros viajeros", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            reviews.forEach { review ->
                ReviewItem(review)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(32.dp).background(ArbnbTeal, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(review.userName.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(review.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(review.date, fontSize = 11.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.weight(1f))
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < review.rating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (index < review.rating) Color(0xFFFFB300) else Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(review.comment, fontSize = 13.sp, color = Color.DarkGray)
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
