package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.mobiColors

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
    val colors = MaterialTheme.colorScheme
    val mobi = MaterialTheme.mobiColors

    val (bgColor, textColor, defaultLabel) = when (type) {
        StreamTagType.LIVE -> Triple(mobi.liveBadge, Color.White, "LIVE")
        StreamTagType.LIVE_TV -> Triple(colors.primary, colors.onPrimary, "LIVE TV")
        StreamTagType.MOVIES -> Triple(colors.secondaryContainer, colors.onSecondaryContainer, "MOVIES")
        StreamTagType.SERIES -> Triple(colors.secondaryContainer, colors.onSecondaryContainer, "SERIES")
        StreamTagType.QUALITY_4K -> Triple(colors.primary, colors.onPrimary, "4K")
        StreamTagType.QUALITY_HD -> Triple(colors.secondaryContainer, colors.onSecondaryContainer, "HD")
    }

    val label = customText ?: defaultLabel
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 0.5.sp
        )
    }
}
