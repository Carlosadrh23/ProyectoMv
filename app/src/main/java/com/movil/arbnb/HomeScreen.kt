package com.movil.arbnb

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.arbnb.ui.theme.*
import com.movil.arbnb.data.PropertyRepository
import com.movil.arbnb.data.FavoriteRepository
import com.movil.arbnb.data.UserRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPropertyClick: (Property) -> Unit,
    onMenuOptionClick: (String) -> Unit,
    onNavigateTo: (Screen) -> Unit
) {
    var properties by remember { mutableStateOf<List<Property>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    val currentUser = UserRepository.currentUser

    LaunchedEffect(currentUser?.email) {
        currentUser?.let {
            FavoriteRepository.loadFavorites(it.email)
        }
        PropertyRepository.getAllActiveProperties { list ->
            properties = list
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            Column {
                ArbnbTopAppBar(
                    title = "Explorar",
                    onMenuOptionClick = onMenuOptionClick,
                    onLogoClick = { /* Already Home */ }
                )
            }
        },
        bottomBar = { 
            HomeBottomNavigation(
                currentScreen = Screen.HOME,
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
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ArbnbBlue)
                }
            } else if (properties.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay propiedades disponibles", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(properties) { property ->
                        val isFavorite = FavoriteRepository.favoriteIds.contains(property.id)
                        PropertyCard(
                            property = property, 
                            isFavorite = isFavorite,
                            onClick = { onPropertyClick(property) },
                            onFavoriteClick = {
                                currentUser?.let { user ->
                                    FavoriteRepository.toggleFavorite(user.email, property.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PropertyCard(
    property: Property, 
    isFavorite: Boolean = false,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
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
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.2f), CircleShape)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) Color.Red else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${property.tipo_alojamiento} en ${property.ciudad}",
                        fontWeight = FontWeight.Bold, 
                        fontSize = 14.sp
                    )
                    StaticRatingBar(rating = property.averageRating, size = 14.dp)
                }
                Text(
                    text = property.descripcion,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${property.precio_noche} MXN por noche",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ArbnbBlue
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (property.amenidades.contains("WIFI")) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                        Text(text = "Wifi", fontSize = 11.sp, modifier = Modifier.padding(end = 8.dp))
                    }
                    if (property.amenidades.contains("Cocina")) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                        Text(text = "Cocina", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun HomeBottomNavigation(
    currentScreen: Screen,
    onNavigateTo: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = ArbnbTealLight,
        contentColor = Color.Black
    ) {
        NavigationBarItem(
            selected = currentScreen == Screen.HOME,
            onClick = { onNavigateTo(Screen.HOME) },
            icon = { Icon(Icons.Default.Search, contentDescription = null) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.Black, unselectedIconColor = Color.Black, indicatorColor = Color.White.copy(alpha = 0.3f))
        )
        NavigationBarItem(
            selected = currentScreen == Screen.FAVORITES,
            onClick = { onNavigateTo(Screen.FAVORITES) },
            icon = { Icon(if (currentScreen == Screen.FAVORITES) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = null) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.Black, unselectedIconColor = Color.Black, indicatorColor = Color.White.copy(alpha = 0.3f))
        )
        NavigationBarItem(
            selected = currentScreen == Screen.MY_RESERVATIONS,
            onClick = { onNavigateTo(Screen.MY_RESERVATIONS) },
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.Black, unselectedIconColor = Color.Black, indicatorColor = Color.White.copy(alpha = 0.3f))
        )
        NavigationBarItem(
            selected = currentScreen == Screen.MESSAGES,
            onClick = { onNavigateTo(Screen.MESSAGES) },
            icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.Black, unselectedIconColor = Color.Black, indicatorColor = Color.White.copy(alpha = 0.3f))
        )
    }
}
