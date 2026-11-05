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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.ui.theme.GlowLilac
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightCard
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.StreamGradientStart
import com.example.ui.theme.StreamViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

enum class ExploreTab(val title: String) {
    GENRE("Genre / Category"),
    COUNTRY("Country"),
    LANGUAGE("Language")
}

@Composable
fun ExploreScreen(
    channels: List<Channel>,
    categories: List<Category>,
    countries: List<Country>,
    languages: List<Language>,
    filterOptions: FilterOptions,
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
    var selectedTab by remember { mutableStateOf(ExploreTab.GENRE) }
    var showFilterSheet by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("explore_screen")
    ) {

        Column(modifier = Modifier.fillMaxSize()) {
            // Screen Header & Search
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Explore & Discover",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = TextWhite
                        )
                        Text(
                            text = "Browse channels by country, language & genre",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    IconButton(
                        onClick = { showFilterSheet = true },
                        modifier = Modifier
                            .size(40.dp)
                            .background(if (filterOptions.hasActiveFilters) StreamViolet else MidnightCard, CircleShape)
                            .testTag("explore_filter_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Open Filters",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search field
                OutlinedTextField(
                    value = filterOptions.searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            text = "Search channels by name, country, genre...",
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = GlowLilac,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (filterOptions.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MidnightCard,
                        unfocusedContainerColor = MidnightCard,
                        focusedBorderColor = StreamGradientStart,
                        unfocusedBorderColor = MidnightCardBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = GlowLilac
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("explore_search_input")
                )
            }

            // Top Tabs: Genre, Country, Language
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = MidnightBackground,
                contentColor = GlowLilac,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                        color = StreamViolet,
                        height = 3.dp
                    )
                },
                divider = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MidnightCardBorder)
                    )
                }
            ) {
                ExploreTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = tab.title,
                                fontFamily = InterFontFamily,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 13.sp,
                                color = if (isSelected) TextWhite else TextMuted
                            )
                        },
                        modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                    )
                }
            }

            // Sub-category filters based on active tab
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                when (selectedTab) {
                    ExploreTab.GENRE -> {
                        val allCats = listOf("All") + categories.map { it.name }.distinct()
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(allCats) { cat ->
                                val isSelected = filterOptions.selectedCategory.equals(cat, ignoreCase = true)
                                FilterChip(
                                    label = cat,
                                    isSelected = isSelected,
                                    onClick = { onSetCategory(cat) }
                                )
                            }
                        }
                    }
                    ExploreTab.COUNTRY -> {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                FilterChip(
                                    label = "🌐 All",
                                    isSelected = filterOptions.selectedCountry == "All",
                                    onClick = { onSetCountry("All") }
                                )
                            }
                            items(countries) { country ->
                                val isSelected = filterOptions.selectedCountry.equals(country.code, ignoreCase = true) ||
                                        filterOptions.selectedCountry.equals(country.name, ignoreCase = true)
                                FilterChip(
                                    label = "${country.flag} ${country.name}",
                                    isSelected = isSelected,
                                    onClick = { onSetCountry(country.code) }
                                )
                            }
                        }
                    }
                    ExploreTab.LANGUAGE -> {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                FilterChip(
                                    label = "🌐 All",
                                    isSelected = filterOptions.selectedLanguage == "All",
                                    onClick = { onSetLanguage("All") }
                                )
                            }
                            items(languages) { lang ->
                                val isSelected = filterOptions.selectedLanguage.equals(lang.name, ignoreCase = true)
                                FilterChip(
                                    label = lang.name,
                                    isSelected = isSelected,
                                    onClick = { onSetLanguage(lang.name) }
                                )
                            }
                        }
                    }
                }
            }

            // Channels Count & Active Filter Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${channels.size} Channels Found",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = GlowLilac
                )

                if (filterOptions.hasActiveFilters) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF281E57))
                            .clickable { onResetFilters() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Reset Filters",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Reset",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // 2-Column Channels Grid
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp, top = 6.dp)
            ) {
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
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Try selecting another country, genre, or reset filters",
                                fontFamily = InterFontFamily,
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
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
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val chipShape = RoundedCornerShape(12.dp)
    Box(
        modifier = Modifier
            .clip(chipShape)
            .background(if (isSelected) StreamViolet else MidnightCard)
            .border(
                1.dp,
                if (isSelected) StreamGradientStart else MidnightCardBorder,
                chipShape
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = InterFontFamily,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else TextMuted
        )
    }
}
