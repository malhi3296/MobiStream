package com.example.ui.screens.explore

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.Country
import com.example.data.model.Language
import com.example.domain.FilterOptions
import com.example.ui.components.ChannelCard
import com.example.ui.components.FilterBottomSheet
import com.example.ui.components.MobiStreamUnifiedTopBar
import com.example.ui.components.SectionHeader
import com.example.ui.theme.GlowLilac
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.StreamGradientStart
import com.example.ui.theme.StreamViolet
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary

@Composable
fun ExploreScreen(
    channels: List<Channel>,
    categories: List<Category>,
    countries: List<Country>,
    languages: List<Language>,
    filterOptions: FilterOptions,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onSelectChannel: (Channel) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSetCategory: (String) -> Unit,
    onSetCountry: (String) -> Unit,
    onSetLanguage: (String) -> Unit,
    onResetFilters: () -> Unit,
    onApplyFilters: (FilterOptions) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    val isDark = MaterialTheme.colorScheme.background != Color.White

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("explore_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Unified Top Bar identical to Home page
            MobiStreamUnifiedTopBar(
                isDarkMode = isDarkMode,
                onToggleTheme = onToggleTheme,
                onSettingsClick = onNavigateToSettings,
                onFilterClick = { showFilterSheet = true },
                hasActiveFilters = filterOptions.hasActiveFilters
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // 2. Redesigned Search Bar
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                    ) {
                        OutlinedTextField(
                            value = filterOptions.searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = {
                                Text(
                                    text = "Search channels, live streams, shows...",
                                    fontFamily = InterFontFamily,
                                    fontSize = 14.sp,
                                    color = if (isDark) Color(0xFFA19BD0) else TextDarkSecondary
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = if (isDark) GlowLilac else StreamViolet,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                if (filterOptions.searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { onSearchQueryChange("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear search",
                                            tint = if (isDark) Color(0xFFA19BD0) else TextDarkSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(18.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = if (isDark) Color(0xFF1E174A) else Color(0xFFF3F4F8),
                                unfocusedContainerColor = if (isDark) Color(0xFF1E174A) else Color(0xFFF3F4F8),
                                focusedBorderColor = StreamViolet,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = if (isDark) Color.White else TextDarkPrimary,
                                unfocusedTextColor = if (isDark) Color.White else TextDarkPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("explore_search_bar")
                        )
                    }
                }

                // 3. Section 1: Categories (Horizontal scrollable from left to right)
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Categories",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isDark) Color.White else TextDarkPrimary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )

                    val categoryList = listOf("All") + categories.map { it.name }.distinct()
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        items(categoryList) { categoryName ->
                            val isSelected = filterOptions.selectedCategory.equals(categoryName, ignoreCase = true)
                            ExploreHorizontalCard(
                                title = categoryName,
                                isSelected = isSelected,
                                onClick = { onSetCategory(categoryName) }
                            )
                        }
                    }
                }

                // 4. Section 2: Countries (Horizontal scrollable from left to right)
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Countries",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isDark) Color.White else TextDarkPrimary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        item {
                            ExploreHorizontalCard(
                                title = "🌐 All",
                                isSelected = filterOptions.selectedCountry == "All",
                                onClick = { onSetCountry("All") }
                            )
                        }
                        items(countries) { country ->
                            val isSelected = filterOptions.selectedCountry.equals(country.code, ignoreCase = true) ||
                                    filterOptions.selectedCountry.equals(country.name, ignoreCase = true)
                            ExploreHorizontalCard(
                                title = "${country.flag} ${country.name}",
                                isSelected = isSelected,
                                onClick = { onSetCountry(country.code) }
                            )
                        }
                    }
                }

                // 5. Section 3: Languages (Horizontal scrollable from left to right)
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Languages",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isDark) Color.White else TextDarkPrimary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        item {
                            ExploreHorizontalCard(
                                title = "🌐 All",
                                isSelected = filterOptions.selectedLanguage == "All",
                                onClick = { onSetLanguage("All") }
                            )
                        }
                        items(languages) { language ->
                            val isSelected = filterOptions.selectedLanguage.equals(language.name, ignoreCase = true)
                            ExploreHorizontalCard(
                                title = language.name,
                                isSelected = isSelected,
                                onClick = { onSetLanguage(language.name) }
                            )
                        }
                    }
                }

                // 6. Header for Channels Grid
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${channels.size} Channels Found",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (isDark) Color.White else TextDarkPrimary
                        )

                        if (filterOptions.hasActiveFilters) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(StreamViolet)
                                    .clickable { onResetFilters() }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "Reset All",
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 7. Channels Grid (2-column)
                if (channels.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No channels match this filter",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (isDark) Color.White else TextDarkPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Try selecting different categories, countries or reset filters",
                                fontFamily = InterFontFamily,
                                fontSize = 12.sp,
                                color = if (isDark) Color(0xFFA19BD0) else TextDarkSecondary
                            )
                        }
                    }
                } else {
                    items(channels.chunked(2)) { pair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp),
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

        // Filter Bottom Sheet when filter button is clicked
        if (showFilterSheet) {
            FilterBottomSheet(
                filterOptions = filterOptions,
                categories = categories,
                countries = countries,
                languages = languages,
                onApplyFilters = onApplyFilters,
                onDismiss = { showFilterSheet = false }
            )
        }
    }
}

@Composable
private fun ExploreHorizontalCard(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background != Color.White
    val cardShape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .clip(cardShape)
            .background(
                if (isSelected) {
                    StreamViolet
                } else {
                    if (isDark) Color(0xFF1E174A) else Color(0xFFF3F4F8)
                }
            )
            .border(
                width = 1.dp,
                color = if (isSelected) StreamGradientStart else if (isDark) Color(0xFF2C2266) else Color(0xFFE4E4EE),
                shape = cardShape
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontFamily = InterFontFamily,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            fontSize = 13.sp,
            color = if (isSelected) Color.White else if (isDark) Color.White else TextDarkPrimary
        )
    }
}
