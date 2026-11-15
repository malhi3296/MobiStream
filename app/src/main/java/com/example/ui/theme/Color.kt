package com.example.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Dark theme tokens from tokens.json
val DarkBg = Color(0xFF161C2A)
val DarkSurface = Color(0xFF212B3D)
val DarkBorder = Color(0xFF33415C)
val DarkSelected = Color(0xFF1D3558)
val DarkAccent = Color(0xFF3B9BF5)
val DarkAccentText = Color(0xFF7FBFFF)
val DarkText = Color(0xFFF1F5FB)
val DarkMuted = Color(0xFFB8C3D6)
val DarkOnAccent = Color(0xFF06152B)
val DarkNav = Color(0xFF243049)

// Light theme tokens from tokens.json
val LightBg = Color(0xFFF6F9FE)
val LightSurface = Color(0xFFFFFFFF)
val LightBorder = Color(0xFFD3E2F4)
val LightSelected = Color(0xFFE3F0FF)
val LightAccent = Color(0xFF1473D6)
val LightAccentText = Color(0xFF0B5CB5)
val LightText = Color(0xFF0E2342)
val LightMuted = Color(0xFF51627C)
val LightOnAccent = Color(0xFFFFFFFF)
val LightNav = Color(0xFFFFFFFF)

// Shared tokens
val LiveBadgeRed = Color(0xFFE0263F)
val HeroBlueStart = Color(0xFF1E6FD9)
val HeroBlueEnd = Color(0xFF0F2F5C)

val ChannelTilePalette = listOf(
    Color(0xFF1E6FD9),
    Color(0xFF0E7C86),
    Color(0xFF3B5BDB),
    Color(0xFF0B5FA5),
    Color(0xFF2563EB),
    Color(0xFF0F766E),
    Color(0xFF4F46E5),
    Color(0xFF0369A1),
    Color(0xFF1D4ED8)
)

@Immutable
data class MobiColors(
    val accentText: Color,
    val navPill: Color,
    val liveBadge: Color,
    val heroGradientStart: Color,
    val heroGradientEnd: Color,
    val heroGradient: Brush,
    val channelTileColors: List<Color>,
    val isDark: Boolean
)

val DarkMobiColors = MobiColors(
    accentText = DarkAccentText,
    navPill = DarkNav,
    liveBadge = LiveBadgeRed,
    heroGradientStart = HeroBlueStart,
    heroGradientEnd = HeroBlueEnd,
    heroGradient = Brush.linearGradient(colors = listOf(HeroBlueStart, HeroBlueEnd)),
    channelTileColors = ChannelTilePalette,
    isDark = true
)

val LightMobiColors = MobiColors(
    accentText = LightAccentText,
    navPill = LightNav,
    liveBadge = LiveBadgeRed,
    heroGradientStart = HeroBlueStart,
    heroGradientEnd = HeroBlueEnd,
    heroGradient = Brush.linearGradient(colors = listOf(HeroBlueStart, HeroBlueEnd)),
    channelTileColors = ChannelTilePalette,
    isDark = false
)

val LocalMobiColors = staticCompositionLocalOf { DarkMobiColors }

val DarkColorScheme = darkColorScheme(
    primary = DarkAccent,
    onPrimary = DarkOnAccent,
    primaryContainer = DarkSelected,
    onPrimaryContainer = DarkAccentText,
    secondary = DarkAccent,
    onSecondary = DarkOnAccent,
    secondaryContainer = DarkSelected,
    onSecondaryContainer = DarkText,
    tertiary = LiveBadgeRed,
    onTertiary = Color.White,
    background = DarkBg,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText,
    surfaceContainer = DarkSurface,
    surfaceContainerLow = DarkBg,
    surfaceContainerHigh = DarkSelected,
    surfaceVariant = DarkSelected,
    onSurfaceVariant = DarkMuted,
    outline = DarkBorder,
    outlineVariant = DarkBorder,
    error = LiveBadgeRed,
    onError = Color.White
)

val LightColorScheme = lightColorScheme(
    primary = LightAccent,
    onPrimary = LightOnAccent,
    primaryContainer = LightSelected,
    onPrimaryContainer = LightAccentText,
    secondary = LightAccent,
    onSecondary = LightOnAccent,
    secondaryContainer = LightSelected,
    onSecondaryContainer = LightText,
    tertiary = LiveBadgeRed,
    onTertiary = Color.White,
    background = LightBg,
    onBackground = LightText,
    surface = LightSurface,
    onSurface = LightText,
    surfaceContainer = LightSurface,
    surfaceContainerLow = LightBg,
    surfaceContainerHigh = LightSelected,
    surfaceVariant = LightSelected,
    onSurfaceVariant = LightMuted,
    outline = LightBorder,
    outlineVariant = LightBorder,
    error = LiveBadgeRed,
    onError = Color.White
)
