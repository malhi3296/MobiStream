package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.Channel
import com.example.ui.theme.MobiCardShape
import com.example.ui.theme.mobiColors

/**
 * Tonal category / status pill tag.
 */
@Composable
fun StreamTag(
    text: String,
    isLive: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val mobi = MaterialTheme.mobiColors

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isLive) mobi.liveBadge else colors.secondaryContainer.copy(alpha = 0.94f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (isLive) Color.White else colors.onSecondaryContainer,
            maxLines = 1
        )
    }
}

/**
 * Channel List Item preserving existing call sites, backed by shared ChannelTile design.
 */
@Composable
fun ChannelListItem(
    channel: Channel,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    ChannelTile(
        channel = channel,
        onClick = onClick,
        onToggleFavorite = onToggleFavorite,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        showLiveBadge = channel.isLive
    )
}

/**
 * Horizontal Channel Card (used in Home horizontal carousels and Related Live Channels).
 * Features a full-width top logo frame that works across Dark and Light themes.
 */
@Composable
fun HorizontalChannelCard(
    channel: Channel,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val mobi = MaterialTheme.mobiColors

    Surface(
        modifier = modifier
            .width(198.dp)
            .height(166.dp)
            .clip(MobiCardShape)
            .clickable { onClick() }
            .testTag("horizontal_channel_card_${channel.id}"),
        shape = MobiCardShape,
        color = colors.surface,
        border = BorderStroke(1.dp, colors.outline)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Full-width Logo showcase container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp)
            ) {
                ChannelLogoBox(
                    channel = channel,
                    compact = false,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxSize()
                )

                // TopStart: Category pill
                StreamTag(
                    text = channel.category.ifBlank { "Live" },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                )

                // TopEnd: 48dp accessible Favorite button
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(48.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(colors.surface.copy(alpha = 0.86f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (channel.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (channel.isFavorite) "Remove from Favorites" else "Add to Favorites",
                            tint = if (channel.isFavorite) mobi.liveBadge else colors.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Bottom: Channel Name + Flag & Country
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = channel.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = getCountryFlag(channel.countryCode),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = channel.country.ifBlank { channel.countryCode },
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Standard compact grid/carousel ChannelCard preserving existing public signature.
 */
@Composable
fun ChannelCard(
    channel: Channel,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalChannelCard(
        channel = channel,
        onClick = onClick,
        onToggleFavorite = onToggleFavorite,
        modifier = modifier
    )
}

fun getTwoLetterInitials(name: String): String {
    val parts = name.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
    return when {
        parts.size >= 2 -> "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> "TV"
    }
}
