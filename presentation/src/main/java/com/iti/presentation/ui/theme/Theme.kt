package com.iti.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = ButtonPrimaryTextLight,
    primaryContainer = AccentLight,
    onPrimaryContainer = PrimaryLight,
    secondary = SecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = SearchFieldLight,
    onSecondaryContainer = TextPrimaryLight,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = CardLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight,
    outlineVariant = DividerLight,
    error = ErrorLight,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = ButtonPrimaryTextDark,
    primaryContainer = AccentDark,
    onPrimaryContainer = PrimaryDark,
    secondary = SecondaryDark,
    onSecondary = TextPrimaryDark,
    secondaryContainer = SearchFieldDark,
    onSecondaryContainer = TextPrimaryDark,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark,
    outlineVariant = DividerDark,
    error = ErrorDark,
    onError = Color.White
)

@Composable
fun ShopIQTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
