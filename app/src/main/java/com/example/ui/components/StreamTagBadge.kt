package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.Tag4kPurple
import com.example.ui.theme.TagLiveRed
import com.example.ui.theme.TagLiveTvBlue
import com.example.ui.theme.TagMoviesGreen
import com.example.ui.theme.TagSeriesOrange

enum class StreamTagType {
    LIVE,
    LIVE_TV,
    MOVIES,
    SERIES,
    QUALITY_4K,
    QUALITY_HD
}

@Composable
fun StreamTagBadge(
    type: StreamTagType,
    modifier: Modifier = Modifier,
    customText: String? = null
) {
    val (bgColor, textColor, defaultLabel) = when (type) {
        StreamTagType.LIVE -> Triple(TagLiveRed, Color.White, "LIVE")
        StreamTagType.LIVE_TV -> Triple(TagLiveTvBlue, Color.White, "LIVE TV")
        StreamTagType.MOVIES -> Triple(TagMoviesGreen, Color.White, "MOVIES")
        StreamTagType.SERIES -> Triple(TagSeriesOrange, Color.White, "SERIES")
        StreamTagType.QUALITY_4K -> Triple(Tag4kPurple, Color.White, "4K")
        StreamTagType.QUALITY_HD -> Triple(Color(0xFF6366F1), Color.White, "HD")
    }

    val label = customText ?: defaultLabel

    val shape = RoundedCornerShape(6.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (type == StreamTagType.LIVE) {
                // Pulsing dot for LIVE badge
                val transition = rememberInfiniteTransition(label = "pulse")
                val alpha by transition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulseAlpha"
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .alpha(alpha)
                        .clip(CircleShape)
                        .background(Color.White)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                color = textColor,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}
