package com.example.lab5.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = DeepBlue,
    secondary = Sky,
    tertiary = Coral,
    background = Cream,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Night,
    onTertiary = Color.White,
    onBackground = Night,
    onSurface = Night,
    onSurfaceVariant = Slate
)

@Composable
fun Lab5Theme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
