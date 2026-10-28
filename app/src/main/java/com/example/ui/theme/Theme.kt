package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MaxoDarkColorScheme = darkColorScheme(
    primary = MaxoWhite,
    onPrimary = MaxoBlack,
    primaryContainer = MaxoLineLight,
    onPrimaryContainer = MaxoFg,
    secondary = MaxoMuted,
    onSecondary = MaxoBlack,
    secondaryContainer = MaxoLine,
    onSecondaryContainer = MaxoFg,
    tertiary = MaxoCyan,
    background = MaxoBlack,
    onBackground = MaxoFg,
    surface = MaxoCardBgStart,
    onSurface = MaxoFg,
    surfaceVariant = MaxoCardBgEnd,
    onSurfaceVariant = MaxoMuted,
    outline = MaxoLine,
    outlineVariant = MaxoLineLight
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MaxoDarkColorScheme,
        typography = MaxoTypography,
        content = content
    )
}
