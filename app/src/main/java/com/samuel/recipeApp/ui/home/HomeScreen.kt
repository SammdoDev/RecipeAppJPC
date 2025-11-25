package com.samuel.recipeApp.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.samuel.recipeApp.ui.animation.LoaderList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onRecipeClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var isIndonesian by remember { mutableStateOf(false) }

    // Daftar kategori
    val categories = listOf(
        "All", "Beef", "Chicken", "Dessert", "Lamb",
        "Miscellaneous", "Pasta", "Pork", "Seafood",
        "Side", "Starter", "Vegan", "Vegetarian", "Breakfast", "Goat"
    )

    // Gradient colors
    val gradientColors = listOf(
        Color(0xFF667eea),
        Color(0xFF764ba2)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Custom Header with Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(gradientColors),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(top = 48.dp, bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isIndonesian) "Temukan" else "Discover",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = if (isIndonesian) "Cari resep favoritmu 🍳" else "Find your favorite recipes 🍳",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            )
                        }

                        // Translate Button
                        IconButton(
                            onClick = { isIndonesian = !isIndonesian },
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Default.Translate,
                                contentDescription = "Translate",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Modern Search Bar
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(28.dp),
                                spotColor = Color.Black.copy(alpha = 0.1f)
                            ),
                        shape = RoundedCornerShape(28.dp),
                        color = Color.White
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                viewModel.searchRecipes(it)
                            },
                            modifier = Modifier.fillMaxSize(),
                            placeholder = {
                                Text(
                                    if (isIndonesian) "Cari resep..." else "Search recipes...",
                                    color = Color.Gray
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color(0xFF667eea)
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    }
                }
            }

            // Category Tabs
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory),
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.White,
                contentColor = Color(0xFF667eea),
                edgePadding = 16.dp,
                indicator = {},
                divider = {}
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    Tab(
                        selected = isSelected,
                        onClick = {
                            selectedCategory = category
                            if (category == "All") {
                                viewModel.loadRandomRecipes()
                            } else {
                                viewModel.filterByCategory(category)
                            }
                        },
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected)
                                Color(0xFF667eea)
                            else
                                Color(0xFFF0F0F0),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(
                                text = if (isIndonesian) translateCategory(category) else category,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = if (isSelected) Color.White else Color.Gray,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }

            // Content Area
            when {
                uiState.isLoading -> {
                    LoaderList()
                }

                uiState.error != null -> {
                    ErrorState(
                        error = uiState.error ?: "Unknown error",
                        onRetry = { viewModel.loadRandomRecipes() },
                        isIndonesian = isIndonesian
                    )
                }

                uiState.recipes.isEmpty() -> {
                    EmptyState(isIndonesian = isIndonesian)
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(uiState.recipes) { index, recipe ->
                            // Staggered Animation
                            val animatedProgress = remember { Animatable(0f) }
                            LaunchedEffect(recipe.idMeal) {
                                animatedProgress.animateTo(
                                    targetValue = 1f,
                                    animationSpec = tween(
                                        durationMillis = 400,
                                        delayMillis = index * 50,
                                        easing = FastOutSlowInEasing
                                    )
                                )
                            }

                            ModernRecipeCard(
                                recipe = recipe,
                                onClick = { onRecipeClick(recipe.idMeal) },
                                isIndonesian = isIndonesian,
                                modifier = Modifier.graphicsLayer {
                                    alpha = animatedProgress.value
                                    translationY = (1f - animatedProgress.value) * 50f
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Fungsi untuk translate kategori ke Indonesia
fun translateCategory(category: String): String {
    return when (category) {
        "All" -> "Semua"
        "Beef" -> "Daging Sapi"
        "Chicken" -> "Ayam"
        "Dessert" -> "Makanan Penutup"
        "Lamb" -> "Daging Domba"
        "Miscellaneous" -> "Lainnya"
        "Pasta" -> "Pasta"
        "Pork" -> "Daging Babi"
        "Seafood" -> "Makanan Laut"
        "Side" -> "Lauk"
        "Starter" -> "Pembuka"
        "Vegan" -> "Vegan"
        "Vegetarian" -> "Vegetarian"
        "Breakfast" -> "Sarapan"
        "Goat" -> "Daging Kambing"
        else -> category
    }
}

@Composable
fun ModernRecipeCard(
    recipe: com.samuel.recipeApp.data.api.Meal,
    onClick: () -> Unit,
    isIndonesian: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color(0xFF667eea).copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Image with Gradient Overlay
            SubcomposeAsyncImage(
                model = recipe.strMealThumb,
                contentDescription = recipe.strMeal,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF0F0F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFF667eea),
                            strokeWidth = 2.dp
                        )
                    }
                }
            )

            // Gradient Overlay on Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f)
                            ),
                            startY = 100f
                        )
                    )
            )

            // Favorite Button
            IconButton(
                onClick = { /* TODO: Add to favorites */ },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(36.dp)
                    .background(
                        Color.White.copy(alpha = 0.9f),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = Color(0xFFE91E63),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Category Badge
            recipe.strCategory?.let { category ->
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF667eea).copy(alpha = 0.9f)
                ) {
                    Text(
                        text = if (isIndonesian) translateCategory(category) else category,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // Bottom Content
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Text(
                    text = recipe.strMeal,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFF1a1a2e)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.Restaurant,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFF667eea)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = recipe.strArea ?: if (isIndonesian) "Internasional" else "International",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorState(error: String, onRetry: () -> Unit, isIndonesian: Boolean = false) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "😔",
                fontSize = 64.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isIndonesian) "Ups! Ada yang salah" else "Oops! Something went wrong",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF667eea)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Icon(Icons.Default.Translate, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isIndonesian) "Coba Lagi" else "Try Again")
            }
        }
    }
}

@Composable
fun EmptyState(isIndonesian: Boolean = false) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "🍽️",
                fontSize = 64.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isIndonesian) "Resep tidak ditemukan" else "No recipes found",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isIndonesian) "Coba cari yang lain" else "Try searching for something else",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}