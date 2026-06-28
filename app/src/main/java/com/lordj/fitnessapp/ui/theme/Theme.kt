package com.lordj.fitnessapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.lordj.fitnessapp.data.preferences.ThemeMode

private val DarkColors = darkColorScheme(
    primary = Indigo400,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Indigo400,
    secondary = Emerald500,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = Emerald300,
    tertiary = Amber500,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = Color(0xFFFDE68A),
    background = Slate900,
    surface = Slate800,
    surfaceVariant = Slate700,
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF94A3B8),
    error = Rose500,
    outline = Color(0xFF475569)
)

private val LightColors = lightColorScheme(
    primary = Indigo500,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Indigo700,
    secondary = Emerald500,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF064E3B),
    tertiary = Amber500,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF78350F),
    background = Slate100,
    surface = Color.White,
    surfaceVariant = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF475569),
    error = Rose500,
    outline = Color(0xFFCBD5E1)
)

@Composable
fun FitnessTrackerTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val colors = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(colorScheme = colors, typography = Typography, content = content)
}
