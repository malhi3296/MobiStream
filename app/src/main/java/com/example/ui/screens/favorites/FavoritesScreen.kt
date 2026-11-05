package com.example.ui.screens.favorites

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.example.data.model.Channel
import com.example.ui.components.ChannelCard
import com.example.ui.theme.GlowLilac
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LiveRed
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightCard
import com.example.ui.theme.StreamViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun FavoritesScreen(
    favoriteChannels: List<Channel>,
    onSelectChannel: (Channel) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onExploreChannels: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    val categories = remember(favoriteChannels) {
        listOf("All") + favoriteChannels.map { it.category }.distinct()
    }

    val displayedChannels = remember(favoriteChannels, selectedCategoryFilter) {
        if (selectedCategoryFilter == "All") {
            favoriteChannels
        } else {
            favoriteChannels.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground)
            .testTag("favorites_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(LiveRed.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = LiveRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "My Favorites",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextWhite
                    )
                    Text(
                        text = "${favoriteChannels.size} channels saved in offline database",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (favoriteChannels.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(MidnightCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = GlowLilac,
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "No favorite channels yet",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = TextWhite
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Tap the heart icon on any channel card or in the player to save it to your personal favorites playlist.",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            color = TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onExploreChannels,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StreamViolet
                            ),
                            modifier = Modifier.testTag("explore_channels_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tv,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Browse Channels",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            } else {
                // Filter by category if user has multiple categories in favorites
                if (categories.size > 2) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        items(categories) { categoryName ->
                            val isSelected = selectedCategoryFilter == categoryName
                            val shape = RoundedCornerShape(12.dp)
                            Surface(
                                shape = shape,
                                color = if (isSelected) StreamViolet else MidnightCard,
                                modifier = Modifier
                                    .clip(shape)
                                    .clickable { selectedCategoryFilter = categoryName }
                            ) {
                                Text(
                                    text = categoryName,
                                    fontFamily = InterFontFamily,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.White else TextMuted,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }

                // Grid of Favorite Channels
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(displayedChannels.chunked(2)) { pair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
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
}
