package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = StreamViolet,
    onPrimary = TextWhite,
    primaryContainer = MidnightCard,
    onPrimaryContainer = GlowLilac,
    secondary = GlowLilac,
    onSecondary = MidnightBackground,
    secondaryContainer = MidnightCardBorder,
    onSecondaryContainer = TextWhite,
    tertiary = TagLiveRed,
    onTertiary = TextWhite,
    background = MidnightBackground,
    onBackground = TextWhite,
    surface = MidnightSurface,
    onSurface = TextWhite,
    surfaceVariant = MidnightCard,
    onSurfaceVariant = TextMuted,
    outline = MidnightCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = StreamViolet,
    onPrimary = TextWhite,
    primaryContainer = StreamGradientStart.copy(alpha = 0.2f),
    onPrimaryContainer = StreamGradientEnd,
    secondary = StreamViolet,
    onSecondary = TextWhite,
    secondaryContainer = CloudSurface,
    onSecondaryContainer = TextDarkPrimary,
    tertiary = TagLiveRed,
    onTertiary = TextWhite,
    background = CloudBackground,
    onBackground = TextDarkPrimary,
    surface = CloudSurface,
    onSurface = TextDarkPrimary,
    surfaceVariant = Color(0xFFEBE7FD),
    onSurfaceVariant = TextDarkSecondary,
    outline = Color(0xFFD3CCE8)
)

@Composable
fun MobiStreamTheme(
    darkTheme: Boolean = true, // Default to Midnight brand aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
