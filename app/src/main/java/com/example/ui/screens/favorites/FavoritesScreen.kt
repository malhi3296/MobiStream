package com.example.ui.screens.favorites

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
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.data.model.Channel
import com.example.ui.components.ChannelListItem
import com.example.ui.components.MobiButton
import com.example.ui.components.MobiCard
import com.example.ui.components.MobiChip
import com.example.ui.theme.MobiStreamTheme

@Composable
fun FavoritesScreen(
    favoriteChannels: List<Channel>,
    onSelectChannel: (Channel) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onExploreChannels: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    val categories = remember(favoriteChannels) {
        listOf("All") + favoriteChannels.map { it.category.ifBlank { "Other" } }.distinct()
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
            .background(colors.background)
            .testTag("favorites_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 12.dp, bottom = 110.dp)
        ) {
            if (favoriteChannels.isEmpty()) {
                item {
                    MobiCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(colors.secondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.PushPin,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "No pinned channels yet",
                                style = MaterialTheme.typography.titleLarge,
                                color = colors.onSurface,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Tap the heart on any channel card to save it here for fast one-tap streaming.",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            MobiButton(
                                text = "Browse Channels",
                                leadingIcon = Icons.Outlined.Explore,
                                onClick = onExploreChannels,
                                modifier = Modifier.testTag("browse_channels_button")
                            )
                        }
                    }
                }
            } else {
                item {
                    val categoryCounts = remember(favoriteChannels) {
                        favoriteChannels.groupingBy { it.category.ifBlank { "Other" } }.eachCount()
                    }

                    Text(
                        text = "Category Folders",
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        items(categories) { categoryName ->
                            val isSelected = selectedCategoryFilter == categoryName
                            val count = if (categoryName == "All") favoriteChannels.size else (categoryCounts[categoryName] ?: 0)
                            MobiChip(
                                label = "$categoryName ($count)",
                                leadingIcon = if (isSelected) Icons.Outlined.FolderOpen else Icons.Outlined.Folder,
                                selected = isSelected,
                                onClick = { selectedCategoryFilter = categoryName }
                            )
                        }
                    }
                }

                items(displayedChannels, key = { it.id }) { channel ->
                    ChannelListItem(
                        channel = channel,
                        onClick = { onSelectChannel(channel) },
                        onToggleFavorite = { onToggleFavorite(channel.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FavoritesScreenFilledPreview() {
    val sample = Channel(
        id = "ptc",
        name = "PTC News",
        logoUrl = "",
        streamUrl = "",
        category = "News",
        country = "India",
        countryCode = "IN",
        language = "Punjabi",
        quality = "HD",
        isLive = true,
        isFavorite = true
    )
    MobiStreamTheme {
        FavoritesScreen(
            favoriteChannels = listOf(sample, sample.copy(id = "espn", name = "ESPN HD", category = "Sports")),
            onSelectChannel = {},
            onToggleFavorite = {},
            onExploreChannels = {}
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FavoritesScreenEmptyPreview() {
    MobiStreamTheme {
        FavoritesScreen(
            favoriteChannels = emptyList(),
            onSelectChannel = {},
            onToggleFavorite = {},
            onExploreChannels = {}
        )
    }
}
