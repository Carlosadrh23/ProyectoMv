package com.movil.arbnb

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.arbnb.ui.theme.ArbnbBackground
import com.movil.arbnb.ui.theme.ArbnbTeal
import com.movil.arbnb.ui.theme.SuccessGreen

@Composable
fun FavoritesScreen(
    onPropertyClick: (Property) -> Unit,
    onMenuOptionClick: (String) -> Unit,
    onNavigateTo: (Screen) -> Unit,
    onContactClick: (Property) -> Unit
) {
    var propertyToCancel by remember { mutableStateOf<Property?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ArbnbTopAppBar(
                title = "Favoritos",
                onMenuOptionClick = onMenuOptionClick,
                onLogoClick = { onNavigateTo(Screen.HOME) }
            )
        },
        bottomBar = {
            HomeBottomNavigation(
                currentScreen = Screen.FAVORITES,
                onNavigateTo = onNavigateTo
            )
        },
        containerColor = ArbnbBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val favoriteProperties = propertiesList.filter { it.isFavorite }

            if (favoriteProperties.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Aún no tienes favoritos", color = Color.Gray)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(favoriteProperties) { property ->
                        FavoritePropertyCard(
                            property = property, 
                            onClick = { onPropertyClick(property) },
                            onToggleFavorite = {
                                val index = propertiesList.indexOfFirst { it.id == property.id }
                                if (index != -1) {
                                    propertiesList[index] = propertiesList[index].copy(isFavorite = false)
                                }
                            },
                            onCancelClick = {
                                propertyToCancel = property
                                showConfirmDialog = true
                            },
                            onContactClick = { onContactClick(property) }
                        )
                    }
                }
            }
        }

        if (showConfirmDialog && propertyToCancel != null) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("¿Desea cancelar su reservación?") },
                text = { Text("Esta acción no se puede deshacer.") },
                confirmButton = {
                    Button(
                        onClick = {
                            val target = propertyToCancel
                            if (target != null) {
                                val index = propertiesList.indexOfFirst { it.id == target.id }
                                if (index != -1) {
                                    propertiesList[index] = propertiesList[index].copy(isFavorite = false)
                                }
                            }
                            showConfirmDialog = false
                            propertyToCancel = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showConfirmDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun FavoritePropertyCard(
    property: Property,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onCancelClick: () -> Unit,
    onContactClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box {
                Image(
                    painter = painterResource(id = property.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(32.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .clickable { onToggleFavorite() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(text = property.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = property.description,
                    fontSize = 13.sp,
                    color = Color.Black,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onCancelClick,
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Text("Reservado", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onContactClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Text("Contactar", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
