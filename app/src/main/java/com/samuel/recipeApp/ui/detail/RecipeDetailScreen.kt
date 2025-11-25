package com.samuel.recipeApp.ui.detail

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage

private val GradientPrimary = Color(0xFF667eea)
private val GradientSecondary = Color(0xFF764ba2)
private val DarkText = Color(0xFF1a1a2e)

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

    LaunchedEffect(recipeId) {
        viewModel.loadRecipeDetail(recipeId)
    }

    // Auto translate ketika bahasa diubah ke Indonesia
    LaunchedEffect(isIndonesian) {
        if (isIndonesian && uiState.translatedMeal == null && uiState.recipe != null) {
            viewModel.translateRecipe()
        } else if (!isIndonesian) {
            viewModel.clearTranslation()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FE))) {
        when {
            uiState.isLoading -> ModernLoadingState(isIndonesian = isIndonesian)
            uiState.error != null -> ModernErrorState(
                error = uiState.error ?: "Unknown error",
                onRetry = { viewModel.loadRecipeDetail(recipeId) },
                isIndonesian = isIndonesian
            )
            uiState.recipe != null -> {
                ModernRecipeDetailContent(
                    recipe = uiState.recipe!!,
                    translatedMeal = uiState.translatedMeal,
                    scrollState = scrollState,
                    onBackPressed = onBackPressed,
                    isIndonesian = isIndonesian,
                    isTranslating = uiState.isTranslating,
                    onTranslateClick = { isIndonesian = !isIndonesian }
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
    onTranslateClick: () -> Unit
) {
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animatedProgress.animateTo(1f, animationSpec = tween(600, easing = FastOutSlowInEasing))
    }

    // Get ingredients dan instructions
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
                    .height(380.dp)
            ) {
                SubcomposeAsyncImage(
                    model = recipe.strMealThumb,
                    contentDescription = recipe.strMeal,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color(0xFFF0F0F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = GradientPrimary, strokeWidth = 3.dp)
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
                        .padding(top = 44.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBackPressed,
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DarkText
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Translate Button with Loading Indicator
                        IconButton(
                            onClick = onTranslateClick,
                            enabled = !isTranslating,
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    if (isIndonesian) GradientPrimary.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.9f),
                                    CircleShape
                                )
                        ) {
                            if (isTranslating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = if (isIndonesian) Color.White else GradientPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.Translate,
                                    contentDescription = "Translate",
                                    tint = if (isIndonesian) Color.White else GradientPrimary
                                )
                            }
                        }

                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.White.copy(alpha = 0.9f), CircleShape)
                        ) {
                            Icon(
                                Icons.Outlined.Share,
                                contentDescription = "Share",
                                tint = DarkText
                            )
                        }
                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.White.copy(alpha = 0.9f), CircleShape)
                        ) {
                            Icon(
                                Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = Color(0xFFE91E63)
                            )
                        }
                    }
                }

                // YouTube Button (if available)
                recipe.strYoutube?.let {
                    FloatingActionButton(
                        onClick = { /* Open YouTube */ },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .offset(y = 28.dp),
                        containerColor = Color(0xFFFF0000),
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.PlayArrow, if (isIndonesian) "Tonton Video" else "Watch Video")
                    }
                }
            }

            // Content Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-24).dp),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color(0xFFF8F9FE)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // Title & Info
                    Text(
                        text = recipe.strMeal,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkText
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
                                icon = Icons.Rounded.Restaurant,
                                gradient = listOf(GradientPrimary, GradientSecondary)
                            )
                        }
                        recipe.strArea?.let { area ->
                            ModernTag(
                                text = if (isIndonesian) translateArea(area) else area,
                                icon = Icons.Rounded.Public,
                                gradient = listOf(Color(0xFF11998e), Color(0xFF38ef7d))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Ingredients Section
                    SectionHeader(
                        title = if (isIndonesian) "Bahan-bahan" else "Ingredients",
                        icon = Icons.Rounded.ShoppingCart,
                        itemCount = ingredients.size
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(20.dp),
                                spotColor = GradientPrimary.copy(alpha = 0.15f)
                            ),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            ingredients.forEachIndexed { index, (ingredient, measure) ->
                                IngredientItem(
                                    ingredient = ingredient,
                                    measure = measure,
                                    index = index + 1
                                )
                                if (index < ingredients.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        color = Color(0xFFF0F0F0)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Instructions Section
                    SectionHeader(
                        title = if (isIndonesian) "Cara Memasak" else "Instructions",
                        icon = Icons.Rounded.MenuBook,
                        itemCount = null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        instructions.forEachIndexed { index, step ->
                            InstructionStep(
                                stepNumber = index + 1,
                                instruction = step.trim()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

// Helper functions tetap sama
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
fun ModernTag(text: String, icon: ImageVector, gradient: List<Color>) {
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
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = Color.White)
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
fun SectionHeader(title: String, icon: ImageVector, itemCount: Int?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    brush = Brush.linearGradient(listOf(GradientPrimary, GradientSecondary)),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
        )
        itemCount?.let {
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = CircleShape,
                color = GradientPrimary.copy(alpha = 0.1f)
            ) {
                Text(
                    text = "$it",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = GradientPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun IngredientItem(ingredient: String, measure: String, index: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = GradientPrimary.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$index",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = GradientPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = ingredient,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = DarkText
            ),
            modifier = Modifier.weight(1f)
        )
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF8F9FE)
        ) {
            Text(
                text = measure,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = GradientSecondary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun InstructionStep(stepNumber: Int, instruction: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.05f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        brush = Brush.linearGradient(listOf(GradientPrimary, GradientSecondary)),
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
                    color = DarkText,
                    lineHeight = 22.sp
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ModernLoadingState(isIndonesian: Boolean = false) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = GradientPrimary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                if (isIndonesian) "Memuat resep..." else "Loading recipe...",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
            )
        }
    }
}

@Composable
fun ModernErrorState(error: String, onRetry: () -> Unit, isIndonesian: Boolean = false) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text("😔", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                if (isIndonesian) "Ada yang salah" else "Something went wrong",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(error, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = GradientPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Rounded.Refresh, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isIndonesian) "Coba Lagi" else "Try Again")
            }
        }
    }
}