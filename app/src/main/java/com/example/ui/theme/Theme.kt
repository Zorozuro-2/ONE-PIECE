package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CyberZenColorScheme = darkColorScheme(
    primary = CyberGreen,
    onPrimary = DarkBg,
    primaryContainer = ForestZen,
    onPrimaryContainer = TextWhite,
    secondary = ForestZen,
    onSecondary = TextWhite,
    background = DarkBg,
    onBackground = TextWhite,
    surface = DarkCard,
    onSurface = TextWhite,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextMuted,
    outline = BorderGreen,
    error = ErrorRed,
    onError = TextWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark Mode for the Cyber-Zen atmosphere by default
    dynamicColor: Boolean = false, // Disable dynamic colors to preserve our brand color palette
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberZenColorScheme,
        typography = Typography,
        content = content
    )
}
