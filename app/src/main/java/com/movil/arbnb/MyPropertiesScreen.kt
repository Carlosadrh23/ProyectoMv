package com.movil.arbnb

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.arbnb.ui.theme.*

@Composable
fun MyPropertiesScreen(
    onBack: () -> Unit,
    onNavigateTo: (Screen) -> Unit
) {
    var isAddingProperty by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ArbnbTopAppBar(
                title = "Mis Propiedades",
                onNavigationIconClick = onBack,
                onMenuOptionClick = { option ->
                    when(option) {
                        "Perfil" -> onNavigateTo(Screen.PROFILE)
                        "Reservaciones" -> onNavigateTo(Screen.MY_RESERVATIONS)
                        "Logout" -> onNavigateTo(Screen.LOGIN)
                    }
                },
                onLogoClick = onBack
            )
        },
        bottomBar = { 
            HomeBottomNavigation(
                currentScreen = Screen.MY_PROPERTIES,
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
            if (!isAddingProperty) {
                // List View (Matching image 1/2)
                MyPropertyItemCard("Cabo house, Los Cabos B.C.S", android.R.drawable.ic_menu_gallery)
                MyPropertyItemCard("Maya house, Mazatlán Sinaloa", android.R.drawable.ic_menu_gallery)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { isAddingProperty = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = ArbnbTeal),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Añadir Nueva Propiedad", color = Color.White)
                }
            } else {
                // Add Property View (Matching image 3)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("¡Agrega Imágenes de tu propiedad!", fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .background(Color(0xFFEEEEEE), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(60.dp), tint = Color.Gray)
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        MyPropertyInputField(label = "Nombre de la propiedad", value = "")
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Checklist
                        val features = listOf("Wifi", "Cocina", "3 habitaciones con cama individual", "2 baños", "Alberca")
                        features.forEach { feature ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Checkbox(checked = false, onCheckedChange = {})
                                Text(feature, fontSize = 14.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                            ) {
                                Text("Marcar Disponibilidad", fontSize = 10.sp, color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("0.0", fontSize = 14.sp)
                            repeat(5) { Icon(Icons.Default.StarBorder, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = { isAddingProperty = false },
                            modifier = Modifier.fillMaxWidth(0.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Guardar", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyPropertyItemCard(title: String, imageRes: Int) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Disfruta de las maravillosas experiencias con las que cuenta Cabo house, ven y pasa las vacaciones de tu vida.", fontSize = 12.sp, color = Color.Gray)
                
                Spacer(modifier = Modifier.height(8.dp))

                val features = listOf("Estacionamiento audiovisual", "Wifi", "Cocina")
                features.forEach { feature ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(12.dp).background(Color.Black))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(feature, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                    ) {
                        Text("Disponible", fontSize = 10.sp, color = Color.White)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text("0.0", fontSize = 14.sp)
                    repeat(5) { Icon(Icons.Default.StarBorder, contentDescription = null, modifier = Modifier.size(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun MyPropertyInputField(label: String, value: String, onValueChange: (String) -> Unit = {}) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.LightGray
            )
        )
    }
}
