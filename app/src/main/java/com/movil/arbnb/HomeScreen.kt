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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPropertyClick: (Property) -> Unit,
    onMenuOptionClick: (String) -> Unit,
    onNavigateTo: (Screen) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var properties by remember { mutableStateOf<List<Property>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        PropertyRepository.getAllActiveProperties { list ->
            properties = list
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
// ... existing topBar ...
            Column {
                ArbnbTopAppBar(
                    title = "Viajes",
                    onMenuOptionClick = onMenuOptionClick,
                    onLogoClick = { /* Already Home */ }
                )
                SecondaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = ArbnbTeal,
                    contentColor = Color.White,
                    indicator = {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(selectedTab),
                            color = Color.White
                        )
                    }
                ) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Próximos", fontSize = 12.sp) })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Pasados", fontSize = 12.sp) })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Cancelados", fontSize = 12.sp) })
                }
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
                        PropertyCard(property = property, onClick = { onPropertyClick(property) })
                    }
                }
            }
        }
    }
}

@Composable
fun PropertyCard(property: Property, onClick: () -> Unit) {
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
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.2f), CircleShape)
                        .padding(4.dp)
                )
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
                    Icon(imageVector = Icons.Default.OpenInFull, contentDescription = null, modifier = Modifier.size(16.dp))
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
            selected = currentScreen == Screen.MESSAGES,
            onClick = { onNavigateTo(Screen.MESSAGES) },
            icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.Black, unselectedIconColor = Color.Black, indicatorColor = Color.White.copy(alpha = 0.3f))
        )
    }
}
