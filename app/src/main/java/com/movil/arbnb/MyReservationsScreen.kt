package com.movil.arbnb

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.movil.arbnb.ui.theme.ArbnbBackground
import com.movil.arbnb.data.UserRepository
import com.movil.arbnb.data.ReservationRepository
import com.movil.arbnb.data.PropertyRepository

@Composable
fun MyReservationsScreen(
    onBack: () -> Unit,
    onNavigateTo: (Screen) -> Unit
) {
    var reservations by remember { mutableStateOf<List<Reservation>>(emptyList()) }
    var reservedProperties by remember { mutableStateOf<List<Property>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val currentUser = UserRepository.currentUser

    LaunchedEffect(currentUser?.email) {
        currentUser?.let { user ->
            ReservationRepository.getReservationsByUser(user.email) { resList ->
                reservations = resList.filter { it.status != "Cancelado" }
                if (reservations.isEmpty()) {
                    isLoading = false
                } else {
                    PropertyRepository.getAllActiveProperties { allProps ->
                        reservedProperties = allProps.filter { prop -> 
                            reservations.any { it.propertyId == prop.id } 
                        }
                        isLoading = false
                    }
                }
            }
        } ?: run { isLoading = false }
    }

    Scaffold(
        topBar = {
            ArbnbTopAppBar(
                title = "Mis Reservaciones",
                onNavigationIconClick = onBack,
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
        },
        bottomBar = { 
            HomeBottomNavigation(
                currentScreen = Screen.MY_RESERVATIONS,
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
                .padding(16.dp)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (reservedProperties.isEmpty()) {
                Text("No tienes reservaciones pendientes.", color = Color.Gray)
            } else {
                reservedProperties.forEach { property ->
                    PropertyCard(property = property, onClick = {
                        // For now we just show the card
                    })
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
