
package com.example.zbudget.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8BD5A5),
    onPrimary = Color(0xFF103B25),
    primaryContainer = Color(0xFF145C38),
    onPrimaryContainer = Color(0xFFD7F5DF),
    secondary = Color(0xFFB5DCC0),
    onSecondary = Color(0xFF193B27),
    tertiary = Color(0xFFFFD180),
    background = Color(0xFF121713),
    onBackground = Color(0xFFE8F0E9),
    surface = Color(0xFF1B211D),
    onSurface = Color(0xFFE8F0E9),
    surfaceVariant = Color(0xFF303A33),
    onSurfaceVariant = Color(0xFFC0CBC2),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    outline = Color(0xFF89958C)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF218653),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE5F4E9),
    onPrimaryContainer = Color(0xFF145C38),
    secondary = Color(0xFF477A58),
    onSecondary = Color.White,
    tertiary = Color(0xFF8A5B00),
    background = Color(0xFFFFFBF4),
    onBackground = Color(0xFF202820),
    surface = Color.White,
    onSurface = Color(0xFF202820),
    surfaceVariant = Color(0xFFE8EEE9),
    onSurfaceVariant = Color(0xFF59645C),
    error = Color(0xFFD32F2F),
    onError = Color.White,
    outline = Color(0xFF7A827A)
)

@Composable
fun ZbudgetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
