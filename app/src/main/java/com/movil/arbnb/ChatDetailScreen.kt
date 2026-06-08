package com.movil.arbnb

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.arbnb.ui.theme.ArbnbTeal
import com.movil.arbnb.data.UserRepository
import com.movil.arbnb.data.ChatRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chatId: String,
    chatName: String,
    onBack: () -> Unit,
    onNavigateTo: (Screen) -> Unit
) {
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var text by remember { mutableStateOf("") }
    val currentUser = UserRepository.currentUser
    val listState = rememberLazyListState()

    LaunchedEffect(chatId) {
        ChatRepository.getMessages(chatId) { list ->
            messages = list
        }
    }
    
    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
// ... existing topBar ...
            ArbnbTopAppBar(
                title = chatName,
                onNavigationIconClick = onBack,
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onMenuOptionClick = {},
                onLogoClick = { onNavigateTo(Screen.HOME) }
            )
        },
        bottomBar = {
// ... existing bottomBar ...
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
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Escribe un mensaje...") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    
                    IconButton(onClick = {
                        if (text.isNotBlank()) {
                            ChatRepository.sendMessage(chatId, currentUser?.email ?: "", text)
                            text = ""
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = ArbnbTeal)
                    }
                }
            }
        },
        containerColor = ArbnbTeal
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages) { message ->
                ChatBubble(message, message.senderId == currentUser?.email)
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message, isFromMe: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isFromMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isFromMe) {
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
            color = if (isFromMe) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.7f),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.widthIn(max = 250.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                fontSize = 15.sp
            )
        }

        if (isFromMe) {
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
