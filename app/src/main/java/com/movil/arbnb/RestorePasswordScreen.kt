package com.movil.arbnb

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirportShuttle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.arbnb.ui.theme.ArbnbBlue
import com.movil.arbnb.ui.theme.DarkBackground
import com.movil.arbnb.ui.theme.InputBackground

@Composable
fun RestorePasswordScreen(
    onRestoreSuccess: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        var isValid = true
        if (newPassword.length < 6) {
            newPasswordError = "Mínimo 6 caracteres"
            isValid = false
        } else {
            newPasswordError = null
        }

        if (confirmPassword != newPassword) {
            confirmPasswordError = "Las contraseñas no coinciden"
            isValid = false
        } else {
            confirmPasswordError = null
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
                text = "Restaura tu contraseña",
                color = ArbnbBlue,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            CustomInputField(
                label = "Nueva contraseña:",
                value = newPassword,
                onValueChange = { newPassword = it; newPasswordError = null },
                icon = Icons.Default.Lock,
                isPassword = true,
                isError = newPasswordError != null,
                errorMessage = newPasswordError
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomInputField(
                label = "Confirmar contraseña:",
                value = confirmPassword,
                onValueChange = { confirmPassword = it; confirmPasswordError = null },
                icon = Icons.Default.Lock,
                isPassword = true,
                isError = confirmPasswordError != null,
                errorMessage = confirmPasswordError
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { if (validate()) onRestoreSuccess() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArbnbBlue)
            ) {
                Text(text = "Confirmar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
