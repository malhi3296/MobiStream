package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Tv
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Channel
import com.example.ui.theme.GlowLilac
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LiveRed
import com.example.ui.theme.StreamViolet
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary

/**
 * Modern Stream Channel Card:
 * - Transparent logo tile background with NO overlay gradient
 * - Really subtle dropshadow as border
 * - Dark text in light mode, light text in dark mode
 * - Clean adaptive surface matching #D4D4D8 (light) and #3F3F46 (dark)
 */
@Composable
fun ChannelCard(
    channel: Channel,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF3F3F46)
    val cardShape = RoundedCornerShape(16.dp)

    // Clean solid background for card, matching user specifications
    val cardBg = if (isDark) Color(0xFF27272A) else Color(0xFFE4E4E7)
    val subtleShadowBorder = if (isDark) Color(0x33FFFFFF) else Color(0x1F000000)

    Surface(
        modifier = modifier
            .testTag("channel_card_${channel.id}")
            .shadow(
                elevation = 2.dp,
                shape = cardShape,
                spotColor = if (isDark) Color(0x66000000) else Color(0x26000000),
                ambientColor = if (isDark) Color(0x40000000) else Color(0x14000000)
            )
            .border(width = 1.dp, color = subtleShadowBorder, shape = cardShape)
            .clip(cardShape)
            .clickable { onClick() },
        shape = cardShape,
        color = cardBg
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) {
            // Main Logo Area: Transparent background with NO white box and NO gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                val resolvedLogo = com.example.domain.ChannelLogoResolver.resolve(channel.name, channel.logoUrl)
                if (resolvedLogo.isNotBlank()) {
                    coil.compose.SubcomposeAsyncImage(
                        model = resolvedLogo,
                        contentDescription = "${channel.name} logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        error = {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(StreamViolet.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = channel.name.take(2).uppercase(),
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = StreamViolet
                                )
                            }
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(StreamViolet.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = channel.name.take(2).uppercase(),
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = StreamViolet
                        )
                    }
                }
            }

            // Top-Right Favorite Button with subtle translucent background
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(32.dp)
                    .background(
                        if (isDark) Color(0x4D000000) else Color(0x14000000),
                        CircleShape
                    )
                    .testTag("fav_button_${channel.id}")
            ) {
                Icon(
                    imageVector = if (channel.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (channel.isFavorite) "Remove favorite" else "Add favorite",
                    tint = if (channel.isFavorite) LiveRed else if (isDark) Color.White.copy(alpha = 0.85f) else TextDarkSecondary,
                    modifier = Modifier.size(17.dp)
                )
            }

            // Bottom Info Overlay: Clean transparent container with NO overlay gradient
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Line 1: Channel Name (dark in light mode, light in dark mode) + Category Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = channel.name,
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = if (isDark) Color.White else TextDarkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Category Pill Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(StreamViolet.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = channel.category,
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp,
                            color = if (isDark) GlowLilac else Color(0xFF6D28D9)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                // Line 2: Flag + uppercase Country Name (adaptive dark/light)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = getCountryFlag(channel.countryCode),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = channel.country.uppercase(),
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        color = if (isDark) Color(0xFFA1A1AA) else TextDarkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

fun getCountryFlag(countryCode: String): String {
    if (countryCode.length != 2) return "🌐"
    val firstChar = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
    val secondChar = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6
    return if (firstChar in 0x1F1E6..0x1F1FF && secondChar in 0x1F1E6..0x1F1FF) {
        String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
    } else {
        "🌐"
    }
}
