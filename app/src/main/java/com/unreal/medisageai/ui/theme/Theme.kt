package com.unreal.medisageai.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = MediNavy,
    onPrimary = MediOnNavy,
    primaryContainer = MediUserBubble,
    onPrimaryContainer = MediOnUserBubble,
    secondary = MediBlueAccent,
    onSecondary = Color.White,
    secondaryContainer = MediAiBubble,
    onSecondaryContainer = MediOnAiBubble,
    background = MediBackground,
    onBackground = MediTextPrimary,
    surface = MediCard,
    onSurface = MediTextPrimary,
    surfaceVariant = MediFieldFill,
    onSurfaceVariant = MediTextSecondary,
    outline = MediFieldBorder,
    outlineVariant = MediFieldBorder,
    error = MediCritical,
    onError = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = MediNavyLightPrimary,
    onPrimary = MediNavyDark,
    primaryContainer = MediBlueAccent,
    onPrimaryContainer = MediOnUserBubble,
    secondary = MediNavyLightPrimary,
    onSecondary = MediNavyDark,
    secondaryContainer = MediDarkAiBubble,
    onSecondaryContainer = Color(0xFFE6ECF5),
    background = MediDarkBackground,
    onBackground = Color(0xFFE6ECF5),
    surface = MediDarkSurface,
    onSurface = Color(0xFFE6ECF5),
    surfaceVariant = Color(0xFF1B2A40),
    onSurfaceVariant = Color(0xFFA9B6C9),
    outline = Color(0xFF2B3C56),
    outlineVariant = Color(0xFF2B3C56),
    error = Color(0xFFFF6B6B),
    onError = MediNavyDark,
)

@Composable
fun MediSageAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled: the clinical navy brand must render identically across devices
    // for visual parity with the mockups, rather than adapting to the user's wallpaper.
    dynamicColor: Boolean = false,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
