package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Category
import com.example.data.model.Channel
import com.example.domain.FilterOptions
import com.example.ui.components.ChannelCard
import com.example.ui.components.FilterBottomSheet
import com.example.ui.components.MobiStreamTopBar
import com.example.ui.components.StreamTagBadge
import com.example.ui.components.StreamTagType
import com.example.ui.theme.GlowLilac
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LiveRed
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightCard
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.StreamGradientEnd
import com.example.ui.theme.StreamGradientStart
import com.example.ui.theme.StreamViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun HomeScreen(
    channels: List<Channel>,
    featuredChannels: List<Channel>,
    popularChannels: List<Channel>,
    categories: List<Category>,
    filterOptions: FilterOptions,
    isLoading: Boolean,
    onSelectChannel: (Channel) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    onApplyFilters: (FilterOptions) -> Unit,
    onNavigateToExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterSheet = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground)
            .testTag("home_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Sticky Top Bar
            MobiStreamTopBar(
                searchQuery = filterOptions.searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onFilterClick = { showFilterSheet.value = true },
                hasActiveFilters = filterOptions.hasActiveFilters
            )

            if (isLoading && channels.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = StreamViolet)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    // 1. Featured Hero Banner (if not searching)
                    if (filterOptions.searchQuery.isBlank() && featuredChannels.isNotEmpty()) {
                        item {
                            FeaturedHeroBanner(
                                featuredChannel = featuredChannels.first(),
                                onWatchClick = { onSelectChannel(featuredChannels.first()) },
                                onBrowseClick = onNavigateToExplore
                            )
                        }
                    }

                    // 2. Category Quick Chips Row
                    item {
                        CategoryPillsRow(
                            categories = categories,
                            selectedCategory = filterOptions.selectedCategory,
                            onSelectCategory = onSelectCategory
                        )
                    }

                    // 3. Popular Channels Carousel (if not searching)
                    if (filterOptions.searchQuery.isBlank() && popularChannels.isNotEmpty()) {
                        item {
                            PopularChannelsSection(
                                channels = popularChannels,
                                onSelectChannel = onSelectChannel,
                                onToggleFavorite = onToggleFavorite
                            )
                        }
                    }

                    // 4. Main Channels Header & Grid Title
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (filterOptions.searchQuery.isNotBlank()) "Search Results" else "All Live Channels",
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = TextWhite
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MidnightCard)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${channels.size}",
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp,
                                        color = GlowLilac
                                    )
                                }
                            }
                        }
                    }

                    // 5. Channels Grid (Rendered in pairs for 2-column layout)
                    if (channels.isEmpty()) {
                        item {
                            EmptyStateView(
                                query = filterOptions.searchQuery,
                                onClear = { onSearchQueryChange("") }
                            )
                        }
                    } else {
                        items(channels.chunked(2)) { pair ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    ChannelCard(
                                        channel = pair[0],
                                        onClick = { onSelectChannel(pair[0]) },
                                        onToggleFavorite = { onToggleFavorite(pair[0].id) }
                                    )
                                }
                                if (pair.size > 1) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        ChannelCard(
                                            channel = pair[1],
                                            onClick = { onSelectChannel(pair[1]) },
                                            onToggleFavorite = { onToggleFavorite(pair[1].id) }
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Filter Bottom Sheet
        if (showFilterSheet.value) {
            FilterBottomSheet(
                filterOptions = filterOptions,
                categories = categories,
                countries = emptyList(),
                languages = emptyList(),
                onApplyFilters = onApplyFilters,
                onDismiss = { showFilterSheet.value = false }
            )
        }
    }
}

@Composable
private fun FeaturedHeroBanner(
    featuredChannel: Channel,
    onWatchClick: () -> Unit,
    onBrowseClick: () -> Unit
) {
    val bannerShape = RoundedCornerShape(20.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(bannerShape)
            .background(MidnightCard)
            .border(1.dp, MidnightCardBorder, bannerShape)
            .testTag("featured_hero_banner")
    ) {
        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF281E57),
                            Color(0xFF161138),
                            MidnightBackground
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Badges row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StreamTagBadge(type = StreamTagType.LIVE)
                StreamTagBadge(type = StreamTagType.QUALITY_4K, customText = featuredChannel.quality)
                StreamTagBadge(type = StreamTagType.LIVE_TV, customText = featuredChannel.category)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Channel Title & Tagline
            Text(
                text = featuredChannel.name,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = TextWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Live broadcast streaming in high definition from ${featuredChannel.country}.",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = TextMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: "Start Watching" and "Browse Channels"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Primary Stream Gradient Button: 14-20dp rounded corners
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.linearGradient(listOf(StreamGradientStart, StreamGradientEnd)))
                        .clickable { onWatchClick() }
                        .padding(horizontal = 18.dp, vertical = 11.dp)
                        .testTag("hero_start_watching_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Start Watching",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }

                // Secondary Outlined Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, StreamViolet, RoundedCornerShape(16.dp))
                        .clickable { onBrowseClick() }
                        .padding(horizontal = 16.dp, vertical = 11.dp)
                        .testTag("hero_browse_channels_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Browse Channels",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = GlowLilac
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryPillsRow(
    categories: List<Category>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit
) {
    val items = listOf("All") + categories.map { it.name }.distinct()

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { categoryName ->
            val isSelected = selectedCategory.equals(categoryName, ignoreCase = true)
            val pillShape = RoundedCornerShape(14.dp)

            Box(
                modifier = Modifier
                    .testTag("category_pill_$categoryName")
                    .clip(pillShape)
                    .background(if (isSelected) StreamViolet else MidnightCard)
                    .border(
                        1.dp,
                        if (isSelected) StreamGradientStart else MidnightCardBorder,
                        pillShape
                    )
                    .clickable { onSelectCategory(categoryName) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = categoryName,
                    fontFamily = InterFontFamily,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    fontSize = 13.sp,
                    color = if (isSelected) Color.White else TextMuted
                )
            }
        }
    }
}

@Composable
private fun PopularChannelsSection(
    channels: List<Channel>,
    onSelectChannel: (Channel) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Popular Channels",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextWhite
            )
            Text(
                text = "Trending Now",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = GlowLilac
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(channels) { channel ->
                Box(modifier = Modifier.width(170.dp)) {
                    ChannelCard(
                        channel = channel,
                        onClick = { onSelectChannel(channel) },
                        onToggleFavorite = { onToggleFavorite(channel.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateView(
    query: String,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No channels found",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = TextWhite
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (query.isNotBlank()) "No channels match \"$query\"" else "Try clearing your filters",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = onClear,
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Clear Search",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                color = GlowLilac
            )
        }
    }
}
