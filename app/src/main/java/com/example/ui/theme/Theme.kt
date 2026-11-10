package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF19191B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF3F4F8),
    onPrimaryContainer = Color(0xFF19191B),
    secondary = StreamViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEFEFF6),
    onSecondaryContainer = Color(0xFF19191B),
    tertiary = TagLiveRed,
    onTertiary = Color.White,
    background = AppLightBackground,
    onBackground = TextDarkPrimary,
    surface = AppLightBackground,
    onSurface = TextDarkPrimary,
    surfaceVariant = Color(0xFFE4E4E7),
    onSurfaceVariant = TextDarkSecondary,
    outline = Color(0xFFA1A1AA)
)

private val DarkColorScheme = darkColorScheme(
    primary = StreamViolet,
    onPrimary = TextWhite,
    primaryContainer = MidnightCard,
    onPrimaryContainer = GlowLilac,
    secondary = GlowLilac,
    onSecondary = AppDarkBackground,
    secondaryContainer = MidnightCardBorder,
    onSecondaryContainer = TextWhite,
    tertiary = TagLiveRed,
    onTertiary = TextWhite,
    background = AppDarkBackground,
    onBackground = TextWhite,
    surface = AppDarkBackground,
    onSurface = TextWhite,
    surfaceVariant = MidnightCard,
    onSurfaceVariant = TextMuted,
    outline = MidnightCardBorder
)

@Composable
fun MobiStreamTheme(
    darkTheme: Boolean = false, // Clean white aesthetic from user UI mockup
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
