package com.samuel.recipeApp.ui.login

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.samuel.recipeApp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LoginButton(
    isLoading: Boolean,
    surfaceColor: Color,
    textColor: Color,
    gradientColors: List<Color>,
    googleWebClientId: String,
    onErrorMessage: (String?) -> Unit,
    onLoadingChange: (Boolean) -> Unit,
    activity: ComponentActivity,
    credentialManager: CredentialManager,
    auth: FirebaseAuth,
    onSuccess: () -> Unit,
    context: android.content.Context
) {
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            if (!isLoading) {
                if (googleWebClientId.isEmpty() || googleWebClientId == "YOUR_WEB_CLIENT_ID_HERE.apps.googleusercontent.com") {
                    onErrorMessage("Google Web Client ID not configured. Please check strings.xml")
                    return@Button
                }

                onErrorMessage(null)
                scope.launch {
                    onLoadingChange(true)
                    signInWithGoogle(
                        activity = activity,
                        credentialManager = credentialManager,
                        googleWebClientId = googleWebClientId,
                        auth = auth,
                        onSuccess = {
                            onLoadingChange(false)
                            onSuccess()
                        },
                        onError = { error ->
                            onLoadingChange(false)
                            onErrorMessage(error)
                            Toast.makeText(
                                context,
                                "Sign in failed: $error",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(
                elevation = if (isLoading) 0.dp else 12.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = gradientColors[0].copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = surfaceColor,
            contentColor = textColor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        enabled = !isLoading,
        contentPadding = PaddingValues(0.dp)
    ) {
        if (isLoading) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = gradientColors[0],
                    strokeWidth = 3.dp
                )
                Text(
                    text = "Signing in...",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Google Icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .shadow(2.dp, CircleShape)
                        .background(Color.White, CircleShape)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google Icon",
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.Unspecified
                    )
                }

                Text(
                    text = "Continue with Google",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    activity: ComponentActivity,
    credentialManager: CredentialManager,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val auth = FirebaseAuth.getInstance()
    val isDarkTheme = isSystemInDarkTheme()

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    val fadeInAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        fadeInAlpha.animateTo(1f, animationSpec = tween(1000))
    }

    // Theme colors
    val backgroundColor = if (isDarkTheme) Color(0xFF0F0F1E) else Color(0xFFF8F9FE)
    val surfaceColor = if (isDarkTheme) Color(0xFF1A1A2E) else Color.White
    val textColor = if (isDarkTheme) Color(0xFFE0E0E0) else Color(0xFF1a1a2e)
    val textSecondary = if (isDarkTheme) Color(0xFFB0B0B0) else Color(0xFF64748B)

    val gradientColors = if (isDarkTheme) listOf(
        Color(0xFF1a237e),
        Color(0xFF311b92),
        Color(0xFF4a148c)
    ) else listOf(
        Color(0xFF667eea),
        Color(0xFF764ba2),
        Color(0xFFf093fb)
    )

    // Read Web Client ID
    val googleWebClientId = remember {
        try {
            context.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        if (isDarkTheme) Color(0xFF16213E) else Color(0xFFEEF2FF)
                    )
                )
            )
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-100).dp)
                .alpha(0.1f)
                .background(
                    brush = Brush.radialGradient(gradientColors),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 100.dp)
                .alpha(0.1f)
                .background(
                    brush = Brush.radialGradient(gradientColors.reversed()),
                    shape = CircleShape
                )
        )

        if (isLandscape) {
            // Landscape Layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .alpha(fadeInAlpha.value),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(48.dp)
            ) {
                // Left Side - Branding
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Logo Section
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .scale(logoScale)
                            .shadow(
                                elevation = 16.dp,
                                shape = CircleShape
                            )
                            .background(
                                brush = Brush.linearGradient(gradientColors),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Restaurant,
                            contentDescription = "App Logo",
                            modifier = Modifier.size(50.dp),
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "Welcome!",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp,
                            color = textColor,
                            letterSpacing = (-0.5).sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Sign in to discover amazing recipes\nand cook delicious meals",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = textSecondary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    // Feature highlights
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        FeatureItem(
                            icon = Icons.Outlined.RestaurantMenu,
                            text = "10,000+\nRecipes",
                            gradientColors = gradientColors,
                            isDarkTheme = isDarkTheme
                        )
                        FeatureItem(
                            icon = Icons.Outlined.Search,
                            text = "Easy\nSearch",
                            gradientColors = gradientColors,
                            isDarkTheme = isDarkTheme
                        )
                        FeatureItem(
                            icon = Icons.Outlined.Favorite,
                            text = "Save\nFavorites",
                            gradientColors = gradientColors,
                            isDarkTheme = isDarkTheme
                        )
                    }
                }

                // Right Side - Login Form
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Error message
                    androidx.compose.animation.AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically(),
                        exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically()
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFEBEE)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ErrorOutline,
                                    contentDescription = null,
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = errorMessage ?: "",
                                    color = Color(0xFFD32F2F),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    LoginButton(
                        isLoading = isLoading,
                        surfaceColor = surfaceColor,
                        textColor = textColor,
                        gradientColors = gradientColors,
                        googleWebClientId = googleWebClientId,
                        onErrorMessage = { errorMessage = it },
                        onLoadingChange = { isLoading = it },
                        activity = activity,
                        credentialManager = credentialManager,
                        auth = auth,
                        onSuccess = onSuccess,
                        context = context
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = textSecondary.copy(alpha = 0.2f)
                        )
                        Text(
                            text = "OR",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = textSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = textSecondary.copy(alpha = 0.2f)
                        )
                    }

                    Text(
                        text = "By continuing, you agree to our Terms of Service and Privacy Policy",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = textSecondary
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Portrait Layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .alpha(fadeInAlpha.value),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.weight(1f))

                // Logo Section
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(logoScale)
                        .shadow(
                            elevation = 16.dp,
                            shape = CircleShape
                        )
                        .background(
                            brush = Brush.linearGradient(gradientColors),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Restaurant,
                        contentDescription = "App Logo",
                        modifier = Modifier.size(60.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Welcome Text
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Welcome!",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 36.sp,
                            color = textColor,
                            letterSpacing = (-0.5).sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Sign in to discover amazing recipes\nand cook delicious meals",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = textSecondary,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Error message
                androidx.compose.animation.AnimatedVisibility(
                    visible = errorMessage != null,
                    enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically(),
                    exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ErrorOutline,
                                contentDescription = null,
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = errorMessage ?: "",
                                color = Color(0xFFD32F2F),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                LoginButton(
                    isLoading = isLoading,
                    surfaceColor = surfaceColor,
                    textColor = textColor,
                    gradientColors = gradientColors,
                    googleWebClientId = googleWebClientId,
                    onErrorMessage = { errorMessage = it },
                    onLoadingChange = { isLoading = it },
                    activity = activity,
                    credentialManager = credentialManager,
                    auth = auth,
                    onSuccess = onSuccess,
                    context = context
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Alternative sign-in options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = textSecondary.copy(alpha = 0.2f)
                    )
                    Text(
                        text = "OR",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = textSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = textSecondary.copy(alpha = 0.2f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Feature highlights
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FeatureItem(
                        icon = Icons.Outlined.RestaurantMenu,
                        text = "10,000+\nRecipes",
                        gradientColors = gradientColors,
                        isDarkTheme = isDarkTheme
                    )
                    FeatureItem(
                        icon = Icons.Outlined.Search,
                        text = "Easy\nSearch",
                        gradientColors = gradientColors,
                        isDarkTheme = isDarkTheme
                    )
                    FeatureItem(
                        icon = Icons.Outlined.Favorite,
                        text = "Save\nFavorites",
                        gradientColors = gradientColors,
                        isDarkTheme = isDarkTheme
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "By continuing, you agree to our Terms of Service and Privacy Policy",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = textSecondary
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    gradientColors: List<Color>,
    isDarkTheme: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = CircleShape
                )
                .background(
                    brush = Brush.linearGradient(gradientColors.take(2)),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                color = if (isDarkTheme) Color(0xFFB0B0B0) else Color(0xFF64748B),
                lineHeight = 16.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}

// Google Sign-In functions remain the same
private suspend fun signInWithGoogle(
    activity: ComponentActivity,
    credentialManager: CredentialManager,
    googleWebClientId: String,
    auth: FirebaseAuth,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    try {
        android.util.Log.d("GoogleSignIn", "Starting sign in with clientId: $googleWebClientId")

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(googleWebClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        android.util.Log.d("GoogleSignIn", "Getting credential...")

        val result = credentialManager.getCredential(
            request = request,
            context = activity
        )

        android.util.Log.d("GoogleSignIn", "Credential received, handling sign in...")

        handleSignIn(result, auth, onSuccess, onError)

    } catch (e: androidx.credentials.exceptions.GetCredentialCancellationException) {
        android.util.Log.e("GoogleSignIn", "User cancelled sign in", e)
        onError("Sign in cancelled")
    } catch (e: androidx.credentials.exceptions.NoCredentialException) {
        android.util.Log.e("GoogleSignIn", "No credentials available", e)
        onError("No Google accounts found. Please add a Google account to your device.")
    } catch (e: Exception) {
        android.util.Log.e("GoogleSignIn", "Sign in failed", e)
        onError(e.message ?: "Unknown error occurred")
    }
}

private suspend fun handleSignIn(
    result: GetCredentialResponse,
    auth: FirebaseAuth,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    when (val credential = result.credential) {
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)

                    val googleIdToken = googleIdTokenCredential.idToken

                    val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    auth.signInWithCredential(firebaseCredential).await()

                    onSuccess()
                } catch (e: Exception) {
                    onError(e.message ?: "Authentication failed")
                }
            } else {
                onError("Unexpected credential type")
            }
        }
        else -> {
            onError("Unexpected credential type")
        }
    }
}