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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.arbnb.ui.theme.ArbnbTeal

data class MessagePreview(
    val id: Int,
    val initials: String,
    val name: String,
    val snippet: String,
    val time: String,
    val avatarColor: Color,
    val unreadCount: Int = 0
)

val messagesList = listOf(
    MessagePreview(1, "SR", "Sofia R.", "¡Bienvenido! El código de acceso...", "10:32", Color(0xFF64B5F6), 2),
    MessagePreview(2, "MG", "Miguel G.", "Claro, el check-in es a las 3 p. m.", "Ayer", Color(0xFF81C784)),
    MessagePreview(3, "AL", "Ana L.", "¿Necesitas más toallas? Con g...", "Lun", Color(0xFFBA68C8)),
    MessagePreview(4, "RV", "Roberto V.", "Gracias por tu estancia, espero...", "Dom", Color(0xFFDCE775)),
    MessagePreview(5, "CH", "Carmen H.", "Perfecto te espero a las 2 p. m.", "Sáb", Color(0xFFE57373))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    onBack: () -> Unit,
    onChatClick: (Int) -> Unit,
    onMenuOptionClick: (String) -> Unit,
    onNavigateTo: (Screen) -> Unit
) {
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

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(messagesList) { message ->
                    MessageItem(message = message, onClick = { onChatClick(message.id) })
                    HorizontalDivider(color = Color.White.copy(alpha = 0.3f), thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
fun MessageItem(message: MessagePreview, onClick: () -> Unit) {
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
                .background(message.avatarColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = message.initials, color = Color.White, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1.0f)) {
            Text(text = message.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = message.snippet, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, maxLines = 1)
        }
        
        Column(horizontalAlignment = Alignment.End) {
            Text(text = message.time, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            if (message.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(20.dp)
                        .background(Color.Red, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = message.unreadCount.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
