
package com.example.zbudget.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ZbudgetGreen,
    onPrimary = ZbudgetCream,
    secondary = ZbudgetGreenLight,
    onSecondary = ZbudgetGreenDark,
    tertiary = ZbudgetGreenLight,
    background = ZbudgetText,
    onBackground = ZbudgetCream,
    surface = ZbudgetText,
    onSurface = ZbudgetCream,
    onSurfaceVariant = ZbudgetSecondary,
    error = ZbudgetRed
)

private val LightColorScheme = lightColorScheme(
    primary = ZbudgetGreen,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = ZbudgetGreenLight,
    onPrimaryContainer = ZbudgetGreenDark,
    secondary = ZbudgetGreenDark,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    tertiary = ZbudgetGreenLight,
    background = ZbudgetCream,
    onBackground = ZbudgetText,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = ZbudgetText,
    onSurfaceVariant = ZbudgetSecondary,
    error = ZbudgetRed,
    onError = androidx.compose.ui.graphics.Color.White
)

@Composable
fun ZbudgetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
