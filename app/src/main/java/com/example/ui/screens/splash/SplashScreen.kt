package com.example.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.StreamGradientEnd
import com.example.ui.theme.StreamGradientStart
import kotlinx.coroutines.delay

/**
 * Minimalist, high-end Splash Screen featuring solely the centered MobiStream app logo
 * with a soft violet pulse glow on Midnight Background.
 */
@Composable
fun SplashScreen(
    isLoading: Boolean,
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.75f) }
    val alpha = remember { Animatable(0f) }
    val glowAlpha = remember { Animatable(0.25f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600)
        )
        glowAlpha.animateTo(
            targetValue = 0.65f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(isLoading) {
        delay(1400)
        if (!isLoading) {
            onSplashFinished()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Soft violet ambient glow behind logo
        Box(
            modifier = Modifier
                .size(240.dp)
                .alpha(glowAlpha.value)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            StreamGradientStart.copy(alpha = 0.55f),
                            StreamGradientEnd.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Only App Logo with smooth scale and entrance
        Image(
            painter = painterResource(id = R.drawable.ic_mobistream_logo),
            contentDescription = "MobiStream Logo",
            modifier = Modifier
                .size(130.dp)
                .scale(scale.value)
                .alpha(alpha.value)
                .clip(RoundedCornerShape(28.dp))
                .testTag("splash_logo")
        )
    }
}
