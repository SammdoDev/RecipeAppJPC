package com.samuel.recipeApp.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.google.firebase.auth.FirebaseUser
import com.samuel.recipeApp.ui.animation.LoaderList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentUser: FirebaseUser?,
    viewModel: HomeViewModel = viewModel(),
    onRecipeClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var isIndonesian by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var isSearchFocused by remember { mutableStateOf(false) }

    val isDarkTheme = isSystemInDarkTheme()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val categories = listOf(
        "All", "Beef", "Chicken", "Dessert", "Lamb",
        "Miscellaneous", "Pasta", "Pork", "Seafood",
        "Side", "Starter", "Vegan", "Vegetarian", "Breakfast", "Goat"
    )

    // Theme-aware colors
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFF8F9FA)
    val surfaceColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDarkTheme) Color(0xFFE0E0E0) else Color(0xFF1a1a2e)
    val textSecondary = if (isDarkTheme) Color(0xFFB0B0B0) else Color.Gray

    val gradientColors = if (isDarkTheme) listOf(
        Color(0xFF1a237e),
        Color(0xFF311b92),
        Color(0xFF4a148c)
    ) else listOf(
        Color(0xFF667eea),
        Color(0xFF764ba2),
        Color(0xFFf093fb)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isLandscape) Modifier.verticalScroll(rememberScrollState())
                    else Modifier
                )
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = Brush.verticalGradient(gradientColors))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(
                            horizontal = if (isLandscape) 32.dp else 20.dp,
                            vertical = if (isLandscape) 12.dp else 20.dp
                        )
                ) {
                    // Top Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User Profile
                        Surface(
                            modifier = Modifier
                                .height(if (isLandscape) 48.dp else 56.dp)
                                .weight(1f),
                            shape = RoundedCornerShape(if (isLandscape) 24.dp else 28.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            shadowElevation = 0.dp
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                // Avatar
                                Surface(
                                    modifier = Modifier.size(if (isLandscape) 32.dp else 40.dp),
                                    shape = CircleShape,
                                    color = Color.White,
                                    shadowElevation = 4.dp
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        if (currentUser?.photoUrl != null) {
                                            SubcomposeAsyncImage(
                                                model = currentUser.photoUrl,
                                                contentDescription = "Profile",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Text(
                                                text = currentUser?.displayName?.firstOrNull()?.uppercase() ?: "U",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    color = Color(0xFF667eea),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }

                                // User Greeting
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isIndonesian) "Halo" else "Hello",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.White.copy(alpha = 0.9f),
                                            fontSize = 11.sp
                                        )
                                    )
                                    Text(
                                        text = currentUser?.displayName?.split(" ")?.firstOrNull() ?: "User",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 14.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Action Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Translate Button
                            IconButton(
                                onClick = { isIndonesian = !isIndonesian },
                                modifier = Modifier
                                    .size(if (isLandscape) 40.dp else 48.dp)
                                    .background(
                                        color = Color.White.copy(alpha = 0.25f),
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Translate,
                                    contentDescription = "Translate",
                                    tint = Color.White
                                )
                            }

                            // Logout Button
                            IconButton(
                                onClick = { showLogoutDialog = true },
                                modifier = Modifier
                                    .size(if (isLandscape) 40.dp else 48.dp)
                                    .background(
                                        color = Color.White.copy(alpha = 0.25f),
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Logout,
                                    contentDescription = "Logout",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(if (isLandscape) 16.dp else 24.dp))

                    // Title
                    if (!isLandscape) {
                        Text(
                            text = if (isIndonesian)
                                "Temukan Resep\nFavoritmu"
                            else
                                "Discover Your\nFavorite Recipes",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                lineHeight = 38.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Search Bar
                    val searchScale by animateFloatAsState(
                        targetValue = if (isSearchFocused) 1.02f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ), label = ""
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (isLandscape) 48.dp else 56.dp)
                            .scale(searchScale)
                            .shadow(
                                elevation = if (isSearchFocused) 16.dp else 8.dp,
                                shape = RoundedCornerShape(if (isLandscape) 24.dp else 28.dp),
                                spotColor = Color.Black.copy(alpha = 0.25f)
                            ),
                        shape = RoundedCornerShape(if (isLandscape) 24.dp else 28.dp),
                        color = surfaceColor
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
                                    if (isIndonesian) "Cari resep favorit..." else "Search your favorite recipe...",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = textSecondary.copy(alpha = 0.6f)
                                    )
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = "Search",
                                    tint = textSecondary
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            searchQuery = ""
                                            viewModel.loadRandomRecipes()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Close,
                                            contentDescription = "Clear",
                                            tint = textSecondary
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(if (isLandscape) 12.dp else 20.dp))
                }
            }

            // Category Chips
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory),
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Transparent,
                contentColor = Color(0xFF667eea),
                edgePadding = 16.dp,
                indicator = {},
                divider = {}
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategory == category

                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0.92f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = ""
                    )

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
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected)
                                Color(0xFF667eea)
                            else
                                surfaceColor,
                            shadowElevation = if (isSelected) 8.dp else 2.dp,
                            modifier = Modifier
                                .scale(scale)
                                .padding(4.dp)
                        ) {
                            Text(
                                text = if (isIndonesian) translateCategory(category) else category,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = if (isSelected) Color.White else textColor,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
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
                        isIndonesian = isIndonesian,
                        isDarkTheme = isDarkTheme
                    )
                }

                uiState.recipes.isEmpty() -> {
                    EmptyState(
                        isIndonesian = isIndonesian,
                        isDarkTheme = isDarkTheme
                    )
                }

                else -> {
                    if (isLandscape) {
                        // Landscape: Use FlowRow instead of LazyVerticalGrid
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            uiState.recipes.chunked(3).forEach { rowRecipes ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    rowRecipes.forEach { recipe ->
                                        val animatedProgress = remember { Animatable(0f) }
                                        LaunchedEffect(recipe.idMeal) {
                                            animatedProgress.animateTo(
                                                targetValue = 1f,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessLow,
                                                )
                                            )
                                        }

                                        Box(modifier = Modifier.weight(1f)) {
                                            PremiumRecipeCard(
                                                recipe = recipe,
                                                onClick = { onRecipeClick(recipe.idMeal) },
                                                isIndonesian = isIndonesian,
                                                isDarkTheme = isDarkTheme,
                                                modifier = Modifier.graphicsLayer {
                                                    alpha = animatedProgress.value
                                                    scaleX = 0.8f + (animatedProgress.value * 0.2f)
                                                    scaleY = 0.8f + (animatedProgress.value * 0.2f)
                                                }
                                            )
                                        }
                                    }
                                    // Fill empty spaces
                                    repeat(3 - rowRecipes.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    } else {
                        // Portrait: Use LazyVerticalGrid as before
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            itemsIndexed(uiState.recipes) { index, recipe ->
                                val animatedProgress = remember { Animatable(0f) }
                                LaunchedEffect(recipe.idMeal) {
                                    animatedProgress.animateTo(
                                        targetValue = 1f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow,
                                        )
                                    )
                                }

                                PremiumRecipeCard(
                                    recipe = recipe,
                                    onClick = { onRecipeClick(recipe.idMeal) },
                                    isIndonesian = isIndonesian,
                                    isDarkTheme = isDarkTheme,
                                    modifier = Modifier.graphicsLayer {
                                        alpha = animatedProgress.value
                                        scaleX = 0.8f + (animatedProgress.value * 0.2f)
                                        scaleY = 0.8f + (animatedProgress.value * 0.2f)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = Color(0xFF667eea).copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Logout,
                            contentDescription = null,
                            tint = Color(0xFF667eea),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            },
            title = {
                Text(
                    text = if (isIndonesian) "Keluar dari Akun" else "Logout from Account",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                )
            },
            text = {
                Text(
                    text = if (isIndonesian)
                        "Apakah kamu yakin ingin keluar dari akun ini?"
                    else
                        "Are you sure you want to logout from this account?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = textSecondary
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF667eea)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        if (isIndonesian) "Ya, Keluar" else "Yes, Logout",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = textSecondary
                    )
                ) {
                    Text(
                        if (isIndonesian) "Batal" else "Cancel",
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = surfaceColor
        )
    }
}

@Composable
fun PremiumRecipeCard(
    recipe: com.samuel.recipeApp.data.api.Meal,
    onClick: () -> Unit,
    isIndonesian: Boolean = false,
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    val surfaceColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDarkTheme) Color(0xFFE0E0E0) else Color(0xFF1a1a2e)
    val textSecondary = if (isDarkTheme) Color(0xFFB0B0B0) else Color.Gray

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Recipe Image
            SubcomposeAsyncImage(
                model = recipe.strMealThumb,
                contentDescription = recipe.strMeal,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(if (isDarkTheme) Color(0xFF2A2A2A) else Color(0xFFF0F0F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = Color(0xFF667eea),
                            strokeWidth = 3.dp
                        )
                    }
                }
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.4f)
                            ),
                            startY = 100f
                        )
                    )
            )

            // Category Badge
            recipe.strCategory?.let { category ->
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF667eea),
                    shadowElevation = 4.dp
                ) {
                    Text(
                        text = if (isIndonesian) translateCategory(category) else category,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Recipe Info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(surfaceColor)
                    .padding(14.dp)
            ) {
                Text(
                    text = recipe.strMeal,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = textColor
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Place,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = textSecondary
                    )
                    Text(
                        text = recipe.strArea ?: if (isIndonesian) "Internasional" else "International",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = textSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

fun translateCategory(category: String): String {
    return when (category) {
        "All" -> "Semua"
        "Beef" -> "Daging Sapi"
        "Chicken" -> "Ayam"
        "Dessert" -> "Penutup"
        "Lamb" -> "Domba"
        "Miscellaneous" -> "Lainnya"
        "Pasta" -> "Pasta"
        "Pork" -> "Babi"
        "Seafood" -> "Seafood"
        "Side" -> "Lauk"
        "Starter" -> "Pembuka"
        "Vegan" -> "Vegan"
        "Vegetarian" -> "Vegetarian"
        "Breakfast" -> "Sarapan"
        "Goat" -> "Kambing"
        else -> category
    }
}

@Composable
fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    isIndonesian: Boolean = false,
    isDarkTheme: Boolean = false
) {
    val textColor = if (isDarkTheme) Color(0xFFE0E0E0) else Color(0xFF1a1a2e)
    val textSecondary = if (isDarkTheme) Color(0xFFB0B0B0) else Color.Gray
    val surfaceColor = if (isDarkTheme) Color(0xFF2A2A2A) else Color(0xFFFFF3E0)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = surfaceColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.ErrorOutline,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color(0xFFFF6B6B)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = if (isIndonesian) "Ups! Ada yang salah" else "Oops! Something went wrong",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = textSecondary,
                textAlign = TextAlign.Center
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
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isIndonesian) "Coba Lagi" else "Try Again",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun EmptyState(isIndonesian: Boolean = false, isDarkTheme: Boolean = false) {
    val textColor = if (isDarkTheme) Color(0xFFE0E0E0) else Color(0xFF1a1a2e)
    val textSecondary = if (isDarkTheme) Color(0xFFB0B0B0) else Color.Gray
    val surfaceColor = if (isDarkTheme) Color(0xFF2A2A2A) else Color(0xFFF5F5F5)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = surfaceColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.RestaurantMenu,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color(0xFF667eea)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = if (isIndonesian) "Resep tidak ditemukan" else "No recipes found",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isIndonesian) "Coba cari yang lain" else "Try searching for something else",
                style = MaterialTheme.typography.bodyMedium,
                color = textSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}