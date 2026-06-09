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
import com.movil.arbnb.data.UserRepository
import com.movil.arbnb.data.ReservationRepository
import com.movil.arbnb.data.PropertyRepository
import com.movil.arbnb.data.ChatRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReservationsScreen(
    onBack: () -> Unit,
    onNavigateTo: (Screen) -> Unit,
    onContactHost: (String, String) -> Unit = { _, _ -> }
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var reservations by remember { mutableStateOf<List<Reservation>>(emptyList()) }
    var allProperties by remember { mutableStateOf<List<Property>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var resToCancel by remember { mutableStateOf<String?>(null) }
    
    val currentUser = UserRepository.currentUser

    fun loadData() {
        isLoading = true
        PropertyRepository.getAllActiveProperties { properties ->
            allProperties = properties
            currentUser?.let { user ->
                ReservationRepository.getReservationsByUser(user.email) { list ->
                    reservations = list
                    isLoading = false
                }
            } ?: run { isLoading = false }
        }
    }

    LaunchedEffect(Unit) {
        loadData()
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
        containerColor = Color(0xFFF7F7F7)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ArbnbTeal)
                }
            } else if (filteredReservations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No tienes viajes en esta categoría.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        // Header
                        Row(
                            modifier = Modifier
                                .background(Color(0xFFF9F9F9))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableHeaderItem("Propiedad", 200.dp)
                            TableHeaderItem("Fechas", 200.dp)
                            TableHeaderItem("Huéspedes", 100.dp)
                            TableHeaderItem("Total", 120.dp)
                            TableHeaderItem("Estado", 120.dp)
                            if (selectedTab == 0) TableHeaderItem("Acciones", 200.dp)
                        }
                        
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                        filteredReservations.forEach { reservation ->
                            val property = allProperties.find { it.id == reservation.propertyId }
                            ReservationRow(
                                reservation = reservation,
                                property = property,
                                showActions = selectedTab == 0,
                                onContact = {
                                    property?.let { p ->
                                        ChatRepository.getOrCreateChat(
                                            currentUser?.email ?: "", currentUser?.fullName ?: "Usuario",
                                            p.anfitrion_id, "Anfitrión"
                                        ) { chatId ->
                                            onContactHost(chatId, "Anfitrión")
                                        }
                                    }
                                },
                                onCancel = {
                                    resToCancel = reservation.id
                                    showCancelDialog = true
                                }
                            )
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
        
        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = { Text("¿Cancelar reservación?") },
                text = { Text("Si cancelas, se te reembolsará el dinero a tu cuenta.") },
                confirmButton = {
                    TextButton(onClick = {
                        resToCancel?.let { id ->
                            ReservationRepository.updateReservationStatus(id, "Cancelado") { success ->
                                if (success) {
                                    showCancelDialog = false
                                    showSuccessDialog = true
                                    loadData()
                                }
                            }
                        }
                    }) { Text("Sí, cancelar") }
                },
                dismissButton = {
                    TextButton(onClick = { showCancelDialog = false }) { Text("No") }
                }
            )
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = { Text("Éxito") },
                text = { Text("Reservación cancelada. Se te reembolsará el dinero a tu cuenta en un plazo de 3 a 5 días hábiles.") },
                confirmButton = {
                    Button(onClick = { showSuccessDialog = false }) { Text("Aceptar") }
                }
            )
        }
    }
}

@Composable
fun TableHeaderItem(text: String, width: androidx.compose.ui.unit.Dp) {
    Text(
        text = text,
        modifier = Modifier.width(width),
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Gray
    )
}

@Composable
fun ReservationRow(
    reservation: Reservation,
    property: Property?,
    showActions: Boolean,
    onContact: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Propiedad
        Column(modifier = Modifier.width(200.dp)) {
            Text(text = property?.tipo_alojamiento ?: "Cargando...", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = property?.ciudad ?: "", fontSize = 12.sp, color = Color.Gray)
        }

        // Fechas
        Text(
            text = "${reservation.startDate} - ${reservation.endDate}",
            modifier = Modifier.width(200.dp),
            fontSize = 14.sp
        )

        // Huéspedes
        Text(
            text = reservation.guests.toString(),
            modifier = Modifier.width(100.dp),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        // Total
        Text(
            text = "$${reservation.totalAmount} MXN",
            modifier = Modifier.width(120.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        // Estado
        Box(
            modifier = Modifier
                .width(120.dp)
                .padding(end = 16.dp)
        ) {
            val bgColor = when(reservation.status) {
                "Cancelado" -> Color(0xFFFFEBEE)
                "Confirmado", "Próximo" -> Color(0xFFE8F5E9)
                else -> Color(0xFFF5F5F5)
            }
            val textColor = when(reservation.status) {
                "Cancelado" -> Color(0xFFC62828)
                "Confirmado", "Próximo" -> Color(0xFF2E7D32)
                else -> Color.Gray
            }
            Surface(
                color = bgColor,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = reservation.status.lowercase(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Acciones
        if (showActions) {
            Row(
                modifier = Modifier.width(200.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onContact,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0F2F1)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Contactar", color = Color(0xFF00695C), fontSize = 12.sp)
                }
                Button(
                    onClick = onCancel,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Cancelar", color = Color(0xFFC62828), fontSize = 12.sp)
                }
            }
        }
    }
}
