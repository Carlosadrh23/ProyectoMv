package com.movil.arbnb

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.arbnb.ui.theme.*
import androidx.compose.ui.unit.sp
import com.movil.arbnb.ui.theme.ArbnbBlue
import com.movil.arbnb.ui.theme.ArbnbTeal
import com.movil.arbnb.ui.theme.InputBackground

@Composable
fun CustomInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = Color.White, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(InputBackground, RoundedCornerShape(4.dp)),
            leadingIcon = { Icon(icon, contentDescription = null, tint = Color.Black) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ArbnbBlue,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                errorBorderColor = ErrorRed
            ),
            shape = RoundedCornerShape(4.dp),
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = ErrorRed,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun SuccessAlert(
    message: String,
    onContinue: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1A2A33)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = message,
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(80.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(45.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArbnbBlue)
            ) {
                Text(text = "Iniciar sesión", color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArbnbTopAppBar(
    title: String,
    onNavigationIconClick: (() -> Unit)? = null,
    navigationIcon: ImageVector? = null,
    onMenuOptionClick: (String) -> Unit,
    onLogoClick: () -> Unit,
    showLogo: Boolean = true
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = if (navigationIcon != null) 0.dp else 48.dp) // Offset for burger menu if no nav icon
                    .clickable { onLogoClick() }
            ) {
                Text(title, color = Color.White, fontSize = 18.sp)
                if (showLogo) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.AirportShuttle,
                        contentDescription = null,
                        tint = ArbnbBlue,
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.White, CircleShape)
                            .padding(2.dp)
                    )
                }
            }
        },
        navigationIcon = {
            if (navigationIcon != null && onNavigationIconClick != null) {
                IconButton(onClick = onNavigationIconClick) {
                    Icon(navigationIcon, contentDescription = "Navigation", tint = Color.White)
                }
            }
        },
        actions = {
            Box {
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier
                        .background(Color.White)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                ) {
                    DropdownMenuItem(
                        text = { Text("Mi Perfil", color = Color.Black, fontWeight = FontWeight.Bold) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ArbnbTeal) },
                        onClick = {
                            showMenu = false
                            onMenuOptionClick("Perfil")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Mis Propiedades", color = Color.Black, fontWeight = FontWeight.Bold) },
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = ArbnbTeal) },
                        onClick = {
                            showMenu = false
                            onMenuOptionClick("Propiedades")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Mis Reservaciones", color = Color.Black, fontWeight = FontWeight.Bold) },
                        leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = ArbnbTeal) },
                        onClick = {
                            showMenu = false
                            onMenuOptionClick("Reservaciones")
                        }
                    )
                    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
                    DropdownMenuItem(
                        text = { Text("Cerrar sesión", color = Color.Red, fontWeight = FontWeight.Bold) },
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.Red) },
                        onClick = {
                            showMenu = false
                            onMenuOptionClick("Logout")
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = ArbnbTeal)
    )
}
