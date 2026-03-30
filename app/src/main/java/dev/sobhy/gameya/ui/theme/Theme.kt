package dev.sobhy.gameya.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = FinancePrimary,
    onPrimary = OnDark,
    secondary = FinanceSecondary,
    onSecondary = OnDark,
    tertiary = StatusSuccess,
    background = Color(0xFF0F172A),
    onBackground = OnDark,
    surface = Color(0xFF111827),
    onSurface = OnDark,
    error = StatusWarning,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = OnDark
)

private val LightColorScheme = lightColorScheme(
    primary = FinancePrimary,
    onPrimary = OnDark,
    secondary = FinanceSecondary,
    onSecondary = OnDark,
    tertiary = StatusSuccess,
    background = FinanceBackground,
    onBackground = OnLight,
    surface = FinanceSurface,
    onSurface = OnLight,
    error = StatusWarning,
    primaryContainer = Color(0xFFE8F0FE),
    onPrimaryContainer = Color(0xFF0B3D91),
    secondaryContainer = Color(0xFFE3F2FD),
    onSecondaryContainer = Color(0xFF0A3A76)
)

@Composable
fun GameyaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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