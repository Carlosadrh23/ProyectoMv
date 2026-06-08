package com.movil.arbnb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.movil.arbnb.ui.theme.ArbnbTheme
import com.movil.arbnb.data.UserRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArbnbTheme {
                var currentScreen by remember { mutableStateOf(Screen.LOGIN) }
                var selectedProperty by remember { mutableStateOf<Property?>(null) }
                var pendingReservation by remember { mutableStateOf<Reservation?>(null) }
                var selectedChatId by remember { mutableStateOf("") }
                var selectedChatName by remember { mutableStateOf("") }

                fun handleMenuOption(option: String) {
                    currentScreen = when(option) {
                        "Perfil" -> Screen.PROFILE
                        "Propiedades" -> Screen.MY_PROPERTIES
                        "Reservaciones" -> Screen.MY_RESERVATIONS
                        "Logout" -> {
                            UserRepository.logout()
                            Screen.LOGIN
                        }
                        else -> currentScreen
                    }
                }

                when (currentScreen) {
                    Screen.LOGIN -> LoginScreen(
                        onLoginClick = { currentScreen = Screen.HOME },
                        onRegisterClick = { currentScreen = Screen.REGISTRATION }
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
                        onMenuOptionClick = { handleMenuOption(it) },
                        onNavigateTo = { screen -> currentScreen = screen }
                    )
                    Screen.PROPERTY_DETAIL -> {
                        selectedProperty?.let { property ->
                            PropertyDetailScreen(
                                property = property,
                                onBack = { currentScreen = Screen.HOME },
                                onMenuOptionClick = { handleMenuOption(it) },
                                onNavigateTo = { screen -> currentScreen = screen },
                                onConfirmReservation = { reservation ->
                                    pendingReservation = reservation
                                    currentScreen = Screen.CONFIRM_PAY
                                },
                                onContactHost = { chatId, chatName ->
                                    selectedChatId = chatId
                                    selectedChatName = chatName
                                    currentScreen = Screen.CHAT_DETAIL
                                }
                            )
                        }
                    }
                    Screen.CONFIRM_PAY -> {
                        if (selectedProperty != null && pendingReservation != null) {
                            ConfirmPayScreen(
                                property = selectedProperty!!,
                                reservation = pendingReservation!!,
                                onBack = { currentScreen = Screen.PROPERTY_DETAIL },
                                onPaymentSuccess = {
                                    currentScreen = Screen.MY_RESERVATIONS
                                }
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
                        onChatClick = { id, name -> 
                            selectedChatId = id
                            selectedChatName = name
                            currentScreen = Screen.CHAT_DETAIL 
                        },
                        onMenuOptionClick = { handleMenuOption(it) },
                        onNavigateTo = { screen -> currentScreen = screen }
                    )
                    Screen.CHAT_DETAIL -> ChatDetailScreen(
                        chatId = selectedChatId,
                        chatName = selectedChatName,
                        onBack = { currentScreen = Screen.MESSAGES },
                        onNavigateTo = { screen -> currentScreen = screen }
                    )
                    Screen.FAVORITES -> FavoritesScreen(
                        onPropertyClick = { property ->
                            selectedProperty = property
                            currentScreen = Screen.PROPERTY_DETAIL
                        },
                        onMenuOptionClick = { handleMenuOption(it) },
                        onNavigateTo = { screen -> currentScreen = screen }
                    )
                }
            }
        }
    }
}
