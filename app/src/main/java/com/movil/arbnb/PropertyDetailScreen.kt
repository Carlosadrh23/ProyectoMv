package com.movil.arbnb

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
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
import com.movil.arbnb.data.UserRepository
import com.movil.arbnb.data.ReservationRepository
import com.movil.arbnb.data.FavoriteRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(
    property: Property,
    onBack: () -> Unit,
    onMenuOptionClick: (String) -> Unit,
    onNavigateTo: (Screen) -> Unit,
    onConfirmReservation: (Reservation) -> Unit,
    onContactHost: (String, String) -> Unit
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showReserveDialog by remember { mutableStateOf(false) }
    
    val currentUser = UserRepository.currentUser
    val isHost = currentUser?.email == property.anfitrion_id
    val isFavorite = FavoriteRepository.favoriteIds.contains(property.id)
    
    var userReservation by remember { mutableStateOf<Reservation?>(null) }
    var isLoadingReservation by remember { mutableStateOf(true) }

    LaunchedEffect(property.id, currentUser?.email) {
        currentUser?.let { user ->
            ReservationRepository.getReservationsByUser(user.email) { list ->
                userReservation = list.find { it.propertyId == property.id && it.status != "Cancelado" }
                isLoadingReservation = false
            }
        } ?: run {
            isLoadingReservation = false
        }
    }

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
                            painter = painterResource(id = property.imageRes),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = {
                                currentUser?.let { user ->
                                    FavoriteRepository.toggleFavorite(user.email, property.id)
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.2f), CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (isFavorite) Color.Red else Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${property.tipo_alojamiento} en ${property.ciudad}", 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 16.sp
                            )
                            Icon(imageVector = Icons.Default.OpenInFull, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = property.descripcion,
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text("Tu estancia incluye:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        property.amenidades.forEach { item ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = item, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        if (isHost) {
                            Text(
                                text = "Esta es tu propiedad",
                                color = ArbnbTeal,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        } else if (!isLoadingReservation) {
                            if (userReservation != null) {
                                Button(
                                    onClick = { showCancelDialog = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Cancelar Reservación", color = Color.White)
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                OutlinedButton(
                                    onClick = { 
                                        currentUser?.let { user ->
                                            com.movil.arbnb.data.ChatRepository.getOrCreateChat(
                                                myId = user.email,
                                                otherId = property.anfitrion_id,
                                                myName = user.fullName,
                                                otherName = "Anfitrión (${property.tipo_alojamiento})"
                                            ) { chatId ->
                                                onContactHost(chatId, "Anfitrión (${property.tipo_alojamiento})")
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    border = BorderStroke(1.dp, ArbnbTeal),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = ArbnbTeal)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Contactar al anfitrión", color = ArbnbTeal)
                                }
                            } else {
                                Button(
                                    onClick = { showReserveDialog = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = ArbnbBlue),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Reservar ahora", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (userReservation != null) {
                ReviewSection()
            } else if (!isHost) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("Reserva para poder dejar un comentario", color = Color.Gray, fontSize = 14.sp)
                }
            }
        }

        if (showReserveDialog) {
            ReservationFormDialog(
                property = property,
                onDismiss = { showReserveDialog = false },
                onContinue = { reservation ->
                    showReserveDialog = false
                    onConfirmReservation(reservation)
                }
            )
        }

        if (showCancelDialog) {
            CancelConfirmationDialog(
                onConfirm = {
                    userReservation?.let { reservation ->
                        ReservationRepository.updateReservationStatus(reservation.id, "Cancelado") { success ->
                            if (success) {
                                showCancelDialog = false
                                showSuccessDialog = true
                            }
                        }
                    }
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
fun ReviewSection() {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().padding(bottom = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Deja un comentario:", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color(0xFFEEEEEE), RoundedCornerShape(4.dp)))
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row {
                    repeat(5) { Icon(Icons.Default.StarBorder, contentDescription = null, modifier = Modifier.size(16.dp)) }
                }
                Text(" 0.0", fontSize = 14.sp)
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.Gray), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
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

@Composable
fun ReservationFormDialog(
    property: Property,
    onDismiss: () -> Unit,
    onContinue: (Reservation) -> Unit
) {
    var checkIn by remember { mutableStateOf("") }
    var checkOut by remember { mutableStateOf("") }
    var guests by remember { mutableIntStateOf(1) }
    
    val currentUser = UserRepository.currentUser

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$${property.precio_noche} MXN",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("por noche", color = Color.Gray, fontSize = 14.sp)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color.LightGray)
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Llegada", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = checkIn,
                            onValueChange = { checkIn = it },
                            placeholder = { Text("dd/mm/aaaa", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Salida", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = checkOut,
                            onValueChange = { checkOut = it },
                            placeholder = { Text("dd/mm/aaaa", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Text("Huéspedes", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.LightGray),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { if (guests > 1) guests-- },
                            modifier = Modifier.border(1.dp, Color.LightGray, CircleShape).size(32.dp)
                        ) {
                            Text("-", fontSize = 20.sp)
                        }
                        Text(text = guests.toString(), fontSize = 16.sp)
                        IconButton(
                            onClick = { guests++ },
                            modifier = Modifier.border(1.dp, Color.LightGray, CircleShape).size(32.dp)
                        ) {
                            Text("+", fontSize = 20.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = {
                        val reservation = Reservation(
                            propertyId = property.id,
                            userId = currentUser?.email ?: "",
                            startDate = checkIn,
                            endDate = checkOut,
                            guests = guests,
                            totalAmount = property.precio_noche // Simple total for now
                        )
                        onContinue(reservation)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ArbnbTeal),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Continuar", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
