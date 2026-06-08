package com.movil.arbnb

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.arbnb.data.PropertyRepository
import com.movil.arbnb.ui.theme.ArbnbBlue
import com.movil.arbnb.ui.theme.ArbnbTeal
import com.movil.arbnb.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmPayScreen(
    property: Property,
    reservation: Reservation,
    onBack: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    var isProcessing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmar y Pagar", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ArbnbTeal)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Resumen de tu viaje", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(property.tipo_alojamiento, fontWeight = FontWeight.Bold)
                    Text(property.ciudad, color = Color.Gray)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Fechas")
                        Text("${reservation.startDate} - ${reservation.endDate}")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total (MXN)", fontWeight = FontWeight.Bold)
                        Text("$${reservation.totalAmount}", fontWeight = FontWeight.Bold, color = ArbnbBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    isProcessing = true
                    PropertyRepository.addReservation(reservation) { success, _ ->
                        isProcessing = false
                        if (success) {
                            onPaymentSuccess()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArbnbBlue),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Confirmar Pago", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
