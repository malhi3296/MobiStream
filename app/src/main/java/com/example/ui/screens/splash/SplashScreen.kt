package com.example.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.StreamTagBadge
import com.example.ui.components.StreamTagType
import com.example.ui.theme.GlowLilac
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.StreamGradientEnd
import com.example.ui.theme.StreamGradientStart
import com.example.ui.theme.StreamViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isLoading: Boolean,
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }
    val glowAlpha = remember { Animatable(0.2f) }

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
            targetValue = 0.6f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(isLoading) {
        // Allow at least 1.8 seconds of splash animation for brand impact
        delay(1800)
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
                .size(260.dp)
                .alpha(glowAlpha.value)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            StreamGradientStart.copy(alpha = 0.5f),
                            StreamGradientEnd.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
                .padding(24.dp)
        ) {
            // App Logo Icon
            Image(
                painter = painterResource(id = R.drawable.ic_mobistream_logo),
                contentDescription = "MobiStream Logo",
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(24.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Brand Title: MobiStream
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Mobi",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    color = TextWhite,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Stream",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    color = GlowLilac,
                    letterSpacing = (-0.5).sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle Tagline
            Text(
                text = "Live TV, sports, movies and series.",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = TextWhite.copy(alpha = 0.9f)
            )
            Text(
                text = "Streaming made mobile.",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = GlowLilac
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Feature Badges
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StreamTagBadge(type = StreamTagType.LIVE)
                StreamTagBadge(type = StreamTagType.LIVE_TV, customText = "1000+ channels")
                StreamTagBadge(type = StreamTagType.QUALITY_4K, customText = "HD & FHD")
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Loading indicator
            CircularProgressIndicator(
                color = StreamViolet,
                trackColor = Color(0xFF1E174A),
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Loading your entertainment...",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = TextMuted
            )
        }
    }
}
