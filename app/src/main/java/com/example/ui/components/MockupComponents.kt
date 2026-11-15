package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.Channel
import com.example.ui.theme.MobiButtonShape
import com.example.ui.theme.MobiCardShape
import com.example.ui.theme.mobiColors
import com.example.ui.viewmodel.SlideShowItem
import kotlinx.coroutines.delay

/**
 * Legacy Home Header wrapper mapped to shared tokens without bordered icon boxes.
 */
@Composable
fun MobiStreamHomeHeader(
    onSettingsClick: () -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("home_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = colors.onSurface,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = onFilterClick,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("home_filter_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = "Filters",
                    tint = colors.onSurface,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.testTag("header_brand")
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_mobistream_logo),
                contentDescription = "MobiStream Logo",
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "MOBISTREAM",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.6.sp
                ),
                color = colors.onBackground
            )
        }
    }
}

/**
 * Shared SectionHeader alias delegating to MobiSectionHeader.
 */
@Composable
fun SectionHeader(
    title: String,
    moreText: String? = "See all",
    onSeeAllClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    MobiSectionHeader(
        title = title,
        actionText = moreText,
        onActionClick = onSeeAllClick,
        modifier = modifier
    )
}

/**
 * Shared SearchBar alias delegating to MobiSearchBar.
 */
@Composable
fun StreamSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search 1000+ live channels, sports, news...",
    onClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    MobiSearchBar(
        query = query,
        onQueryChange = onQueryChange,
        hint = placeholder,
        onMenuClick = onMenuClick,
        onBarClick = onClick,
        modifier = modifier
    )
}

/**
 * Hero Banner Carousel:
 * - Hero gradient #1E6FD9 -> #0F2F5C with crisp white text
 * - LIVE badge (#E0263F) + channel logo badge in top-right
 * - "Play Now" action button
 * - Interactive HorizontalPager dots (thumb-scrollable + auto-rotate)
 */
@Composable
fun DynamicHeroSlideshow(
    slides: List<SlideShowItem>,
    onSlideClick: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    if (slides.isEmpty()) return

    val mobi = MaterialTheme.mobiColors
    val pagerState = rememberPagerState(pageCount = { slides.size })

    LaunchedEffect(pagerState.currentPage) {
        delay(5000)
        val nextPage = (pagerState.currentPage + 1) % slides.size
        pagerState.animateScrollToPage(nextPage)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(216.dp)
                .clip(MobiCardShape)
                .testTag("hero_slideshow_pager")
        ) { page ->
            val slide = slides[page]
            val channel = slide.channel

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(mobi.heroGradient)
                    .clickable { onSlideClick(channel) }
            ) {
                if (slide.showImageUrl.isNotBlank()) {
                    AsyncImage(
                        model = slide.showImageUrl,
                        contentDescription = slide.programTitle,
                        contentScale = ContentScale.Crop,
                        alpha = 0.35f,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Blue hero gradient overlay guaranteeing white text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    mobi.heroGradientStart.copy(alpha = 0.55f),
                                    mobi.heroGradientEnd.copy(alpha = 0.92f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top row: LIVE badge + category tag on left, channel logo on right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LiveBadge(text = "LIVE")
                            if (slide.tagText.isNotBlank() && !slide.tagText.equals("LIVE", ignoreCase = true)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White.copy(alpha = 0.18f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = slide.tagText,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        ChannelLogoBox(
                            channel = channel,
                            compact = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .width(84.dp)
                                .height(46.dp)
                        )
                    }

                    // Bottom content: Title, subtitle, Play Now button & page dots
                    Column {
                        Text(
                            text = slide.programTitle.ifBlank { channel.name },
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = slide.programSubtitle.ifBlank { "${channel.name} • ${channel.category}" },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier
                                    .height(44.dp)
                                    .clip(MobiButtonShape)
                                    .clickable { onSlideClick(channel) },
                                shape = MobiButtonShape,
                                color = Color.White
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play Now",
                                        tint = mobi.heroGradientEnd,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Play Now",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = mobi.heroGradientEnd
                                    )
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                slides.indices.forEach { index ->
                                    val isCurrent = index == pagerState.currentPage
                                    Box(
                                        modifier = Modifier
                                            .size(width = if (isCurrent) 20.dp else 6.dp, height = 6.dp)
                                            .clip(CircleShape)
                                            .background(if (isCurrent) Color.White else Color.White.copy(alpha = 0.42f))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Country Channel Card wrapper.
 */
@Composable
fun CountryChannelCard(
    channel: Channel,
    onPlayClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalChannelCard(
        channel = channel,
        onClick = onPlayClick,
        onToggleFavorite = onToggleFavorite,
        modifier = modifier
    )
}

/**
 * Pinned & Favorites card with full-width logo box and pin button.
 */
@Composable
fun PinnedAndFavCard(
    channel: Channel,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = modifier
            .width(164.dp)
            .height(148.dp)
            .clip(MobiCardShape)
            .clickable { onClick() }
            .testTag("pinned_fav_card_${channel.id}"),
        shape = MobiCardShape,
        color = colors.surface,
        border = BorderStroke(1.dp, if (channel.isPinned) colors.primary else colors.outline)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp)
            ) {
                ChannelLogoBox(
                    channel = channel,
                    compact = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxSize()
                )

                StreamTag(
                    text = channel.category.ifBlank { "Live" }.take(10),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                )

                IconButton(
                    onClick = onTogglePin,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(colors.surface.copy(alpha = 0.85f))
                ) {
                    Icon(
                        imageVector = if (channel.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        contentDescription = "Pin channel",
                        tint = if (channel.isPinned) colors.primary else colors.onSurface,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = channel.name,
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${getCountryFlag(channel.countryCode)} ${channel.country.ifBlank { channel.countryCode }}",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * TrendingStreamItem preserved for SearchScreen and Explore compatibility.
 */
@Composable
fun TrendingStreamItem(
    channel: Channel,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    ChannelTile(
        channel = channel,
        onClick = onClick,
        onToggleFavorite = onToggleFavorite,
        showLiveBadge = true,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    )
}

/**
 * Converts 2-letter ISO country code into real Unicode Country Flag emoji.
 */
fun getCountryFlag(countryCode: String): String {
    var clean = countryCode.trim().uppercase()
    if (clean == "UK") clean = "GB"
    if (clean.length != 2 || !clean[0].isLetter() || !clean[1].isLetter()) {
        return "\uD83C\uDF10" // Globe emoji fallback
    }
    val firstChar = Character.codePointAt(clean, 0) - 0x41 + 0x1F1E6
    val secondChar = Character.codePointAt(clean, 1) - 0x41 + 0x1F1E6
    return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
}
