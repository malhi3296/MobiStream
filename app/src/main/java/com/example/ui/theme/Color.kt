package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Brand Colors
val StreamGradientStart = Color(0xFF9B6BFF)
val StreamGradientEnd = Color(0xFF4F2BC4)
val StreamViolet = Color(0xFF7B3FE4)
val AppLightBackground = Color(0xFFD4D4D8)
val AppDarkBackground = Color(0xFF3F3F46)
val MidnightBackground = Color(0xFF3F3F46)
val MidnightSurface = Color(0xFF27272A)
val MidnightCard = Color(0xFF2E2E33)
val MidnightCardBorder = Color(0xFF52525B)
val GlowLilac = Color(0xFFB7A6FF)
val CloudSurface = Color(0xFFE4E4E7)
val CloudBackground = Color(0xFFD4D4D8)
val LiveRed = Color(0xFFE5334B)

// UI Tag Colors
val TagLiveRed = Color(0xFFE5334B)
val TagLiveTvBlue = Color(0xFF2B7FFF)
val TagMoviesGreen = Color(0xFF10B981)
val TagSeriesOrange = Color(0xFFF59E0B)
val Tag4kPurple = Color(0xFF8B5CF6)

// Text Colors
val TextWhite = Color(0xFFFFFFFF)
val TextMuted = Color(0xFFA19BD0)
val TextDarkPrimary = Color(0xFF19191B)
val TextDarkSecondary = Color(0xFF82828E)
val CardLightBg = Color(0xFFF5F6FA)
val BorderLight = Color(0xFFEEEEF4)

// Genre Orb Gradients
val GenreSportsGradient = Brush.radialGradient(listOf(Color(0xFFFF5252), Color(0xFFFF7A7A), Color(0x33FF5252)))
val GenreNewsGradient = Brush.radialGradient(listOf(Color(0xFF3867D6), Color(0xFF4B7BEC), Color(0x333867D6)))
val GenreMoviesGradient = Brush.radialGradient(listOf(Color(0xFF8854D0), Color(0xFFA55EEA), Color(0x338854D0)))
val GenreMusicGradient = Brush.radialGradient(listOf(Color(0xFFFA8231), Color(0xFFFD9644), Color(0x33FA8231)))
val GenreKidsGradient = Brush.radialGradient(listOf(Color(0xFF20BF6B), Color(0xFF26DE81), Color(0x3320BF6B)))

// Gradient Brushes
val StreamPrimaryGradient = Brush.linearGradient(
    colors = listOf(StreamGradientStart, StreamGradientEnd)
)
val StreamHeroGradient = Brush.verticalGradient(
    colors = listOf(Color.Transparent, Color(0xCC0E0A24), MidnightBackground)
)
