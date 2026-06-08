package com.movil.arbnb

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.arbnb.ui.theme.*
import com.movil.arbnb.data.ReservationRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmPayScreen(
    property: Property,
    reservation: Reservation,
    onBack: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmar y pagar", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF7F7F7)
    ) { padding ->
        Row(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Left Column: Payment Form
            Card(
                modifier = Modifier.width(350.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Paga", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Agrega una forma de pago", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = "Tarjeta de crédito o débito",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { cardNumber = it },
                        placeholder = { Text("Número de tarjeta") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = expiryDate,
                            onValueChange = { expiryDate = it },
                            placeholder = { Text("Caducidad (MM/YY)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = cvv,
                            onValueChange = { cvv = it },
                            placeholder = { Text("CVV") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = zipCode,
                        onValueChange = { zipCode = it },
                        placeholder = { Text("Código postal") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
                            isProcessing = true
                            ReservationRepository.addReservation(reservation) { success, _ ->
                                isProcessing = false
                                if (success) onPaymentSuccess()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222)),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Listo", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Right Column: Summary
            Card(
                modifier = Modifier.width(350.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = property.imageRes),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(property.tipo_alojamiento, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(property.ciudad, fontSize = 14.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Detalles del precio", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Fechas", color = Color.Gray)
                        Text("${reservation.startDate} - ${reservation.endDate}")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Huéspedes", color = Color.Gray)
                        Text("${reservation.guests}")
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("$${reservation.totalAmount} MXN", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    val cancellationDate = remember(reservation.startDate) {
                        try {
                            val parts = reservation.startDate.split("/")
                            if (parts.size == 3) {
                                val day = parts[0].toInt()
                                val month = parts[1]
                                val year = parts[2]
                                if (day > 1) {
                                    "${day - 1}/$month/$year"
                                } else {
                                    // Simplified: if it's the 1st, just say "un día antes"
                                    "un día antes de su llegada"
                                }
                            } else {
                                "un día antes de la llegada"
                            }
                        } catch (_: Exception) {
                            "un día antes de la llegada"
                        }
                    }

                    Text("Cancelación gratuita", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Si cancelas antes del $cancellationDate, recibirás reembolso completo.", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}
