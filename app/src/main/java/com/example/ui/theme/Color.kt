package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Futuristic Matte Dark Palette (Default)
val DarkBackground = Color(0xFF090C11)
val DarkSurface = Color(0xFF111622)
val DarkSurfaceElevated = Color(0xFF161E2E)
val DarkKeypadSurface = Color(0xFF151C2A)
val DarkNumberKey = Color(0xFF1C2436)
val DarkNumberKeyText = Color(0xFFF1F5F9)
val DarkScientificKey = Color(0xFF131A26)
val DarkActionKey = Color(0xFF222B3F)

// Neon Accents
val NeonBlue = Color(0xFF4D96FF)
val NeonCyan = Color(0xFF00E5FF)
val NeonPurple = Color(0xFF9B51E0)
val NeonViolet = Color(0xFF7F00FF)
val NeonPink = Color(0xFFFF4D80)
val NeonGreen = Color(0xFF00E676)
val NeonOrange = Color(0xFFFF9100)

// Text Colors
val TextPrimaryDark = Color(0xFFF8FAFC)
val TextSecondaryDark = Color(0xFF8E9BAE)
val TextDimDark = Color(0xFF5B687C)

// Equals Neon Gradient
val EqualsGradient = Brush.linearGradient(
    colors = listOf(NeonViolet, NeonBlue, NeonCyan)
)

val ClearKeyGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFE53935), Color(0xFFFF6D00))
)

val AccentGlowBrush = Brush.radialGradient(
    colors = listOf(NeonBlue.copy(alpha = 0.25f), Color.Transparent)
)

// Minimalist Light Theme Palette
val LightBackground = Color(0xFFF4F6FB)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceElevated = Color(0xFFEAEFF8)
val LightKeypadSurface = Color(0xFFE4E9F2)
val LightNumberKey = Color(0xFFFFFFFF)
val LightNumberKeyText = Color(0xFF1A202C)
val LightScientificKey = Color(0xFFECEFF5)
val LightActionKey = Color(0xFFDDE3EE)

val TextPrimaryLight = Color(0xFF101828)
val TextSecondaryLight = Color(0xFF475467)
val TextDimLight = Color(0xFF98A2B3)
