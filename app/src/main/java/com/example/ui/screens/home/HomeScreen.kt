package com.example.ui.screens.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.data.model.Category
import com.example.data.model.Channel
import com.example.domain.FilterOptions
import com.example.ui.components.ChannelListItem
import com.example.ui.components.DynamicHeroSlideshow
import com.example.ui.components.HorizontalChannelCard
import com.example.ui.components.MobiButton
import com.example.ui.components.MobiCard
import com.example.ui.components.MobiChip
import com.example.ui.components.MobiSearchBar
import com.example.ui.components.MobiSectionHeader
import com.example.ui.components.PinnedAndFavCard
import com.example.ui.theme.MobiStreamTheme
import com.example.ui.viewmodel.SlideShowItem

@Composable
fun HomeScreen(
    slides: List<SlideShowItem>,
    pinnedAndFavorites: List<Channel>,
    countryChannels: List<Channel>,
    channelsYouMayLike: List<Channel> = emptyList(),
    globalPopularChannels: List<Channel>,
    trendingChannels: List<Channel>,
    categories: List<Category>,
    detectedCountryName: String,
    detectedCountryCode: String,
    filterOptions: FilterOptions,
    isDarkMode: Boolean,
    isLoading: Boolean,
    onToggleTheme: () -> Unit,
    onSelectChannel: (Channel) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onTogglePin: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    onApplyFilters: (FilterOptions) -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onOpenFilter: () -> Unit = onNavigateToSettings,
    onNavigateToFavorites: () -> Unit = onNavigateToExplore,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .testTag("home_screen")
    ) {
        if (isLoading && slides.isEmpty() && countryChannels.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colors.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 4.dp, bottom = 110.dp)
            ) {
                // 1. Search Bar pill + Menu button
                item {
                    MobiSearchBar(
                        query = "",
                        onQueryChange = {},
                        hint = "Search 1000+ live channels, sports, news...",
                        onBarClick = onNavigateToExplore,
                        onMenuClick = onOpenFilter
                    )
                }

                // 2. Hero Carousel
                if (slides.isNotEmpty()) {
                    item {
                        DynamicHeroSlideshow(
                            slides = slides,
                            onSlideClick = onSelectChannel
                        )
                    }
                }

                // 3. Pinned & Favourites (filled row or empty state card)
                item {
                    MobiSectionHeader(
                        title = "Pinned & Favourites",
                        actionText = if (pinnedAndFavorites.isNotEmpty()) "See all" else null,
                        onActionClick = onNavigateToFavorites
                    )
                    if (pinnedAndFavorites.isNotEmpty()) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            items(pinnedAndFavorites, key = { it.id }) { channel ->
                                PinnedAndFavCard(
                                    channel = channel,
                                    onClick = { onSelectChannel(channel) },
                                    onTogglePin = { onTogglePin(channel.id) },
                                    onToggleFavorite = { onToggleFavorite(channel.id) }
                                )
                            }
                        }
                    } else {
                        MobiCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(colors.secondaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.PushPin,
                                        contentDescription = null,
                                        tint = colors.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Pin your favorite live channels",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = colors.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tap the pin or heart icon on any channel to pin it here at the top.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // 4. Popular in <country>
                val countryDisplay = detectedCountryName.ifBlank { detectedCountryCode.ifBlank { "Your Country" } }
                val popularCountryList = countryChannels.take(10)
                if (popularCountryList.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        MobiSectionHeader(
                            title = "Popular in $countryDisplay",
                            actionText = "See all",
                            onActionClick = onNavigateToExplore
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(popularCountryList, key = { it.id }) { channel ->
                                HorizontalChannelCard(
                                    channel = channel,
                                    onClick = { onSelectChannel(channel) },
                                    onToggleFavorite = { onToggleFavorite(channel.id) }
                                )
                            }
                        }
                    }
                }

                // 5. Channels You May Like
                val mayLikeList = (if (channelsYouMayLike.isNotEmpty()) channelsYouMayLike else trendingChannels).take(6)
                if (mayLikeList.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        MobiSectionHeader(
                            title = "Channels You May Like",
                            actionText = "See all",
                            onActionClick = onNavigateToExplore
                        )
                    }
                    items(mayLikeList, key = { "like_${it.id}" }) { channel ->
                        ChannelListItem(
                            channel = channel,
                            onClick = { onSelectChannel(channel) },
                            onToggleFavorite = { onToggleFavorite(channel.id) }
                        )
                    }
                }

                // 6. Popular Global Channels
                val globalList = globalPopularChannels.take(6)
                if (globalList.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        MobiSectionHeader(
                            title = "Popular Global Channels",
                            actionText = "See all",
                            onActionClick = onNavigateToExplore
                        )
                    }
                    items(globalList, key = { "global_${it.id}" }) { channel ->
                        ChannelListItem(
                            channel = channel,
                            onClick = { onSelectChannel(channel) },
                            onToggleFavorite = { onToggleFavorite(channel.id) }
                        )
                    }
                }

                // 7. Trending Now
                val trendingList = trendingChannels.take(10)
                if (trendingList.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        MobiSectionHeader(
                            title = "Trending Now",
                            actionText = "See all",
                            onActionClick = onNavigateToExplore
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(trendingList, key = { "trend_${it.id}" }) { channel ->
                                HorizontalChannelCard(
                                    channel = channel,
                                    onClick = { onSelectChannel(channel) },
                                    onToggleFavorite = { onToggleFavorite(channel.id) }
                                )
                            }
                        }
                    }
                }

                // 8. Categories Chips
                val categoryChips = if (categories.isNotEmpty()) {
                    categories.map { it.name }.distinct().take(12)
                } else {
                    listOf("News", "Sports", "Movies", "Music", "Kids", "Documentary", "Comedy", "Entertainment")
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    MobiSectionHeader(
                        title = "Categories",
                        actionText = "See all",
                        onActionClick = onNavigateToExplore
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categoryChips) { catName ->
                            MobiChip(
                                label = catName,
                                selected = filterOptions.selectedCategory.equals(catName, ignoreCase = true),
                                onClick = {
                                    onSelectCategory(catName)
                                    onNavigateToExplore()
                                }
                            )
                        }
                    }
                }

                // 9. Discover 11,000+ Channels Card
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    MobiCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Text(
                                text = "Discover 11,000+ Channels",
                                style = MaterialTheme.typography.titleLarge,
                                color = colors.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Browse live television across 50+ categories, 150+ countries, and all global languages.",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            MobiButton(
                                text = "Explore All Channels",
                                leadingIcon = Icons.Outlined.Explore,
                                onClick = onNavigateToExplore,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("home_explore_all_button")
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenPreview() {
    val sampleChannel = Channel(
        id = "bbc",
        name = "BBC World News",
        logoUrl = "",
        streamUrl = "",
        category = "News",
        country = "United Kingdom",
        countryCode = "GB",
        language = "English",
        quality = "FHD",
        isLive = true,
        isFavorite = true
    )
    val sampleSlide = SlideShowItem(
        id = "slide1",
        channel = sampleChannel,
        programTitle = "Global News Hour",
        programSubtitle = "BBC World News • Breaking Coverage",
        tagText = "LIVE",
        tagColor = Color(0xFFE0263F),
        bgGradient = Brush.linearGradient(listOf(Color(0xFF1E6FD9), Color(0xFF0F2F5C)))
    )
    MobiStreamTheme {
        HomeScreen(
            slides = listOf(sampleSlide),
            pinnedAndFavorites = emptyList(),
            countryChannels = listOf(sampleChannel),
            channelsYouMayLike = listOf(sampleChannel.copy(id = "espn", name = "ESPN Live", category = "Sports")),
            globalPopularChannels = listOf(sampleChannel.copy(id = "cnn", name = "CNN International")),
            trendingChannels = listOf(sampleChannel.copy(id = "natgeo", name = "National Geographic", category = "Documentary")),
            categories = listOf(Category("News", "News", channelCount = 400), Category("Sports", "Sports", channelCount = 280)),
            detectedCountryName = "India",
            detectedCountryCode = "IN",
            filterOptions = FilterOptions(),
            isDarkMode = false,
            isLoading = false,
            onToggleTheme = {},
            onSelectChannel = {},
            onToggleFavorite = {},
            onTogglePin = {},
            onSelectCategory = {},
            onApplyFilters = {},
            onNavigateToExplore = {},
            onNavigateToSettings = {}
        )
    }
}
