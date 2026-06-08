package com.movil.arbnb

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.arbnb.ui.theme.ArbnbBackground
import com.movil.arbnb.ui.theme.ArbnbTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onPropertyClick: (Property) -> Unit,
    onMenuOptionClick: (String) -> Unit,
    onNavigateTo: (Screen) -> Unit
) {
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
            Text(
                text = "Tus Favoritos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp),
                color = ArbnbTeal
            )
            
            // For now, just show a subset of properties as favorites
            val favoriteProperties = propertiesList.take(2) 

            if (favoriteProperties.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("Aún no tienes favoritos")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(favoriteProperties) { property ->
                        PropertyCard(property = property, onClick = { onPropertyClick(property) })
                    }
                }
            }
        }
    }
}
