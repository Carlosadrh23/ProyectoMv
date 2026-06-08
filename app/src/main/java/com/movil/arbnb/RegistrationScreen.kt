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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.arbnb.ui.theme.*

@Composable
fun RegistrationScreen(
    onRegisterSuccess: () -> Unit = {},
    onBackToLogin: () -> Unit = {},
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }

    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var termsError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        var isValid = true
        
        if (fullName.isBlank()) {
            fullNameError = "El nombre es obligatorio"
            isValid = false
        } else {
            fullNameError = null
        }

        if (email.isBlank()) {
            emailError = "El correo es obligatorio"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Correo inválido"
            isValid = false
        } else {
            emailError = null
        }

        if (password.length < 6) {
            passwordError = "Mínimo 6 caracteres"
            isValid = false
        } else {
            passwordError = null
        }

        if (confirmPassword != password) {
            confirmPasswordError = "Las contraseñas no coinciden"
            isValid = false
        } else {
            confirmPasswordError = null
        }

        if (!termsAccepted) {
            termsError = "Debes aceptar los términos"
            isValid = false
        } else {
            termsError = null
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
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

            CustomInputField(
                label = "Nombre completo:",
                value = fullName,
                onValueChange = { fullName = it; fullNameError = null },
                icon = Icons.Default.Person,
                isError = fullNameError != null,
                errorMessage = fullNameError
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            CustomInputField(
                label = "Correo electrónico:",
                value = email,
                onValueChange = { email = it; emailError = null },
                icon = Icons.Default.Email,
                keyboardType = KeyboardType.Email,
                isError = emailError != null,
                errorMessage = emailError
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            CustomInputField(
                label = "Contraseña:",
                value = password,
                onValueChange = { password = it; passwordError = null },
                icon = Icons.Default.Lock,
                isPassword = true,
                isError = passwordError != null,
                errorMessage = passwordError
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            CustomInputField(
                label = "Confirmar contraseña:",
                value = confirmPassword,
                onValueChange = { confirmPassword = it; confirmPasswordError = null },
                icon = Icons.Default.Lock,
                isPassword = true,
                isError = confirmPasswordError != null,
                errorMessage = confirmPasswordError
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it; termsError = null },
                    colors = CheckboxDefaults.colors(checkedColor = ArbnbBlue, uncheckedColor = Color.White)
                )
                Text(
                    text = "Aceptar los Términos de uso y Política de privacidad",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            if (termsError != null) {
                Text(text = termsError!!, color = ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(start = 12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { if (validate()) onRegisterSuccess() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArbnbBlue)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Crear cuenta", color = Color.White, fontWeight = FontWeight.Bold)
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
                    modifier = Modifier.clickable { onBackToLogin() }
                )
            }
        }
    }
}
