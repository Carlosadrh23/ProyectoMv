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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.movil.arbnb.ui.theme.*
import com.movil.arbnb.data.UserRepository
import com.movil.arbnb.data.PropertyRepository

@Composable
fun MyPropertiesScreen(
    onBack: () -> Unit,
    onNavigateTo: (Screen) -> Unit
) {
    var isAddingProperty by remember { mutableStateOf(false) }
    var userProperties by remember { mutableStateOf<List<Property>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Alerts
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    
    val currentUser = UserRepository.currentUser

    fun refreshProperties() {
        isLoading = true
        currentUser?.let { user ->
            PropertyRepository.getPropertiesByHost(user.email) { list ->
                userProperties = list
                isLoading = false
            }
        } ?: run { isLoading = false }
    }

    LaunchedEffect(Unit) {
        refreshProperties()
    }

    Scaffold(
        topBar = {
            ArbnbTopAppBar(
                title = "Mis Propiedades",
                onNavigationIconClick = onBack,
                onMenuOptionClick = { option ->
                    when(option) {
                        "Perfil" -> onNavigateTo(Screen.PROFILE)
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
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ArbnbBlue)
                    }
                } else {
                    if (userProperties.isEmpty()) {
                        Text("No tienes propiedades registradas.", color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))
                    } else {
                        userProperties.forEach { property ->
                            MyPropertyItemCard(property)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { isAddingProperty = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ArbnbBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Añadir Nueva Propiedad", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                key(isAddingProperty) {
                    AddPropertyForm(
                        onCancel = { isAddingProperty = false },
                        onSaved = { 
                            showSuccessDialog = true
                        },
                        onError = {
                            showErrorDialog = true
                        }
                    )
                }
            }
        }
        
        if (showSuccessDialog) {
            PropertyAlert(
                message = "¡Datos guardados con exito!",
                isSuccess = true,
                onDismiss = { 
                    showSuccessDialog = false
                    isAddingProperty = false
                    refreshProperties()
                }
            )
        }
        
        if (showErrorDialog) {
            PropertyAlert(
                message = "¡Porfavor llena todas las casillas!",
                isSuccess = false,
                onDismiss = { showErrorDialog = false }
            )
        }
    }
}

@Composable
fun AddPropertyForm(onCancel: () -> Unit, onSaved: () -> Unit, onError: () -> Unit) {
    var tipo by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var zona by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var nochesMinimas by remember { mutableStateOf("1") }
    var descripcion by remember { mutableStateOf("") }
    var amenidadesStr by remember { mutableStateOf("") }
    
    var isSaving by remember { mutableStateOf(false) }

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

            MyPropertyInputField(label = "Tipo de alojamiento (ej. Casa, Depto)", value = tipo, onValueChange = { tipo = it })
            MyPropertyInputField(label = "Dirección", value = direccion, onValueChange = { direccion = it })
            MyPropertyInputField(label = "Zona", value = zona, onValueChange = { zona = it })
            MyPropertyInputField(label = "Ciudad", value = ciudad, onValueChange = { ciudad = it })
            MyPropertyInputField(label = "Estado", value = estado, onValueChange = { estado = it })
            MyPropertyInputField(label = "Precio por noche (MXN)", value = precio, onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) precio = it }, keyboardType = KeyboardType.Number)
            MyPropertyInputField(label = "Noches mínimas", value = nochesMinimas, onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) nochesMinimas = it }, keyboardType = KeyboardType.Number)
            MyPropertyInputField(label = "Descripción", value = descripcion, onValueChange = { descripcion = it })
            
            MyPropertyInputField(
                label = "Amenidades (separadas por coma)", 
                value = amenidadesStr, 
                onValueChange = { amenidadesStr = it }
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isSaving
                ) {
                    Text("Cancelar", color = Color.Black)
                }
                
                Button(
                    onClick = {
                        val trimmedTipo = tipo.trim()
                        val trimmedDireccion = direccion.trim()
                        val trimmedCiudad = ciudad.trim()
                        val trimmedEstado = estado.trim()
                        val trimmedPrecio = precio.trim()
                        val trimmedDesc = descripcion.trim()

                        if (trimmedTipo.isEmpty() || trimmedDireccion.isEmpty() || 
                            trimmedCiudad.isEmpty() || trimmedEstado.isEmpty() || 
                            trimmedPrecio.isEmpty() || trimmedDesc.isEmpty()) {
                            onError()
                        } else {
                            isSaving = true
                            val amenidadesList = amenidadesStr.split(",").map { it.trim() }.filter { it.isNotBlank() }
                            val property = Property(
                                tipo_alojamiento = trimmedTipo,
                                direccion = trimmedDireccion,
                                zona = zona.trim(),
                                ciudad = trimmedCiudad,
                                estado = trimmedEstado,
                                precio_noche = trimmedPrecio,
                                noches_minimas = nochesMinimas.toIntOrNull() ?: 1,
                                descripcion = trimmedDesc,
                                amenidades = amenidadesList,
                                anfitrion_id = UserRepository.currentUser?.email ?: ""
                            )
                            PropertyRepository.addProperty(property) { success, _ ->
                                isSaving = false
                                if (success) onSaved()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ArbnbBlue),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Publicar Propiedad", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun MyPropertyItemCard(property: Property) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Image(
                painter = painterResource(id = property.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text("${property.tipo_alojamiento} en ${property.ciudad}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(property.descripcion, fontSize = 12.sp, color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis)

                Spacer(modifier = Modifier.height(8.dp))

                property.amenidades.take(3).forEach { feature ->
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
                        colors = ButtonDefaults.buttonColors(containerColor = if (property.estado_publicacion == "Activo") SuccessGreen else Color.Gray),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                    ) {
                        Text(property.estado_publicacion, fontSize = 10.sp, color = Color.White)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    StaticRatingBar(rating = property.averageRating, size = 16.dp)
                }
            }
        }
    }
}

@Composable
fun PropertyAlert(
    message: String,
    isSuccess: Boolean,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.width(300.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A33))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isSuccess) {
                    Text("Alerta", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                Text(
                    message,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(if (isSuccess) Color(0xFF4CAF50) else Color.Red),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSuccess) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ArbnbBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Volver", color = Color.White)
                }
            }
        }
    }
}
