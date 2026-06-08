package com.movil.arbnb

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AirportShuttle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.arbnb.ui.theme.ArbnbBlue
import com.movil.arbnb.ui.theme.DarkBackground
import com.movil.arbnb.ui.theme.ErrorRed
import com.movil.arbnb.ui.theme.InputBackground

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

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

        if (password.length < 6) {
            passwordError = "Mínimo 6 caracteres"
            isValid = false
        } else {
            passwordError = null
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
            Spacer(modifier = Modifier.height(40.dp))

            // Logo
            Icon(
                imageVector = Icons.Default.AirportShuttle,
                contentDescription = "Logo",
                tint = ArbnbBlue,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Bienvenido de nuevo",
                color = ArbnbBlue,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "\"Descubre dónde quieres que sea tu próxima aventura\"",
                color = ArbnbBlue.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Correo electrónico:", color = Color.White, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; emailError = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(InputBackground, RoundedCornerShape(8.dp)),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.Black) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ArbnbBlue,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = ArbnbBlue,
                        errorBorderColor = ErrorRed
                    ),
                    shape = RoundedCornerShape(8.dp),
                    isError = emailError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )
                if (emailError != null) {
                    Text(text = emailError!!, color = ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Contraseña:", color = Color.White, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; passwordError = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(InputBackground, RoundedCornerShape(8.dp)),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Black) },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ArbnbBlue,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = ArbnbBlue,
                        errorBorderColor = ErrorRed
                    ),
                    shape = RoundedCornerShape(8.dp),
                    isError = passwordError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
                if (passwordError != null) {
                    Text(text = passwordError!!, color = ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
                }
            }

            Text(
                text = "¿Olvidaste tu contraseña?",
                color = ArbnbBlue,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 8.dp)
                    .clickable { onForgotPasswordClick() }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Login Button
            Button(
                onClick = { if (validate()) onLoginClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArbnbBlue)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Iniciar sesión", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row {
                Text(text = "¿No tienes cuenta? ", color = Color.White, fontSize = 14.sp)
                Text(
                    text = "Regístrate aquí",
                    color = ArbnbBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onRegisterClick() }
                )
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
