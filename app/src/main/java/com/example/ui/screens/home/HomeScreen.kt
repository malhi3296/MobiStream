package com.example.ui.screens.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.data.model.Channel
import com.example.domain.FilterOptions
import com.example.ui.components.CountryChannelCard
import com.example.ui.components.DynamicHeroSlideshow
import com.example.ui.components.GenreGlowingBubble
import com.example.ui.components.PinnedAndFavCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.TrendingStreamItem
import com.example.ui.components.getCountryFlag
import com.example.ui.theme.GenreKidsGradient
import com.example.ui.theme.GenreMoviesGradient
import com.example.ui.theme.GenreMusicGradient
import com.example.ui.theme.GenreNewsGradient
import com.example.ui.theme.GenreSportsGradient
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.StreamViolet
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.viewmodel.SlideShowItem

@Composable
fun HomeScreen(
    slides: List<SlideShowItem>,
    pinnedAndFavorites: List<Channel>,
    countryChannels: List<Channel>,
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
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background != Color.White

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen")
    ) {
        if (isLoading && slides.isEmpty() && countryChannels.isEmpty()) {
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
                // 1. Dynamic 5-slide Hero Carousel (rotates content every 30 mins)
                if (slides.isNotEmpty()) {
                    item {
                        DynamicHeroSlideshow(
                            slides = slides,
                            onSlideClick = onSelectChannel
                        )
                    }
                }

                // 2. Requirement 9 & 7: "Pinned & Favourites" section with smaller cards (pinned first)
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    SectionHeader(
                        title = "Pinned & Favourites",
                        onSeeAllClick = onNavigateToExplore
                    )

                    if (pinnedAndFavorites.isNotEmpty()) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            items(pinnedAndFavorites) { channel ->
                                PinnedAndFavCard(
                                    channel = channel,
                                    onClick = { onSelectChannel(channel) },
                                    onTogglePin = { onTogglePin(channel.id) },
                                    onToggleFavorite = { onToggleFavorite(channel.id) }
                                )
                            }
                        }
                    } else {
                        // Empty hint encouraging pinning
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = if (isDark) Color(0xFF1E174A) else Color(0xFFF4F5FB)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFFB300).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PushPin,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB300),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Pin your favorite live channels",
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isDark) Color.White else TextDarkPrimary
                                    )
                                    Text(
                                        text = "Tap the pin or heart icon on any channel to pin it here at the top.",
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 11.sp,
                                        color = if (isDark) Color(0xFFA19BD0) else TextDarkSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Requirement 6: "Popular in [Country Name]" section
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    val countryFlag = getCountryFlag(detectedCountryCode)
                    SectionHeader(
                        title = "Popular in $countryFlag $detectedCountryName",
                        onSeeAllClick = onNavigateToExplore
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        items(countryChannels) { channel ->
                            CountryChannelCard(
                                channel = channel,
                                onPlayClick = { onSelectChannel(channel) },
                                onToggleFavorite = { onToggleFavorite(channel.id) }
                            )
                        }
                    }
                }

                // 4. Requirement 15: "Popular Global Channels" section directly under "Popular in India/Country"
                if (globalPopularChannels.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        SectionHeader(
                            title = "Popular Global Channels",
                            onSeeAllClick = onNavigateToExplore
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            items(globalPopularChannels) { channel ->
                                CountryChannelCard(
                                    channel = channel,
                                    onPlayClick = { onSelectChannel(channel) },
                                    onToggleFavorite = { onToggleFavorite(channel.id) }
                                )
                            }
                        }
                    }
                }

                // 5. Requirement 11: Trending Live Streams (rotated every 2 hours)
                if (trendingChannels.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        SectionHeader(
                            title = "Trending Now",
                            onSeeAllClick = onNavigateToExplore
                        )
                    }

                    items(trendingChannels.take(5)) { channel ->
                        TrendingStreamItem(
                            channel = channel,
                            onClick = { onSelectChannel(channel) },
                            onToggleFavorite = { onToggleFavorite(channel.id) }
                        )
                    }
                }

                // 6. Requirement 6: Categories (Working: clicks navigate directly to Explore and open that category)
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    SectionHeader(
                        title = "Categories",
                        onSeeAllClick = onNavigateToExplore
                    )

                    val categoryBubbles = listOf(
                        "Sports" to GenreSportsGradient,
                        "News" to GenreNewsGradient,
                        "Movies" to GenreMoviesGradient,
                        "Music" to GenreMusicGradient,
                        "Kids" to GenreKidsGradient
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                    ) {
                        items(categoryBubbles) { (name, gradient) ->
                            GenreGlowingBubble(
                                label = name,
                                gradient = gradient,
                                onClick = {
                                    onSelectCategory(name)
                                    onNavigateToExplore()
                                }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
