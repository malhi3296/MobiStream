package com.example.ui.screens.search

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.data.model.Category
import com.example.data.model.Channel
import com.example.domain.FilterOptions
import com.example.ui.components.MobiChip
import com.example.ui.components.MobiSearchBar
import com.example.ui.components.TrendingStreamItem
import com.example.ui.theme.MobiStreamTheme

@Composable
fun SearchScreen(
    channels: List<Channel>,
    categories: List<Category>,
    filterOptions: FilterOptions,
    onSearchQueryChange: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    onSelectChannel: (Channel) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .testTag("search_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 4.dp)
            ) {
                Text(
                    text = "Search",
                    style = MaterialTheme.typography.headlineLarge,
                    color = colors.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                MobiSearchBar(
                    query = filterOptions.searchQuery,
                    onQueryChange = onSearchQueryChange,
                    hint = "Artists, songs, channels or live streams..."
                )
            }

            val categoryNames = listOf("All") + categories.map { it.name }.distinct()
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                items(categoryNames) { cat ->
                    val isSelected = filterOptions.selectedCategory.equals(cat, ignoreCase = true)
                    MobiChip(
                        label = cat,
                        selected = isSelected,
                        onClick = { onSelectCategory(cat) }
                    )
                }
            }

            if (channels.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.SearchOff,
                            contentDescription = null,
                            tint = colors.onSurfaceVariant,
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No results found",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.onBackground
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 110.dp)
                ) {
                    items(channels, key = { it.id }) { channel ->
                        TrendingStreamItem(
                            channel = channel,
                            onClick = { onSelectChannel(channel) },
                            onToggleFavorite = { onToggleFavorite(channel.id) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SearchScreenPreview() {
    val sample = Channel(
        id = "aaj",
        name = "Aaj Tak",
        logoUrl = "",
        streamUrl = "",
        category = "News",
        country = "India",
        countryCode = "IN",
        language = "Hindi",
        quality = "HD",
        isLive = true,
        isFavorite = true
    )
    MobiStreamTheme {
        SearchScreen(
            channels = listOf(sample, sample.copy(id = "ptc", name = "PTC Punjabi", category = "Entertainment", isFavorite = false)),
            categories = listOf(Category("News", "News", channelCount = 100), Category("Sports", "Sports", channelCount = 80)),
            filterOptions = FilterOptions(selectedCategory = "All"),
            onSearchQueryChange = {},
            onSelectCategory = {},
            onSelectChannel = {},
            onToggleFavorite = {}
        )
    }
}
