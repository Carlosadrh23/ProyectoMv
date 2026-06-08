package com.movil.arbnb

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.arbnb.ui.theme.ArbnbTeal

data class ChatMessage(
    val text: String,
    val isFromMe: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chatName: String,
    onBack: () -> Unit,
    onNavigateTo: (Screen) -> Unit
) {
    val messages = listOf(
        ChatMessage("Hola Angel", false),
        ChatMessage("¿Cómo va todo?", false),
        ChatMessage("Todo bien, gracias", true),
        ChatMessage("¿A qué hora llegas?", false),
        ChatMessage("Estaré ahí a las 2", true),
        ChatMessage("Perfecto", false)
    )

    Scaffold(
        topBar = {
            ArbnbTopAppBar(
                title = chatName,
                onNavigationIconClick = onBack,
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onMenuOptionClick = {},
                onLogoClick = { onNavigateTo(Screen.HOME) }
            )
        },
        bottomBar = {
            HomeBottomNavigation(
                currentScreen = Screen.CHAT_DETAIL,
                onNavigateTo = onNavigateTo
            )
        },
        containerColor = ArbnbTeal
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                reverseLayout = false
            ) {
                items(messages) { message ->
                    ChatBubble(message)
                }
            }

            // Bottom Input Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.9f),
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                    
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Gray.copy(alpha = 0.3f)
                    ) {
                        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text("Escribe un mensaje...", color = Color.DarkGray, fontSize = 14.sp)
                        }
                    }
                    
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Mic, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isFromMe) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            color = if (message.isFromMe) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.7f),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.widthIn(max = 250.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                fontSize = 15.sp
            )
        }

        if (message.isFromMe) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 14.sp)
            }
        }
    }
}
