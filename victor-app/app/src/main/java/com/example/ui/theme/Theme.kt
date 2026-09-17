package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VictorDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.Black,
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldLight,
    secondary = SkyBlue,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF0F384C),
    onSecondaryContainer = SkyBlue,
    tertiary = EmeraldGreen,
    onTertiary = Color.Black,
    background = DarkBg,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceCard,
    onSurfaceVariant = TextGray,
    outline = DarkBorder,
    error = CrimsonAlert,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Victor App is strictly Dark Mode with #14181B background as specified
    MaterialTheme(
        colorScheme = VictorDarkColorScheme,
        typography = Typography,
        content = content
    )
}
