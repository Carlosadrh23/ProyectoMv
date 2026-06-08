package com.movil.arbnb

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.arbnb.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReservationScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    onNavigateTo: (Screen) -> Unit
) {
    var guestName by remember { mutableStateOf("") }
    var propertyName by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var totalAmount by remember { mutableStateOf("") }

    var guestNameError by remember { mutableStateOf<String?>(null) }
    var propertyNameError by remember { mutableStateOf<String?>(null) }
    var startDateError by remember { mutableStateOf<String?>(null) }
    var endDateError by remember { mutableStateOf<String?>(null) }
    var totalAmountError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        var isValid = true
        if (guestName.isBlank()) {
            guestNameError = "El nombre es obligatorio"
            isValid = false
        } else {
            guestNameError = null
        }

        if (propertyName.isBlank()) {
            propertyNameError = "La propiedad es obligatoria"
            isValid = false
        } else {
            propertyNameError = null
        }

        if (startDate.isBlank()) {
            startDateError = "Fecha obligatoria"
            isValid = false
        } else {
            startDateError = null
        }

        if (endDate.isBlank()) {
            endDateError = "Fecha obligatoria"
            isValid = false
        } else {
            endDateError = null
        }

        if (totalAmount.isBlank()) {
            totalAmountError = "El monto es obligatorio"
            isValid = false
        } else if (totalAmount.toDoubleOrNull() == null) {
            totalAmountError = "Monto inválido"
            isValid = false
        } else {
            totalAmountError = null
        }

        return isValid
    }

    Scaffold(
        topBar = {
            ArbnbTopAppBar(
                title = "Nueva Reservación",
                onNavigationIconClick = onBack,
                onMenuOptionClick = {},
                onLogoClick = onBack
            )
        },
        bottomBar = { 
            HomeBottomNavigation(
                currentScreen = Screen.ADD_RESERVATION,
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Registrar Reservación Externa",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ArbnbTeal
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Añade los datos del huésped para una de tus propiedades.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    MyPropertyInputField(
                        label = "Nombre del Huésped",
                        value = guestName,
                        onValueChange = { guestName = it; guestNameError = null },
                        isError = guestNameError != null,
                        errorMessage = guestNameError
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    MyPropertyInputField(
                        label = "Propiedad a asignar",
                        value = propertyName,
                        onValueChange = { propertyName = it; propertyNameError = null },
                        isError = propertyNameError != null,
                        errorMessage = propertyNameError
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            MyPropertyInputField(
                                label = "Fecha Inicio",
                                value = startDate,
                                onValueChange = { startDate = it; startDateError = null },
                                isError = startDateError != null,
                                errorMessage = startDateError
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            MyPropertyInputField(
                                label = "Fecha Fin",
                                value = endDate,
                                onValueChange = { endDate = it; endDateError = null },
                                isError = endDateError != null,
                                errorMessage = endDateError
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    MyPropertyInputField(
                        label = "Monto Total (MXN)",
                        value = totalAmount,
                        onValueChange = { totalAmount = it; totalAmountError = null },
                        isError = totalAmountError != null,
                        errorMessage = totalAmountError
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(
                        onClick = { if (validate()) onSuccess() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Confirmar Reservación", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onBack,
                modifier = Modifier.width(150.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.Black),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancelar", color = Color.Black)
            }
        }
    }
}
