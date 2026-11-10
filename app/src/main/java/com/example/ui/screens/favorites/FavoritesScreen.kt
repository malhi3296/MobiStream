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
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.data.model.Channel
import com.example.ui.components.ChannelCard
import com.example.ui.theme.GlowLilac
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LiveRed
import com.example.ui.theme.MidnightCard
import com.example.ui.theme.StreamViolet
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
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
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF3F3F46)

    val primaryTextColor = if (isDark) TextWhite else TextDarkPrimary
    val secondaryTextColor = if (isDark) TextMuted else TextDarkSecondary
    val cardBg = if (isDark) MidnightCard else Color(0xFFF3F4F8)

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
            .background(MaterialTheme.colorScheme.background)
            .testTag("favorites_screen")
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
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
                        color = primaryTextColor
                    )
                    Text(
                        text = "${favoriteChannels.size} channels saved in offline database",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = secondaryTextColor
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
                                .background(cardBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = if (isDark) GlowLilac else StreamViolet,
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "No favorite channels yet",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = primaryTextColor
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Tap the heart icon on any channel card or in the player to save it to your personal favorites playlist.",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            color = secondaryTextColor,
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
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                // Requirement: Make multiple folders of fav channels according to categories
                val categoryCounts = remember(favoriteChannels) {
                    favoriteChannels.groupingBy { it.category }.eachCount()
                }

                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                    Text(
                        text = "Category Folders",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = secondaryTextColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { categoryName ->
                            val isSelected = selectedCategoryFilter == categoryName
                            val shape = RoundedCornerShape(14.dp)
                            val count = if (categoryName == "All") favoriteChannels.size else (categoryCounts[categoryName] ?: 0)

                            Surface(
                                shape = shape,
                                color = if (isSelected) StreamViolet else cardBg,
                                modifier = Modifier
                                    .clip(shape)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) StreamViolet else (if (isDark) Color(0xFF2C2266) else Color(0xFFDCDFEA)),
                                        shape = shape
                                    )
                                    .clickable { selectedCategoryFilter = categoryName }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Default.FolderOpen else Icons.Default.Folder,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else StreamViolet,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = categoryName,
                                        fontFamily = InterFontFamily,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = if (isSelected) Color.White else primaryTextColor
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) Color.White.copy(alpha = 0.25f) else (if (isDark) Color(0xFF281E52) else Color(0xFFE2E4F0)))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "$count",
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = if (isSelected) Color.White else secondaryTextColor
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Active Folder Banner
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedCategoryFilter == "All") "📁 All Favorites Folder (${displayedChannels.size})" else "📁 $selectedCategoryFilter Folder (${displayedChannels.size} channels)",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.5.sp,
                            color = primaryTextColor
                        )
                        if (selectedCategoryFilter != "All") {
                            Text(
                                text = "Show All",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = StreamViolet,
                                modifier = Modifier
                                    .clickable { selectedCategoryFilter = "All" }
                                    .padding(4.dp)
                            )
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
