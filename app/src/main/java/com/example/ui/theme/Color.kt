package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Brand Colors
val StreamGradientStart = Color(0xFF9B6BFF)
val StreamGradientEnd = Color(0xFF4F2BC4)
val StreamViolet = Color(0xFF7B3FE4)
val MidnightBackground = Color(0xFF0E0A24)
val MidnightSurface = Color(0xFF161138)
val MidnightCard = Color(0xFF1E174A)
val MidnightCardBorder = Color(0xFF2C2266)
val GlowLilac = Color(0xFFB7A6FF)
val CloudSurface = Color(0xFFF5F3FF)
val CloudBackground = Color(0xFFFFFFFF)
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
val TextDarkPrimary = Color(0xFF1A153A)
val TextDarkSecondary = Color(0xFF6B658A)

// Gradient Brushes
val StreamPrimaryGradient = Brush.linearGradient(
    colors = listOf(StreamGradientStart, StreamGradientEnd)
)
val StreamHeroGradient = Brush.verticalGradient(
    colors = listOf(Color.Transparent, Color(0xCC0E0A24), MidnightBackground)
)
val GlowOverlay = Brush.radialGradient(
    colors = listOf(Color(0x339B6BFF), Color.Transparent)
)
