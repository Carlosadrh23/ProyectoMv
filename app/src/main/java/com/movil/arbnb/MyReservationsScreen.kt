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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReservationsScreen(
    onBack: () -> Unit,
    onNavigateTo: (Screen) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(2) } // Default to Cancelados as per request

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
                            "Logout" -> onNavigateTo(Screen.LOGIN)
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
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Próximos", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Pasados", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Cancelados", fontSize = 12.sp) }
                    )
                }
            }
        },
        bottomBar = { 
            HomeBottomNavigation(
                currentScreen = Screen.MY_RESERVATIONS,
                onNavigateTo = onNavigateTo
            ) 
        },
        containerColor = ArbnbTeal // Background color as seen in the screenshot
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    Text("No tienes viajes próximos.", color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
                1 -> {
                    Text("No tienes viajes pasados.", color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
                2 -> {
                    // Show cancelled travels
                    propertiesList.forEach { property ->
                        CancelledReservationCard(property = property)
                    }
                }
            }
        }
    }
}

@Composable
fun CancelledReservationCard(property: Property) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp), // More rounded as in screenshot
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
                    // Viaje Cancelado Button/Label
                    Button(
                        onClick = { /* Already cancelled */ },
                        modifier = Modifier.weight(1f).height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)), // Red
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Viaje Cancelado", color = Color.White, fontSize = 12.sp)
                    }
                    
                    // Contactar Button
                    Button(
                        onClick = { /* Contact host logic */ },
                        modifier = Modifier.weight(1f).height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ArbnbBlue),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Contactar", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
