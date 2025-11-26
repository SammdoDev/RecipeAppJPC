package com.samuel.recipeApp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light Theme Colors
private val LightPrimary = Color(0xFF667eea)
private val LightOnPrimary = Color.White
private val LightPrimaryContainer = Color(0xFF764ba2)
private val LightOnPrimaryContainer = Color.White

private val LightSecondary = Color(0xFFE91E63)
private val LightOnSecondary = Color.White
private val LightSecondaryContainer = Color(0xFFFCE4EC)
private val LightOnSecondaryContainer = Color(0xFF3E2723)

private val LightTertiary = Color(0xFF00BCD4)
private val LightOnTertiary = Color.White
private val LightTertiaryContainer = Color(0xFFB2EBF2)
private val LightOnTertiaryContainer = Color(0xFF006064)

private val LightError = Color(0xFFB00020)
private val LightOnError = Color.White
private val LightErrorContainer = Color(0xFFFDEDED)
private val LightOnErrorContainer = Color(0xFF370009)

private val LightBackground = Color(0xFFFAFAFA)
private val LightOnBackground = Color(0xFF1a1a2e)
private val LightSurface = Color.White
private val LightOnSurface = Color(0xFF1a1a2e)
private val LightSurfaceVariant = Color(0xFFF5F5F5)
private val LightOnSurfaceVariant = Color(0xFF49454F)

// Dark Theme Colors
private val DarkPrimary = Color(0xFF8b9de8)
private val DarkOnPrimary = Color(0xFF1a1a2e)
private val DarkPrimaryContainer = Color(0xFF5568c8)
private val DarkOnPrimaryContainer = Color.White

private val DarkSecondary = Color(0xFFFF6090)
private val DarkOnSecondary = Color(0xFF1a1a2e)
private val DarkSecondaryContainer = Color(0xFFC2185B)
private val DarkOnSecondaryContainer = Color.White

private val DarkTertiary = Color(0xFF4DD0E1)
private val DarkOnTertiary = Color(0xFF1a1a2e)
private val DarkTertiaryContainer = Color(0xFF00838F)
private val DarkOnTertiaryContainer = Color.White

private val DarkError = Color(0xFFCF6679)
private val DarkOnError = Color(0xFF1a1a2e)
private val DarkErrorContainer = Color(0xFF93000A)
private val DarkOnErrorContainer = Color(0xFFFFDAD6)

private val DarkBackground = Color(0xFF121212)
private val DarkOnBackground = Color(0xFFE4E4E4)
private val DarkSurface = Color(0xFF1E1E1E)
private val DarkOnSurface = Color(0xFFE4E4E4)
private val DarkSurfaceVariant = Color(0xFF2C2C2C)
private val DarkOnSurfaceVariant = Color(0xFFCAC4D0)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,

    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,

    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,

    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,

    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,

    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    scrim = Color(0xFF000000)
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,

    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,

    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,

    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,

    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,

    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),
    scrim = Color(0xFF000000)
)

@Composable
fun RecipeAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}