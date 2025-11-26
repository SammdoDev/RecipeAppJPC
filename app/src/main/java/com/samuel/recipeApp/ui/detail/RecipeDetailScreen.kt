package com.samuel.recipeApp.ui.detail

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String,
    viewModel: RecipeDetailViewModel = viewModel(),
    onBackPressed: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var isIndonesian by remember { mutableStateOf(false) }

    val isDarkTheme = isSystemInDarkTheme()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(recipeId) {
        viewModel.loadRecipeDetail(recipeId)
    }

    LaunchedEffect(isIndonesian) {
        if (isIndonesian && uiState.translatedMeal == null && uiState.recipe != null) {
            viewModel.translateRecipe()
        } else if (!isIndonesian) {
            viewModel.clearTranslation()
        }
    }

    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFF8F9FE)

    Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        when {
            uiState.isLoading -> ModernLoadingState(
                isIndonesian = isIndonesian,
                isDarkTheme = isDarkTheme
            )
            uiState.error != null -> ModernErrorState(
                error = uiState.error ?: "Unknown error",
                onRetry = { viewModel.loadRecipeDetail(recipeId) },
                isIndonesian = isIndonesian,
                isDarkTheme = isDarkTheme
            )
            uiState.recipe != null -> {
                ModernRecipeDetailContent(
                    recipe = uiState.recipe!!,
                    translatedMeal = uiState.translatedMeal,
                    scrollState = scrollState,
                    onBackPressed = onBackPressed,
                    isIndonesian = isIndonesian,
                    isTranslating = uiState.isTranslating,
                    onTranslateClick = { isIndonesian = !isIndonesian },
                    isDarkTheme = isDarkTheme,
                    isLandscape = isLandscape
                )
            }
        }
    }
}

@Composable
fun ModernRecipeDetailContent(
    recipe: com.samuel.recipeApp.data.api.Meal,
    translatedMeal: com.samuel.recipeApp.data.api.TranslatedMeal?,
    scrollState: androidx.compose.foundation.ScrollState,
    onBackPressed: () -> Unit,
    isIndonesian: Boolean,
    isTranslating: Boolean,
    onTranslateClick: () -> Unit,
    isDarkTheme: Boolean,
    isLandscape: Boolean
) {
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animatedProgress.animateTo(1f, animationSpec = tween(600, easing = FastOutSlowInEasing))
    }

    // Theme colors
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFF8F9FE)
    val surfaceColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
    val cardColor = if (isDarkTheme) Color(0xFF2A2A2A) else Color.White
    val textColor = if (isDarkTheme) Color(0xFFE0E0E0) else Color(0xFF1a1a2e)
    val textSecondary = if (isDarkTheme) Color(0xFFB0B0B0) else Color.Gray
    val dividerColor = if (isDarkTheme) Color(0xFF3A3A3A) else Color(0xFFF0F0F0)

    val gradientPrimary = if (isDarkTheme) Color(0xFF5a67d8) else Color(0xFF667eea)
    val gradientSecondary = if (isDarkTheme) Color(0xFF6b46c1) else Color(0xFF764ba2)

    val ingredients = translatedMeal?.getIngredients(isIndonesian) ?: recipe.getIngredients()
    val instructions = translatedMeal?.getInstructions(isIndonesian) ?: recipe.getInstructionSteps()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Hero Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isLandscape) 280.dp else 380.dp)
            ) {
                SubcomposeAsyncImage(
                    model = recipe.strMealThumb,
                    contentDescription = recipe.strMeal,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize().background(
                                if (isDarkTheme) Color(0xFF2A2A2A) else Color(0xFFF0F0F0)
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = gradientPrimary,
                                strokeWidth = 3.dp
                            )
                        }
                    }
                )

                // Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.5f)
                                )
                            )
                        )
                )

                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = if (isLandscape) 16.dp else 44.dp,
                            start = 16.dp,
                            end = 16.dp
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = CircleShape
                            )
                            .background(
                                color = surfaceColor.copy(alpha = 0.9f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowBack,
                                contentDescription = "Back",
                                tint = textColor
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Translate Button
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .shadow(
                                    elevation = 4.dp,
                                    shape = CircleShape
                                )
                                .background(
                                    color = if (isIndonesian)
                                        gradientPrimary.copy(alpha = 0.9f)
                                    else
                                        surfaceColor.copy(alpha = 0.9f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = onTranslateClick,
                                enabled = !isTranslating
                            ) {
                                if (isTranslating) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = if (isIndonesian) Color.White else gradientPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Outlined.Translate,
                                        contentDescription = "Translate",
                                        tint = if (isIndonesian) Color.White else textColor
                                    )
                                }
                            }
                        }

                    }
                }

                // YouTube Button
                recipe.strYoutube?.let {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .offset(y = 28.dp)
                            .size(56.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = CircleShape
                            )
                            .background(
                                color = Color(0xFFFF0000),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = "Play video",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            // Content Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-24).dp),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = backgroundColor
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = if (isLandscape) 32.dp else 24.dp,
                        vertical = 24.dp
                    )
                ) {
                    // Title
                    Text(
                        text = recipe.strMeal,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            fontSize = if (isLandscape) 26.sp else 28.sp
                        ),
                        modifier = Modifier.graphicsLayer {
                            alpha = animatedProgress.value
                            translationY = (1f - animatedProgress.value) * 20f
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tags Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.graphicsLayer {
                            alpha = animatedProgress.value
                        }
                    ) {
                        recipe.strCategory?.let { category ->
                            ModernTag(
                                text = if (isIndonesian) translateCategory(category) else category,
                                icon = Icons.Outlined.Restaurant,
                                gradient = listOf(gradientPrimary, gradientSecondary)
                            )
                        }
                        recipe.strArea?.let { area ->
                            ModernTag(
                                text = if (isIndonesian) translateArea(area) else area,
                                icon = Icons.Outlined.Place,
                                gradient = listOf(Color(0xFF11998e), Color(0xFF38ef7d))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    if (isLandscape) {
                        // Landscape Layout
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                SectionHeader(
                                    title = if (isIndonesian) "Bahan-bahan" else "Ingredients",
                                    icon = Icons.Outlined.ShoppingCart,
                                    itemCount = ingredients.size,
                                    textColor = textColor,
                                    gradientPrimary = gradientPrimary,
                                    gradientSecondary = gradientSecondary
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(
                                            elevation = 8.dp,
                                            shape = RoundedCornerShape(20.dp),
                                            spotColor = gradientPrimary.copy(alpha = 0.15f)
                                        ),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = cardColor)
                                ) {
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        ingredients.forEachIndexed { index, (ingredient, measure) ->
                                            IngredientItem(
                                                ingredient = ingredient,
                                                measure = measure,
                                                index = index + 1,
                                                textColor = textColor,
                                                gradientPrimary = gradientPrimary,
                                                gradientSecondary = gradientSecondary,
                                                dividerColor = dividerColor,
                                                isDarkTheme = isDarkTheme
                                            )
                                            if (index < ingredients.lastIndex) {
                                                HorizontalDivider(
                                                    modifier = Modifier.padding(vertical = 12.dp),
                                                    color = dividerColor
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                SectionHeader(
                                    title = if (isIndonesian) "Cara Memasak" else "Instructions",
                                    icon = Icons.Outlined.MenuBook,
                                    itemCount = null,
                                    textColor = textColor,
                                    gradientPrimary = gradientPrimary,
                                    gradientSecondary = gradientSecondary
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    instructions.forEachIndexed { index, step ->
                                        InstructionStep(
                                            stepNumber = index + 1,
                                            instruction = step.trim(),
                                            textColor = textColor,
                                            cardColor = cardColor,
                                            gradientPrimary = gradientPrimary,
                                            gradientSecondary = gradientSecondary
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Portrait Layout
                        SectionHeader(
                            title = if (isIndonesian) "Bahan-bahan" else "Ingredients",
                            icon = Icons.Outlined.ShoppingCart,
                            itemCount = ingredients.size,
                            textColor = textColor,
                            gradientPrimary = gradientPrimary,
                            gradientSecondary = gradientSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    spotColor = gradientPrimary.copy(alpha = 0.15f)
                                ),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                ingredients.forEachIndexed { index, (ingredient, measure) ->
                                    IngredientItem(
                                        ingredient = ingredient,
                                        measure = measure,
                                        index = index + 1,
                                        textColor = textColor,
                                        gradientPrimary = gradientPrimary,
                                        gradientSecondary = gradientSecondary,
                                        dividerColor = dividerColor,
                                        isDarkTheme = isDarkTheme
                                    )
                                    if (index < ingredients.lastIndex) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(vertical = 12.dp),
                                            color = dividerColor
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        SectionHeader(
                            title = if (isIndonesian) "Cara Memasak" else "Instructions",
                            icon = Icons.Outlined.MenuBook,
                            itemCount = null,
                            textColor = textColor,
                            gradientPrimary = gradientPrimary,
                            gradientSecondary = gradientSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            instructions.forEachIndexed { index, step ->
                                InstructionStep(
                                    stepNumber = index + 1,
                                    instruction = step.trim(),
                                    textColor = textColor,
                                    cardColor = cardColor,
                                    gradientPrimary = gradientPrimary,
                                    gradientSecondary = gradientSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

fun translateCategory(category: String): String {
    return when (category) {
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

fun translateArea(area: String): String {
    return when (area) {
        "American" -> "Amerika"
        "British" -> "Inggris"
        "Canadian" -> "Kanada"
        "Chinese" -> "Tiongkok"
        "Croatian" -> "Kroasia"
        "Dutch" -> "Belanda"
        "Egyptian" -> "Mesir"
        "French" -> "Prancis"
        "Greek" -> "Yunani"
        "Indian" -> "India"
        "Irish" -> "Irlandia"
        "Italian" -> "Italia"
        "Jamaican" -> "Jamaika"
        "Japanese" -> "Jepang"
        "Kenyan" -> "Kenya"
        "Malaysian" -> "Malaysia"
        "Mexican" -> "Meksiko"
        "Moroccan" -> "Maroko"
        "Polish" -> "Polandia"
        "Portuguese" -> "Portugal"
        "Russian" -> "Rusia"
        "Spanish" -> "Spanyol"
        "Thai" -> "Thailand"
        "Tunisian" -> "Tunisia"
        "Turkish" -> "Turki"
        "Vietnamese" -> "Vietnam"
        else -> area
    }
}

@Composable
fun ModernTag(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: List<Color>
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent,
        modifier = Modifier.background(
            brush = Brush.linearGradient(gradient),
            shape = RoundedCornerShape(12.dp)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.White
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    itemCount: Int?,
    textColor: Color,
    gradientPrimary: Color,
    gradientSecondary: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    brush = Brush.linearGradient(listOf(gradientPrimary, gradientSecondary)),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
        itemCount?.let {
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = CircleShape,
                color = gradientPrimary.copy(alpha = 0.1f)
            ) {
                Text(
                    text = "$it",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = gradientPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun IngredientItem(
    ingredient: String,
    measure: String,
    index: Int,
    textColor: Color,
    gradientPrimary: Color,
    gradientSecondary: Color,
    dividerColor: Color,
    isDarkTheme: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = gradientPrimary.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$index",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = gradientPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = ingredient,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = textColor
            ),
            modifier = Modifier.weight(1f)
        )
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isDarkTheme) Color(0xFF3A3A3A) else Color(0xFFF8F9FE)
        ) {
            Text(
                text = measure,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = gradientSecondary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun InstructionStep(
    stepNumber: Int,
    instruction: String,
    textColor: Color,
    cardColor: Color,
    gradientPrimary: Color,
    gradientSecondary: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.05f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        brush = Brush.linearGradient(listOf(gradientPrimary, gradientSecondary)),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$stepNumber",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = instruction,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = textColor,
                    lineHeight = 22.sp
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ModernLoadingState(isIndonesian: Boolean = false, isDarkTheme: Boolean = false) {
    val textSecondary = if (isDarkTheme) Color(0xFFB0B0B0) else Color.Gray
    val gradientPrimary = if (isDarkTheme) Color(0xFF5a67d8) else Color(0xFF667eea)

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = gradientPrimary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
            )
            Text(
                if (isIndonesian) "Memuat resep..." else "Loading recipe...",
                style = MaterialTheme.typography.bodyMedium.copy(color = textSecondary)
            )
        }
    }
}

@Composable
fun ModernErrorState(
    error: String,
    onRetry: () -> Unit,
    isIndonesian: Boolean = false,
    isDarkTheme: Boolean = false
) {
    val textColor = if (isDarkTheme) Color(0xFFE0E0E0) else Color(0xFF1a1a2e)
    val textSecondary = if (isDarkTheme) Color(0xFFB0B0B0) else Color.Gray
    val gradientPrimary = if (isDarkTheme) Color(0xFF5a67d8) else Color(0xFF667eea)

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFFFF6B6B)
            )
            Text(
                if (isIndonesian) "Ada yang salah" else "Something went wrong",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            )
            Text(
                error,
                style = MaterialTheme.typography.bodyMedium,
                color = textSecondary
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = gradientPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (isIndonesian) "Coba Lagi" else "Try Again")
            }
        }
    }
}