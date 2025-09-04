package com.Fanuel.zobi.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DivoDarkColorScheme = darkColorScheme(
    primary = DivoGreen,
    onPrimary = Color.Black, // Better contrast on green
    primaryContainer = DivoGreenVariant,
    onPrimaryContainer = Color.Black,
    secondary = DivoBlue,
    onSecondary = WhiteText,
    secondaryContainer = DivoBlueVariant,
    onSecondaryContainer = WhiteText,
    tertiary = AccentPurple,
    onTertiary = WhiteText,
    tertiaryContainer = AccentPurple.copy(alpha = 0.2f),
    onTertiaryContainer = WhiteText,
    background = DarkBackground,
    onBackground = WhiteText,
    surface = DarkSurface,
    onSurface = WhiteText,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = WhiteTextSecondary,
    surfaceTint = DivoGreen,
    outline = DarkSurfaceVariant2,
    outlineVariant = DarkSurfaceVariant2.copy(alpha = 0.5f),
    error = ErrorRed,
    onError = Color.Black,
    errorContainer = ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = ErrorRed,
    scrim = Color.Black.copy(alpha = 0.32f),
    inverseSurface = WhiteText,
    inverseOnSurface = DarkBackground,
    inversePrimary = DivoGreen
)

private val DivoLightColorScheme = lightColorScheme(
    primary = DivoGreen,
    secondary = DivoBlue,
    tertiary = DivoGreen,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onPrimary = WhiteText,
    onSecondary = WhiteText,
    onTertiary = WhiteText,
    onBackground = WhiteText,
    onSurface = WhiteText,
    onSurfaceVariant = WhiteTextSecondary,
    error = ErrorRed,
    onError = WhiteText
)

@Composable
fun DivoTheme(
    darkTheme: Boolean = true, // Always use dark theme for DIVO
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DivoDarkColorScheme
        else -> DivoLightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}