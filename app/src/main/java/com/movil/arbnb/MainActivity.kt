package com.movil.arbnb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.movil.arbnb.ui.theme.ArbnbTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArbnbTheme {
                var currentScreen by remember { mutableStateOf(Screen.LOGIN) }
                var selectedProperty by remember { mutableStateOf<Property?>(null) }

                when (currentScreen) {
                    Screen.LOGIN -> LoginScreen(
                        onLoginClick = { currentScreen = Screen.HOME },
                        onRegisterClick = { currentScreen = Screen.REGISTRATION },
                        onForgotPasswordClick = { currentScreen = Screen.FORGOT_PASSWORD }
                    )
                    Screen.FORGOT_PASSWORD -> ForgotPasswordScreen(
                        onCodeSent = { currentScreen = Screen.RESTORE_PASSWORD },
                        onBackToLogin = { currentScreen = Screen.LOGIN }
                    )
                    Screen.RESTORE_PASSWORD -> RestorePasswordScreen(
                        onRestoreSuccess = { currentScreen = Screen.LOGIN }
                    )
                    Screen.REGISTRATION -> RegistrationScreen(
                        onRegisterSuccess = { currentScreen = Screen.SUCCESS_REGISTRATION },
                        onBackToLogin = { currentScreen = Screen.LOGIN }
                    )
                    Screen.SUCCESS_REGISTRATION -> SuccessAlert(
                        message = "¡Cuenta creada con éxito!",
                        onContinue = { currentScreen = Screen.LOGIN }
                    )
                    Screen.HOME -> HomeScreen(
                        onPropertyClick = { property ->
                            selectedProperty = property
                            currentScreen = Screen.PROPERTY_DETAIL
                        },
                        onMenuOptionClick = { option ->
                            currentScreen = when(option) {
                                "Perfil" -> Screen.PROFILE
                                "Propiedades" -> Screen.MY_PROPERTIES
                                "Reservaciones" -> Screen.MY_RESERVATIONS
                                "Logout" -> Screen.LOGIN
                                else -> Screen.HOME
                            }
                        },
                        onNavigateTo = { screen -> currentScreen = screen }
                    )
                    Screen.PROPERTY_DETAIL -> {
                        selectedProperty?.let { property ->
                            PropertyDetailScreen(
                                property = property,
                                onBack = { currentScreen = Screen.HOME },
                                onMenuOptionClick = { option ->
                                    currentScreen = when(option) {
                                        "Perfil" -> Screen.PROFILE
                                        "Propiedades" -> Screen.MY_PROPERTIES
                                        "Reservaciones" -> Screen.MY_RESERVATIONS
                                        "Logout" -> Screen.LOGIN
                                        else -> Screen.HOME
                                    }
                                },
                                onNavigateTo = { screen -> currentScreen = screen }
                            )
                        }
                    }
                    Screen.PROFILE -> ProfileScreen(
                        onBack = { currentScreen = Screen.HOME },
                        onNavigateTo = { screen -> currentScreen = screen }
                    )
                    Screen.MY_PROPERTIES -> MyPropertiesScreen(
                        onBack = { currentScreen = Screen.HOME },
                        onNavigateTo = { screen -> currentScreen = screen }
                    )
                    Screen.MY_RESERVATIONS -> MyReservationsScreen(
                        onBack = { currentScreen = Screen.HOME },
                        onNavigateTo = { screen -> currentScreen = screen }
                    )
                    Screen.ADD_RESERVATION -> AddReservationScreen(
                        onBack = { currentScreen = Screen.PROFILE },
                        onSuccess = { currentScreen = Screen.PROFILE },
                        onNavigateTo = { screen -> currentScreen = screen }
                    )
                    Screen.MESSAGES -> MessagesScreen(
                        onBack = { currentScreen = Screen.HOME },
                        onChatClick = { _ -> currentScreen = Screen.CHAT_DETAIL },
                        onMenuOptionClick = { option ->
                            currentScreen = when(option) {
                                "Perfil" -> Screen.PROFILE
                                "Propiedades" -> Screen.MY_PROPERTIES
                                "Reservaciones" -> Screen.MY_RESERVATIONS
                                "Logout" -> Screen.LOGIN
                                else -> Screen.HOME
                            }
                        },
                        onNavigateTo = { screen -> currentScreen = screen }
                    )
                    Screen.CHAT_DETAIL -> ChatDetailScreen(
                        chatName = "Angel",
                        onBack = { currentScreen = Screen.MESSAGES },
                        onNavigateTo = { screen -> currentScreen = screen }
                    )
                    Screen.FAVORITES -> FavoritesScreen(
                        onPropertyClick = { property ->
                            selectedProperty = property
                            currentScreen = Screen.PROPERTY_DETAIL
                        },
                        onMenuOptionClick = { option ->
                            currentScreen = when(option) {
                                "Perfil" -> Screen.PROFILE
                                "Propiedades" -> Screen.MY_PROPERTIES
                                "Reservaciones" -> Screen.MY_RESERVATIONS
                                "Logout" -> Screen.LOGIN
                                else -> Screen.HOME
                            }
                        },
                        onNavigateTo = { screen -> currentScreen = screen }
                    )
                }
            }
        }
    }
}
