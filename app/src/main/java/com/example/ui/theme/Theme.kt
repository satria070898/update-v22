package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    themeColor: Color = AccentBlue,
    content: @Composable () -> Unit
) {
    val darkScheme = darkColorScheme(
        primary = themeColor,
        onPrimary = Color.White,
        primaryContainer = themeColor.copy(alpha = 0.25f),
        onPrimaryContainer = Color.White,
        secondary = themeColor,
        onSecondary = Color.White,
        background = DarkBackground,
        onBackground = TextWhite,
        surface = DarkSurface,
        onSurface = TextWhite,
        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = TextMuted,
        outline = DarkCardBorder,
        error = ExpenseRed,
        onError = Color.White
    )

    val lightScheme = lightColorScheme(
        primary = themeColor,
        onPrimary = Color.White,
        primaryContainer = themeColor.copy(alpha = 0.15f),
        onPrimaryContainer = themeColor,
        secondary = themeColor,
        background = Color(0xFFF1F5F9),
        onBackground = Color(0xFF0F172A),
        surface = Color.White,
        onSurface = Color(0xFF0F172A),
        surfaceVariant = Color(0xFFE2E8F0),
        onSurfaceVariant = Color(0xFF475569),
        outline = Color(0xFFCBD5E1),
        error = ExpenseRed,
        onError = Color.White
    )

    val colorScheme = if (darkTheme) darkScheme else lightScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
