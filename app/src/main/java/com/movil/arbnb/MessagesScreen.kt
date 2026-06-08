package com.movil.arbnb

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.arbnb.ui.theme.ArbnbTeal
import com.movil.arbnb.data.UserRepository
import com.movil.arbnb.data.ChatRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    onBack: () -> Unit,
    onChatClick: (String, String) -> Unit,
    onMenuOptionClick: (String) -> Unit,
    onNavigateTo: (Screen) -> Unit
) {
    var chats by remember { mutableStateOf<List<Chat>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val currentUser = UserRepository.currentUser

    LaunchedEffect(currentUser?.email) {
        currentUser?.let { user ->
            ChatRepository.getMyChats(user.email) { list ->
                chats = list
                isLoading = false
            }
        } ?: run { isLoading = false }
    }

    Scaffold(
        topBar = {
            ArbnbTopAppBar(
                title = "Mensajes",
                onNavigationIconClick = onBack,
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onMenuOptionClick = onMenuOptionClick,
                onLogoClick = onBack
            )
        },
        bottomBar = {
            HomeBottomNavigation(
                currentScreen = Screen.MESSAGES,
                onNavigateTo = onNavigateTo
            )
        },
        containerColor = ArbnbTeal
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Mensajes",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.7f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Buscar mensajes", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (chats.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes mensajes aún", color = Color.White)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(chats) { chat ->
                        val otherName = chat.participantNames.entries.find { it.key != currentUser?.email }?.value ?: "Usuario"
                        MessageItem(
                            chat = chat, 
                            otherName = otherName,
                            onClick = { onChatClick(chat.id, otherName) }
                        )
                        HorizontalDivider(color = Color.White.copy(alpha = 0.3f), thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItem(chat: Chat, otherName: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFF64B5F6), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = otherName.take(2).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1.0f)) {
            Text(text = otherName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = if (chat.lastMessage.isEmpty()) "Nuevo chat" else chat.lastMessage, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, maxLines = 1)
        }
    }
}
