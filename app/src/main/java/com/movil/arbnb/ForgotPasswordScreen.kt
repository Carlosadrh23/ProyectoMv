package com.movil.arbnb

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.AirportShuttle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.arbnb.ui.theme.ArbnbBlue
import com.movil.arbnb.ui.theme.DarkBackground
import com.movil.arbnb.ui.theme.InputBackground

@Composable
fun ForgotPasswordScreen(
    onCodeSent: () -> Unit,
    onBackToLogin: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        var isValid = true
        if (email.isBlank()) {
            emailError = "El correo es obligatorio"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Correo inválido"
            isValid = false
        } else {
            emailError = null
        }
        return isValid
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AirportShuttle,
                contentDescription = "Logo",
                tint = ArbnbBlue,
                modifier = Modifier.size(60.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¿Olvidaste tu contraseña?",
                color = ArbnbBlue,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ingresa tu correo para recuperar la contraseña, recibirás un código para realizar tu contraseña",
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            CustomInputField(
                label = "Correo electrónico:",
                value = email,
                onValueChange = { email = it; emailError = null },
                icon = Icons.Default.Email,
                isError = emailError != null,
                errorMessage = emailError
            )

            Text(
                text = "Enviar código",
                color = ArbnbBlue,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 8.dp)
                    .clickable { if (validate()) onCodeSent() }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Simulating the number pad from the image (simplified)
            NumberPad(onNumberClick = { /* Logic for code entry */ })

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Volver al inicio",
                color = ArbnbBlue,
                modifier = Modifier.clickable { onBackToLogin() }
            )
        }
    }
}

@Composable
fun NumberPad(onNumberClick: (String) -> Unit) {
    val numbers = listOf("1", "1", "1", "1", "1", "1", "1", "1", "1", "1") // As seen in image
    Column {
        for (i in 0 until 3) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (j in 0 until 3) {
                    NumberButton(numbers[i * 3 + j])
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            NumberButton(numbers[9])
        }
    }
}

@Composable
fun NumberButton(text: String) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .background(Color.White, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.Black, fontSize = 20.sp)
    }
}
