package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.Channel
import com.example.ui.theme.GlowLilac
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LiveRed
import com.example.ui.theme.StreamGradientStart
import com.example.ui.theme.StreamViolet
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.viewmodel.SlideShowItem
import kotlinx.coroutines.delay

/**
 * Top Home Header matching Requirement 3:
 * "setting should be icon on top left with filter icon on right of it, just use logo and mobistream name"
 */
@Composable
fun MobiStreamHomeHeader(
    onSettingsClick: () -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background != Color.White

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Side: Setting icon and Filter icon right of it
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF1E174A) else Color(0xFFF2F3F8))
                    .testTag("home_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = if (isDark) Color.White else TextDarkPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onFilterClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF1E174A) else Color(0xFFF2F3F8))
                    .testTag("home_filter_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Filters",
                    tint = if (isDark) Color.White else TextDarkPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Center / Brand: App Logo + MobiStream Name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.testTag("header_brand")
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_mobistream_logo),
                contentDescription = "MobiStream Logo",
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(9.dp))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "MOBISTREAM",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                letterSpacing = 1.8.sp,
                color = if (isDark) Color.White else TextDarkPrimary
            )
        }

        // Right side: LIVE pulse tag
        StreamTagBadge(type = StreamTagType.LIVE)
    }
}

/**
 * Dynamic 5-Slide Hero Carousel matching Requirement 5:
 * Slide 1: News channel with current news program bg
 * Slide 2: Most important live sports event with action bg
 * Slide 3,4,5: Three random sliders with current program bg
 */
@Composable
fun DynamicHeroSlideshow(
    slides: List<SlideShowItem>,
    onSlideClick: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    if (slides.isEmpty()) return

    var currentSlideIndex by remember { mutableIntStateOf(0) }

    // Auto rotate slides every 5 seconds
    LaunchedEffect(slides.size) {
        while (true) {
            delay(5000)
            if (slides.isNotEmpty()) {
                currentSlideIndex = (currentSlideIndex + 1) % slides.size
            }
        }
    }

    val currentSlide = slides[currentSlideIndex.coerceIn(0, slides.size - 1)]
    val cardShape = RoundedCornerShape(26.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(cardShape)
                .clickable { onSlideClick(currentSlide.channel) }
                .testTag("hero_slideshow_card")
        ) {
            AnimatedContent(
                targetState = currentSlide,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "slideAnimation",
                modifier = Modifier.fillMaxSize()
            ) { slide ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(slide.bgGradient)
                ) {
                    // Show image backdrop or channel logo
                    if (slide.showImageUrl.isNotBlank()) {
                        AsyncImage(
                            model = slide.showImageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    } else if (slide.channel.logoUrl.isNotBlank()) {
                        AsyncImage(
                            model = slide.channel.logoUrl,
                            contentDescription = slide.channel.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(36.dp)
                        )
                    }

                    // Scrim overlay for readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0x66000000), Color(0xDD0E0A24))
                                )
                            )
                    )

                    // Tag on Top Left
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(slide.tagColor)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = slide.tagText,
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Requirement 3: Currently running program with image of that show at title
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(18.dp)
                            .fillMaxWidth(0.78f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (slide.showImageUrl.isNotBlank()) {
                            AsyncImage(
                                model = slide.showImageUrl,
                                contentDescription = slide.programTitle,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = slide.programTitle,
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = slide.programSubtitle,
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.5.sp,
                                color = Color(0xFFE2E2E2),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Circular Play Button docked at Bottom Right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(52.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFF19191B))
                    .clickable { onSlideClick(currentSlide.channel) }
                    .testTag("slideshow_play_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Now",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 5 Dot Pagination Indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            slides.indices.forEach { index ->
                val isSelected = index == currentSlideIndex
                Box(
                    modifier = Modifier
                        .clickable { currentSlideIndex = index }
                        .padding(horizontal = 3.dp)
                        .size(width = if (isSelected) 18.dp else 6.dp, height = 5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            if (isSelected) StreamViolet else Color(0xFFD4D4DE)
                        )
                )
            }
        }
    }
}

/**
 * Pinned & Favourites Channel Card matching Requirement 9:
 * Distinctive card with pin tag and quick pin/favorite actions
 */
@Composable
fun PinnedAndFavCard(
    channel: Channel,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background != Color.White
    val cardShape = RoundedCornerShape(14.dp)

    Column(
        modifier = modifier
            .width(115.dp)
            .clickable { onClick() }
            .testTag("pinned_fav_card_${channel.id}"),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .width(115.dp)
                .height(85.dp)
                .clip(cardShape)
                .background(if (isDark) Color(0xFF1E174A) else Color(0xFFF3F4F9))
                .border(
                    width = 1.dp,
                    color = if (channel.isPinned) Color(0xFFFFB300) else if (isDark) Color(0xFF2C2266) else Color(0xFFE4E4EE),
                    shape = cardShape
                )
        ) {
            if (channel.logoUrl.isNotBlank()) {
                AsyncImage(
                    model = channel.logoUrl,
                    contentDescription = channel.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Tv,
                        contentDescription = null,
                        tint = if (isDark) Color.White else TextDarkPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // Top Badges: PINNED or FAV badge
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (channel.isPinned) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFFB300))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(8.dp)
                            )
                            Spacer(modifier = Modifier.width(1.dp))
                            Text(
                                text = "PIN",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp,
                                color = Color.Black
                            )
                        }
                    }
                } else if (channel.isFavorite) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(LiveRed)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "FAV",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            color = Color.White
                        )
                    }
                }
            }

            // Pin toggle on top-right
            IconButton(
                onClick = onTogglePin,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .size(22.dp)
            ) {
                Icon(
                    imageVector = if (channel.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                    contentDescription = "Pin channel",
                    tint = if (channel.isPinned) Color(0xFFFFB300) else if (isDark) Color.White else TextDarkSecondary,
                    modifier = Modifier.size(13.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = channel.name,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = if (isDark) Color.White else TextDarkPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = channel.category,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            color = if (isDark) Color(0xFFA19BD0) else TextDarkSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Localized Country Channel Card matching screenshot format (ICON_0f5a79ef.png)
 */
@Composable
fun CountryChannelCard(
    channel: Channel,
    onPlayClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background != Color.White
    val cardShape = RoundedCornerShape(16.dp)

    val cardGradient = Brush.verticalGradient(
        colors = listOf(
            if (isDark) Color(0xFF2C2746) else Color(0xFFECEEF8),
            if (isDark) Color(0xFF18142A) else Color(0xFFDFE2F2),
            if (isDark) Color(0xFF0C0918) else Color(0xFFD3D7EB)
        )
    )

    val borderColor = if (isDark) Color(0xFF383256) else Color(0xFFD4D8EC)

    Box(
        modifier = modifier
            .width(150.dp)
            .height(165.dp)
            .clip(cardShape)
            .border(1.dp, borderColor, cardShape)
            .background(cardGradient)
            .clickable { onPlayClick() }
            .testTag("country_channel_card_${channel.id}")
    ) {
        // Logo in Center Upper
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (channel.logoUrl.isNotBlank()) {
                AsyncImage(
                    model = channel.logoUrl,
                    contentDescription = channel.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = channel.name.take(2).uppercase(),
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = StreamViolet
                )
            }
        }

        // Top Right Favorite Button
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .size(30.dp)
                .background(Color(0x660E0A24), CircleShape)
        ) {
            Icon(
                imageVector = if (channel.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (channel.isFavorite) LiveRed else Color.White,
                modifier = Modifier.size(16.dp)
            )
        }

        // Bottom overlay with channel info, category pill, and flag + uppercase country
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xCC000000),
                            Color(0xF2070512)
                        )
                    )
                )
                .padding(horizontal = 9.dp, vertical = 7.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = channel.name,
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color(0xFFE4D6FF))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = channel.category,
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = Color(0xFF5322B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = getCountryFlag(channel.countryCode),
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = channel.country.uppercase(),
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 9.sp,
                        color = Color(0xFFD6D3E8),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Section Header with Title on Left and "See all" on Right
 */
@Composable
fun SectionHeader(
    title: String,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background != Color.White

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = if (isDark) Color.White else TextDarkPrimary
        )

        Text(
            text = "See all",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = if (isDark) Color(0xFFA19BD0) else TextDarkSecondary,
            modifier = Modifier
                .clickable { onSeeAllClick() }
                .padding(4.dp)
                .testTag("see_all_${title.lowercase().replace(" ", "_")}")
        )
    }
}

/**
 * Trending Stream List Item
 */
@Composable
fun TrendingStreamItem(
    channel: Channel,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background != Color.White

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 7.dp)
            .testTag("trending_item_${channel.id}"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF1E174A) else Color(0xFFF2F3F8)),
                contentAlignment = Alignment.Center
            ) {
                if (channel.logoUrl.isNotBlank()) {
                    AsyncImage(
                        model = channel.logoUrl,
                        contentDescription = channel.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Tv,
                        contentDescription = null,
                        tint = if (isDark) Color.White else TextDarkPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = channel.name,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isDark) Color.White else TextDarkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${getCountryFlag(channel.countryCode)} ${channel.country} • ${channel.category}",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = if (isDark) Color(0xFFA19BD0) else TextDarkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        IconButton(onClick = onToggleFavorite) {
            Icon(
                imageVector = if (channel.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (channel.isFavorite) LiveRed else if (isDark) Color(0xFFA19BD0) else TextDarkSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Genre Glowing Bubble
 */
@Composable
fun GenreGlowingBubble(
    label: String,
    gradient: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background != Color.White

    Column(
        modifier = modifier
            .width(82.dp)
            .clickable { onClick() }
            .testTag("genre_bubble_${label.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(gradient),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label.uppercase(),
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = if (isDark) Color.White else TextDarkPrimary,
            textAlign = TextAlign.Center
        )
    }
}
