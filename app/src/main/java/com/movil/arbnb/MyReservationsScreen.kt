package com.movil.arbnb

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.movil.arbnb.ui.theme.*
import com.movil.arbnb.data.PropertyRepository
import com.movil.arbnb.data.UserRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReservationsScreen(
    onBack: () -> Unit,
    onNavigateTo: (Screen) -> Unit,
    onContactHost: (String, String) -> Unit = { _, _ -> }
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var reservations by remember { mutableStateOf<List<Reservation>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    val currentUser = UserRepository.currentUser

    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            PropertyRepository.getReservationsByUser(user.email) { list ->
                reservations = list
                isLoading = false
            }
        } ?: run { isLoading = false }
    }

    val filteredReservations = when (selectedTab) {
        0 -> reservations.filter { it.status == "Próximo" || it.status == "Confirmado" }
        1 -> reservations.filter { it.status == "Pasado" }
        2 -> reservations.filter { it.status == "Cancelado" }
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            Column {
                ArbnbTopAppBar(
                    title = "Viajes",
                    onNavigationIconClick = onBack,
                    navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                    onMenuOptionClick = { option ->
                        when(option) {
                            "Perfil" -> onNavigateTo(Screen.PROFILE)
                            "Propiedades" -> onNavigateTo(Screen.MY_PROPERTIES)
                            "Logout" -> {
                                UserRepository.logout()
                                onNavigateTo(Screen.LOGIN)
                            }
                        }
                    },
                    onLogoClick = onBack
                )
                SecondaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = ArbnbTeal,
                    contentColor = Color.White,
                    indicator = {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(selectedTab),
                            color = Color.White
                        )
                    }
                ) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Próximos", fontSize = 12.sp) })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Pasados", fontSize = 12.sp) })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Cancelados", fontSize = 12.sp) })
                }
            }
        },
        bottomBar = { 
            HomeBottomNavigation(
                currentScreen = Screen.MY_RESERVATIONS,
                onNavigateTo = onNavigateTo
            ) 
        },
        containerColor = ArbnbTeal
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (filteredReservations.isEmpty()) {
                Text(
                    text = "No tienes viajes en esta categoría.",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
                )
            } else {
                filteredReservations.forEach { reservation ->
                    if (selectedTab == 2) {
                        val property = propertiesList.firstOrNull { it.id == reservation.propertyId } ?: propertiesList[0]
                        CancelledReservationCard(
                            property = property.copy(descripcion = "Reservación en ${property.ciudad}"),
                            onContact = {
                                val user = UserRepository.currentUser
                                if (user != null) {
                                    com.movil.arbnb.data.ChatRepository.getOrCreateChat(
                                        user.email, user.fullName,
                                        property.anfitrion_id, "Anfitrión"
                                    ) { chatId ->
                                        onContactHost(chatId, "Anfitrión")
                                    }
                                }
                            }
                        )
                    } else {
                        ReservationCard(reservation)
                    }
                }
            }
        }
    }
}

@Composable
fun ReservationCard(reservation: Reservation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Reservación #${reservation.id.takeLast(6)}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Desde: ${reservation.startDate}", fontSize = 14.sp)
            Text("Hasta: ${reservation.endDate}", fontSize = 14.sp)
            Text("Monto: $${reservation.totalAmount} MXN", fontWeight = FontWeight.Bold, color = ArbnbBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Estado: ${reservation.status}", color = if(reservation.status == "Cancelado") Color.Red else SuccessGreen)
        }
    }
}

@Composable
fun CancelledReservationCard(property: Property, onContact: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = property.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = property.descripcion,
                    fontSize = 14.sp,
                    color = Color.Black,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f).height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Viaje Cancelado", color = Color.White, fontSize = 12.sp)
                    }
                    
                    Button(
                        onClick = onContact,
                        modifier = Modifier.weight(1f).height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ArbnbBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Contactar", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
