package com.movil.arbnb

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.movil.arbnb.ui.theme.ArbnbBackground

@Composable
fun MyReservationsScreen(
    onBack: () -> Unit,
    onNavigateTo: (Screen) -> Unit
) {
    Scaffold(
        topBar = {
            ArbnbTopAppBar(
                title = "Mis Reservaciones",
                onNavigationIconClick = onBack,
                onMenuOptionClick = { option ->
                    when(option) {
                        "Perfil" -> onNavigateTo(Screen.PROFILE)
                        "Propiedades" -> onNavigateTo(Screen.MY_PROPERTIES)
                        "Logout" -> onNavigateTo(Screen.LOGIN)
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
            Text("No tienes reservaciones pendientes.", color = Color.Gray)

            // Reusing PropertyCard for layout consistency
            propertiesList.take(1).forEach { property ->
                PropertyCard(property = property, onClick = {})
            }
        }
    }
}
