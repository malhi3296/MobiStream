package com.example.ui.screens.search

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.components.TrendingStreamItem
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary

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
    val isDark = MaterialTheme.colorScheme.background != Color.White

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("search_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar Input
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Search",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = if (isDark) Color.White else TextDarkPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = filterOptions.searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            text = "Artists, songs, channels or live streams...",
                            fontFamily = InterFontFamily,
                            fontSize = 14.sp,
                            color = if (isDark) Color(0xFFA19BD0) else TextDarkSecondary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = if (isDark) Color.White else TextDarkPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (filterOptions.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = if (isDark) Color(0xFFA19BD0) else TextDarkSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = if (isDark) Color(0xFF1E174A) else Color(0xFFF3F4F8),
                        unfocusedContainerColor = if (isDark) Color(0xFF1E174A) else Color(0xFFF3F4F8),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = if (isDark) Color.White else TextDarkPrimary,
                        unfocusedTextColor = if (isDark) Color.White else TextDarkPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("search_screen_input")
                )
            }

            // Quick Category Filters
            val categoryNames = listOf("All") + categories.map { it.name }.distinct()
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                items(categoryNames) { cat ->
                    val isSelected = filterOptions.selectedCategory.equals(cat, ignoreCase = true)
                    val shape = RoundedCornerShape(14.dp)
                    Box(
                        modifier = Modifier
                            .clip(shape)
                            .background(
                                if (isSelected) {
                                    if (isDark) Color(0xFF7B3FE4) else Color(0xFF19191B)
                                } else {
                                    if (isDark) Color(0xFF1E174A) else Color(0xFFF0F1F6)
                                }
                            )
                            .clickable { onSelectCategory(cat) }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            fontFamily = InterFontFamily,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 12.sp,
                            color = if (isSelected) Color.White else if (isDark) Color.White else TextDarkSecondary
                        )
                    }
                }
            }

            // Search Results List
            if (channels.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = if (isDark) Color(0xFFA19BD0) else TextDarkSecondary,
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No results found",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (isDark) Color.White else TextDarkPrimary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(channels) { channel ->
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
