package com.movil.arbnb

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AirportShuttle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.arbnb.ui.theme.*
import com.movil.arbnb.data.UserRepository

@Composable
fun RegistrationScreen(
    onRegisterSuccess: () -> Unit = {},
    onBackToLogin: () -> Unit = {},
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }
    var registrationError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    fun handleRegister() {
        if (phone.length != 10 || !phone.all { it.isDigit() }) {
            registrationError = "El teléfono debe tener 10 dígitos"
            return
        }
        if (password != confirmPassword) {
            registrationError = "Las contraseñas no coinciden"
            return
        }
        
        isLoading = true
        // Default esAnfitrion to false during registration
        val newUser = User(fullName, email, password, phone, esAnfitrion = false)
        UserRepository.register(newUser) { success, error ->
            isLoading = false
            if (success) {
                onRegisterSuccess()
            } else {
                registrationError = error ?: "Error al registrar usuario"
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                text = "Crea tu cuenta",
                color = ArbnbBlue,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (registrationError != null) {
                Text(
                    text = registrationError!!,
                    color = ErrorRed,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

            CustomInputField(label = "Nombre completo:", value = fullName, onValueChange = { fullName = it; registrationError = null }, icon = Icons.Default.Person)
            Spacer(modifier = Modifier.height(12.dp))
            CustomInputField(label = "Correo electrónico:", value = email, onValueChange = { email = it; registrationError = null }, icon = Icons.Default.Email, keyboardType = KeyboardType.Email)
            Spacer(modifier = Modifier.height(12.dp))
            CustomInputField(label = "Teléfono:", value = phone, onValueChange = { if (it.length <= 10) phone = it; registrationError = null }, icon = Icons.Default.Phone, keyboardType = KeyboardType.Phone)
            Spacer(modifier = Modifier.height(12.dp))
            CustomInputField(label = "Contraseña:", value = password, onValueChange = { password = it; registrationError = null }, icon = Icons.Default.Lock, isPassword = true)
            Spacer(modifier = Modifier.height(12.dp))
            CustomInputField(label = "Confirmar contraseña:", value = confirmPassword, onValueChange = { confirmPassword = it; registrationError = null }, icon = Icons.Default.Lock, isPassword = true)

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    colors = CheckboxDefaults.colors(checkedColor = ArbnbBlue, uncheckedColor = Color.White)
                )
                Text(
                    text = "Aceptar los Términos de uso y Política de privacidad",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { handleRegister() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArbnbBlue),
                enabled = (termsAccepted && fullName.isNotBlank() && email.isNotBlank() && phone.length == 10 && password.length >= 6 && confirmPassword.isNotBlank() && !isLoading)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Crear cuenta", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row {
                Text(text = "¿Ya tienes cuenta? ", color = Color.White, fontSize = 14.sp)
                Text(
                    text = "Inicia sesión",
                    color = ArbnbBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { if (!isLoading) onBackToLogin() }
                )
            }
        }
    }
}
