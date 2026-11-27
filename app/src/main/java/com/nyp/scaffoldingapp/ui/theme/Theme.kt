package com.nyp.scaffoldingapp.ui.theme

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
    primary = Color(0xFF9BC7F5),
    onPrimary = Color(0xFF101216),

    secondary = Color(0xFFB6C4D4),
    onSecondary = Color(0xFF101216),

    secondaryContainer = Color(0xFF34465A),
    onSecondaryContainer = Color(0xFFDCEAFF),

    background = Color(0xFF101216),
    onBackground = Color(0xFFF2F5F9),

    surface = Color(0xFF222A33),
    onSurface = Color(0xFFF2F5F9),

    surfaceVariant = Color(0xFF29333E),
    onSurfaceVariant = Color(0xFFA6B0BD),

    outline = Color(0xFF536170)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF365B80),
    onPrimary = Color(0xFFFFFFFF),

    secondary = Color(0xFF536779),
    onSecondary = Color(0xFFFFFFFF),

    secondaryContainer = Color(0xFFD2E2F0),
    onSecondaryContainer = Color(0xFF243E55),

    background = Color(0xFFE4EDF5),
    onBackground = Color(0xFF192B3C),

    surface = Color(0xFFF2F7FC),
    onSurface = Color(0xFF192B3C),

    surfaceVariant = Color(0xFFDCE7F0),
    onSurfaceVariant = Color(0xFF536779),

    outline = Color(0xFF899BAB)
)

@Composable
fun ScaffoldingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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