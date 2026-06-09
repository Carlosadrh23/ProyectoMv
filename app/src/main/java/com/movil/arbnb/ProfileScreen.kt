package com.movil.arbnb

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.arbnb.ui.theme.*
import com.movil.arbnb.data.UserRepository

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onNavigateTo: (Screen) -> Unit
) {
    val user = UserRepository.currentUser
    var isEditing by remember { mutableStateOf(false) }
    var showHostDialog by remember { mutableStateOf(false) }

    // Form fields
    var fullName by remember { mutableStateOf(user?.fullName ?: "Invitado") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }
    var registrationDate by remember { mutableStateOf(user?.registrationDate ?: "2024-06-01") }
    
    if (showHostDialog) {
        AlertDialog(
            onDismissRequest = { showHostDialog = false },
            title = { Text("Convertirse en anfitrión") },
            text = { Text("Para añadir propiedades primero debes convertirte en anfitrión. ¿Deseas hacerlo ahora?") },
            confirmButton = {
                Button(
                    onClick = {
                        user?.let {
                            val updatedUser = it.copy(esAnfitrion = true)
                            UserRepository.updateUser(updatedUser) { success ->
                                if (success) {
                                    showHostDialog = false
                                    onNavigateTo(Screen.MY_PROPERTIES)
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ArbnbBlue)
                ) {
                    Text("Sí, quiero ser anfitrión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showHostDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            ArbnbTopAppBar(
                title = "Perfil",
                onNavigationIconClick = onBack,
                onMenuOptionClick = { option ->
                    when(option) {
                        "Propiedades" -> onNavigateTo(Screen.MY_PROPERTIES)
                        "Reservaciones" -> onNavigateTo(Screen.MY_RESERVATIONS)
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
                currentScreen = Screen.PROFILE,
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
            // Main Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header inside card
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF607D8B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(50.dp), tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(if (isEditing) "Añadir Nombre" else fullName, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text(if (isEditing) "Correo electronico" else email, color = Color.Gray, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    if (!isEditing) {
                        // Display Mode
                        ProfileInfoItem("Nombre completo:", fullName)
                        ProfileInfoItem("Correo electrónico:", email)
                        val maskedPhone = if (phone.length >= 2) {
                            "*" .repeat(phone.length - 2) + phone.takeLast(2)
                        } else {
                            phone
                        }
                        ProfileInfoItem("Teléfono:", maskedPhone)
                        ProfileInfoItem("Fecha de registro:", registrationDate)
                        ProfileInfoItem("Tipo de cuenta:", if (user?.esAnfitrion == true) "Anfitrión" else "Huésped")
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text("Estatus:", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = { isEditing = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp)
                        ) {
                            Text("Editar Perfil", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Edit Mode
                        ProfileEditItem(label = "Nombre completo:", value = fullName, onValueChange = { fullName = it })
                        ProfileEditItem(label = "Correo electrónico:", value = email, onValueChange = { email = it })
                        ProfileEditItem(label = "Telefono:", value = phone, onValueChange = { phone = it })
                        ProfileEditItem(label = "Fecha de registro:", value = registrationDate, onValueChange = { registrationDate = it }, isDate = true)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text("Estado:", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF00E676), RoundedCornerShape(20.dp))
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Text("Editar Perfil", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            
            if (isEditing) {
                Spacer(modifier = Modifier.height(40.dp))
                
                Button(
                    onClick = { 
                        val updatedUser = User(fullName, email, user?.password ?: "", phone, registrationDate, user?.esAnfitrion ?: false)
                        UserRepository.updateUser(updatedUser) { success ->
                            if (success) {
                                isEditing = false
                            }
                        }
                    },
                    modifier = Modifier
                        .width(150.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Guardar", color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun ProfileEditItem(label: String, value: String, onValueChange: (String) -> Unit, isDate: Boolean = false) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, fontSize = 14.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text("Añade texto en los campos", color = Color.Red, fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.weight(1f)) { innerTextField() }
                        if (isDate) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Text(value, fontSize = 14.sp)
        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
    }
}
