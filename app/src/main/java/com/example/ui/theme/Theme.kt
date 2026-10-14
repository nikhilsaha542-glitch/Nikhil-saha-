package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
data class NovaColors(
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val keypadSurface: Color,
    val numberKey: Color,
    val numberKeyText: Color,
    val scientificKey: Color,
    val scientificKeyText: Color,
    val actionKey: Color,
    val actionKeyText: Color,
    val operatorKeyText: Color,
    val equalsGradient: Brush,
    val textPrimary: Color,
    val textSecondary: Color,
    val textDim: Color,
    val neonAccent: Color,
    val neonSecondary: Color,
    val isDark: Boolean
)

val LocalNovaColors = staticCompositionLocalOf {
    NovaColors(
        background = DarkBackground,
        surface = DarkSurface,
        surfaceElevated = DarkSurfaceElevated,
        keypadSurface = DarkKeypadSurface,
        numberKey = DarkNumberKey,
        numberKeyText = DarkNumberKeyText,
        scientificKey = DarkScientificKey,
        scientificKeyText = NeonBlue,
        actionKey = DarkActionKey,
        actionKeyText = NeonPink,
        operatorKeyText = NeonBlue,
        equalsGradient = EqualsGradient,
        textPrimary = TextPrimaryDark,
        textSecondary = TextSecondaryDark,
        textDim = TextDimDark,
        neonAccent = NeonBlue,
        neonSecondary = NeonPurple,
        isDark = true
    )
}

val DarkNovaColors = NovaColors(
    background = DarkBackground,
    surface = DarkSurface,
    surfaceElevated = DarkSurfaceElevated,
    keypadSurface = DarkKeypadSurface,
    numberKey = DarkNumberKey,
    numberKeyText = DarkNumberKeyText,
    scientificKey = DarkScientificKey,
    scientificKeyText = NeonBlue,
    actionKey = DarkActionKey,
    actionKeyText = NeonPink,
    operatorKeyText = NeonBlue,
    equalsGradient = EqualsGradient,
    textPrimary = TextPrimaryDark,
    textSecondary = TextSecondaryDark,
    textDim = TextDimDark,
    neonAccent = NeonBlue,
    neonSecondary = NeonPurple,
    isDark = true
)

val LightNovaColors = NovaColors(
    background = LightBackground,
    surface = LightSurface,
    surfaceElevated = LightSurfaceElevated,
    keypadSurface = LightKeypadSurface,
    numberKey = LightNumberKey,
    numberKeyText = LightNumberKeyText,
    scientificKey = LightScientificKey,
    scientificKeyText = Color(0xFF1976D2),
    actionKey = LightActionKey,
    actionKeyText = Color(0xFFD81B60),
    operatorKeyText = Color(0xFF0288D1),
    equalsGradient = EqualsGradient,
    textPrimary = TextPrimaryLight,
    textSecondary = TextSecondaryLight,
    textDim = TextDimLight,
    neonAccent = Color(0xFF0066FF),
    neonSecondary = Color(0xFF7B1FA2),
    isDark = false
)

private val M3DarkColorScheme = darkColorScheme(
    primary = NeonBlue,
    secondary = NeonPurple,
    tertiary = NeonPink,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark
)

private val M3LightColorScheme = lightColorScheme(
    primary = Color(0xFF0066FF),
    secondary = Color(0xFF7B1FA2),
    tertiary = Color(0xFFD81B60),
    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight
)

@Composable
fun NovaCalculatorTheme(
    isDarkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val novaColors = if (isDarkTheme) DarkNovaColors else LightNovaColors
    val m3Colors = if (isDarkTheme) M3DarkColorScheme else M3LightColorScheme

    CompositionLocalProvider(LocalNovaColors provides novaColors) {
        MaterialTheme(
            colorScheme = m3Colors,
            typography = Typography,
            content = content
        )
    }
}
