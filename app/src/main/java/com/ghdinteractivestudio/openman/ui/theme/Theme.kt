package com.ghdinteractivestudio.openman.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ObsidianColorScheme = darkColorScheme(
    primary = AccentElectricBlue,
    onPrimary = TextPrimary,
    secondary = AccentCyan,
    onSecondary = TextDark,
    tertiary = AccentTurquoise,
    background = ObsidianBase,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = ObsidianElevated,
    onSurfaceVariant = TextSecondary,
    outline = GlassCardBorder
)

@Composable
fun OpenManTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ObsidianColorScheme,
        typography = Typography,
        content = content
    )
}
