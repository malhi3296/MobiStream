package com.example.ui.screens.explore

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.data.model.Channel
import com.example.data.model.Country
import com.example.data.model.Language
import com.example.domain.FilterOptions
import com.example.domain.GroupType
import com.example.domain.SelectedGroup
import com.example.ui.components.ChannelCard
import com.example.ui.components.CountryChannelCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.getCountryFlag
import com.example.ui.theme.GlowLilac
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.StreamViolet
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary

enum class ExploreSubView {
    NONE,
    ALL_COUNTRIES,
    ALL_LANGUAGES
}

@Composable
fun ExploreScreen(
    allChannels: List<Channel>,
    countryChannels: List<Channel>,
    categories: List<Category>,
    countries: List<Country>,
    languages: List<Language>,
    detectedCountryName: String,
    detectedCountryCode: String,
    filterOptions: FilterOptions,
    isDarkMode: Boolean,
    selectedGroup: SelectedGroup?,
    onSelectGroup: (SelectedGroup?) -> Unit,
    sortCategoryChannels: (String, List<Channel>) -> List<Channel>,
    onSelectChannel: (Channel) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background != Color.White
    var subView by remember { mutableStateOf(ExploreSubView.NONE) }
    var subViewSearch by remember { mutableStateOf("") }

    // High-contrast text colors
    val primaryTextColor = if (isDark) Color.White else TextDarkPrimary
    val secondaryTextColor = if (isDark) Color(0xFFA19BD0) else TextDarkSecondary

    // 1. If a category, country, or language is selected, show its live channel list
    if (selectedGroup != null) {
        val group = selectedGroup
        BackHandler { onSelectGroup(null) }

        val matchingChannels = remember(group, allChannels, filterOptions) {
            val raw = allChannels.filter { channel ->
                val matchesGroup = when (group.type) {
                    GroupType.CATEGORY -> channel.category.equals(group.identifier, ignoreCase = true)
                    GroupType.COUNTRY -> channel.countryCode.equals(group.identifier, ignoreCase = true) ||
                            channel.country.equals(group.title, ignoreCase = true)
                    GroupType.LANGUAGE -> channel.language.equals(group.identifier, ignoreCase = true)
                }
                val matchesFilter = if (filterOptions.selectedQuality != "All") {
                    channel.quality.contains(filterOptions.selectedQuality, ignoreCase = true)
                } else true

                matchesGroup && matchesFilter
            }

            // Requirement 14: Prioritize popular global channels at the top (ESPN, CNN, etc.)
            if (group.type == GroupType.CATEGORY) {
                sortCategoryChannels(group.identifier, raw)
            } else {
                raw
            }
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .testTag("group_channel_detail_screen")
        ) {
            // Group Header with Back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onSelectGroup(null) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Explore",
                        tint = primaryTextColor
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = group.title,
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = primaryTextColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${matchingChannels.size} live channels available",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = secondaryTextColor
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDark) Color(0xFF281E57) else Color(0xFFEFE9FE))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (group.type) {
                            GroupType.CATEGORY -> "Category"
                            GroupType.COUNTRY -> "Country"
                            GroupType.LANGUAGE -> "Language"
                        },
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = if (isDark) GlowLilac else StreamViolet
                    )
                }
            }

            // Grid of matching channels
            if (matchingChannels.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No channels found for ${group.title}",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = primaryTextColor
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try another category or sync playlists from settings.",
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            color = secondaryTextColor
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(matchingChannels) { channel ->
                        ChannelCard(
                            channel = channel,
                            onClick = { onSelectChannel(channel) },
                            onToggleFavorite = { onToggleFavorite(channel.id) }
                        )
                    }
                }
            }
        }
    } else if (subView == ExploreSubView.ALL_COUNTRIES) {
        // Requirement 13: "See all" for Countries in alphabetical order
        BackHandler { subView = ExploreSubView.NONE }

        val filteredCountries = remember(countries, subViewSearch) {
            if (subViewSearch.isBlank()) countries
            else countries.filter {
                it.name.contains(subViewSearch, ignoreCase = true) ||
                        it.code.contains(subViewSearch, ignoreCase = true)
            }
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { subView = ExploreSubView.NONE },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = primaryTextColor
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "All Countries (A-Z)",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = primaryTextColor
                    )
                    Text(
                        text = "${countries.size} countries available",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = secondaryTextColor
                    )
                }
            }

            // Search filter for countries
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                OutlinedTextField(
                    value = subViewSearch,
                    onValueChange = { subViewSearch = it },
                    placeholder = { Text("Search countries...", color = secondaryTextColor) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = StreamViolet) },
                    trailingIcon = {
                        if (subViewSearch.isNotEmpty()) {
                            IconButton(onClick = { subViewSearch = "" }) {
                                Icon(Icons.Default.Close, null, tint = secondaryTextColor)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = if (isDark) Color(0xFF1E174A) else Color(0xFFF3F4F8),
                        unfocusedContainerColor = if (isDark) Color(0xFF1E174A) else Color(0xFFF3F4F8),
                        focusedBorderColor = StreamViolet,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = primaryTextColor,
                        unfocusedTextColor = primaryTextColor
                    ),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredCountries) { country ->
                    BigCountryCard(
                        country = country,
                        isDark = isDark,
                        onClick = {
                            subView = ExploreSubView.NONE
                            onSelectGroup(
                                SelectedGroup(
                                    type = GroupType.COUNTRY,
                                    title = "${country.flag} ${country.name}",
                                    identifier = country.code
                                )
                            )
                        }
                    )
                }
            }
        }
    } else if (subView == ExploreSubView.ALL_LANGUAGES) {
        // Requirement 12: "See all" for Languages (Indian languages on top, punjabi first)
        BackHandler { subView = ExploreSubView.NONE }

        val filteredLanguages = remember(languages, subViewSearch) {
            if (subViewSearch.isBlank()) languages
            else languages.filter {
                it.name.contains(subViewSearch, ignoreCase = true) ||
                        it.code.contains(subViewSearch, ignoreCase = true)
            }
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { subView = ExploreSubView.NONE },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = primaryTextColor
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "All Languages",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = primaryTextColor
                    )
                    Text(
                        text = "${languages.size} languages available (Punjabi & Indian priority)",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = secondaryTextColor
                    )
                }
            }

            // Search filter for languages
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                OutlinedTextField(
                    value = subViewSearch,
                    onValueChange = { subViewSearch = it },
                    placeholder = { Text("Search languages...", color = secondaryTextColor) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = StreamViolet) },
                    trailingIcon = {
                        if (subViewSearch.isNotEmpty()) {
                            IconButton(onClick = { subViewSearch = "" }) {
                                Icon(Icons.Default.Close, null, tint = secondaryTextColor)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = if (isDark) Color(0xFF1E174A) else Color(0xFFF3F4F8),
                        unfocusedContainerColor = if (isDark) Color(0xFF1E174A) else Color(0xFFF3F4F8),
                        focusedBorderColor = StreamViolet,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = primaryTextColor,
                        unfocusedTextColor = primaryTextColor
                    ),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredLanguages) { language ->
                    BigLanguageCard(
                        language = language,
                        isDark = isDark,
                        onClick = {
                            subView = ExploreSubView.NONE
                            onSelectGroup(
                                SelectedGroup(
                                    type = GroupType.LANGUAGE,
                                    title = "${language.name} Channels",
                                    identifier = language.name
                                )
                            )
                        }
                    )
                }
            }
        }
    } else {
        // Explore Root Page
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .testTag("explore_screen")
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                // 1. Top Channel List Slider: Left to right with user country
                item {
                    val countryFlag = getCountryFlag(detectedCountryCode)
                    SectionHeader(
                        title = "Popular in $countryFlag $detectedCountryName",
                        onSeeAllClick = {
                            onSelectGroup(
                                SelectedGroup(
                                    type = GroupType.COUNTRY,
                                    title = "$countryFlag $detectedCountryName",
                                    identifier = detectedCountryCode
                                )
                            )
                        }
                    )

                    if (countryChannels.isNotEmpty()) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
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
                }

                // 2. Search Bar
                item {
                    Spacer(modifier = Modifier.height(10.dp))
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
                                    text = "Search 11,000+ channels by name...",
                                    fontFamily = InterFontFamily,
                                    fontSize = 14.sp,
                                    color = secondaryTextColor
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
                                            tint = secondaryTextColor,
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
                                focusedTextColor = primaryTextColor,
                                unfocusedTextColor = primaryTextColor
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("explore_search_bar")
                        )
                    }
                }

                // If user typed in search query, show direct search results
                if (filterOptions.searchQuery.isNotBlank()) {
                    val searchResults = allChannels.filter {
                        it.name.contains(filterOptions.searchQuery, ignoreCase = true) ||
                                it.category.contains(filterOptions.searchQuery, ignoreCase = true) ||
                                it.country.contains(filterOptions.searchQuery, ignoreCase = true)
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${searchResults.size} channels found",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = primaryTextColor,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                        )
                    }

                    items(searchResults.chunked(2)) { pair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            for (channel in pair) {
                                Box(modifier = Modifier.weight(1f)) {
                                    ChannelCard(
                                        channel = channel,
                                        onClick = { onSelectChannel(channel) },
                                        onToggleFavorite = { onToggleFavorite(channel.id) }
                                    )
                                }
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    // Big Divs for Categories, Countries, and Languages

                    // SECTION 1: Categories (Big Divs)
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Categories",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = primaryTextColor,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            items(categories) { category ->
                                BigCategoryCard(
                                    category = category,
                                    isDark = isDark,
                                    onClick = {
                                        onSelectGroup(
                                            SelectedGroup(
                                                type = GroupType.CATEGORY,
                                                title = category.name,
                                                identifier = category.name
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }

                    // SECTION 2: Countries (Big Divs with "See All" at the end - Requirement 13)
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Countries",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = primaryTextColor
                            )
                            Text(
                                text = "See all (${countries.size})",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = StreamViolet,
                                modifier = Modifier.clickable { subView = ExploreSubView.ALL_COUNTRIES }
                            )
                        }

                        val visibleCountries = countries.take(12)
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            items(visibleCountries) { country ->
                                BigCountryCard(
                                    country = country,
                                    isDark = isDark,
                                    onClick = {
                                        onSelectGroup(
                                            SelectedGroup(
                                                type = GroupType.COUNTRY,
                                                title = "${country.flag} ${country.name}",
                                                identifier = country.code
                                            )
                                        )
                                    }
                                )
                            }

                            // "See all" card at the end
                            item {
                                SeeAllCard(
                                    title = "All Countries",
                                    subtitle = "${countries.size} countries (A-Z)",
                                    icon = Icons.Default.Public,
                                    isDark = isDark,
                                    onClick = { subView = ExploreSubView.ALL_COUNTRIES }
                                )
                            }
                        }
                    }

                    // SECTION 3: Languages (Big Divs with Indian & Punjabi on top and "See All" at the end - Requirement 12)
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Languages",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = primaryTextColor
                            )
                            Text(
                                text = "See all (${languages.size})",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = StreamViolet,
                                modifier = Modifier.clickable { subView = ExploreSubView.ALL_LANGUAGES }
                            )
                        }

                        val visibleLanguages = languages.take(12)
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            items(visibleLanguages) { language ->
                                BigLanguageCard(
                                    language = language,
                                    isDark = isDark,
                                    onClick = {
                                        onSelectGroup(
                                            SelectedGroup(
                                                type = GroupType.LANGUAGE,
                                                title = "${language.name} Channels",
                                                identifier = language.name
                                            )
                                        )
                                    }
                                )
                            }

                            // "See all" card at the end
                            item {
                                SeeAllCard(
                                    title = "All Languages",
                                    subtitle = "${languages.size} languages",
                                    icon = Icons.Default.Language,
                                    isDark = isDark,
                                    onClick = { subView = ExploreSubView.ALL_LANGUAGES }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * See All Card for Countries and Languages rows
 */
@Composable
fun SeeAllCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isDark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .width(155.dp)
            .clickable { onClick() }
            .testTag("see_all_card_${title.lowercase().replace(" ", "_")}"),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .size(155.dp)
                .clip(cardShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            if (isDark) Color(0xFF381F6F) else Color(0xFFE9E5FC),
                            if (isDark) Color(0xFF1E1045) else Color(0xFFF5F3FF)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = StreamViolet.copy(alpha = 0.5f),
                    shape = cardShape
                )
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(StreamViolet),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "View All",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isDark) Color.White else TextDarkPrimary
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(36.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(StreamViolet),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = title,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = if (isDark) Color.White else TextDarkPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = subtitle,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = if (isDark) Color(0xFFA19BD0) else TextDarkSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Big Category Card (styled with 155dp size like popular channels on homepage)
 */
@Composable
fun BigCategoryCard(
    category: Category,
    isDark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(20.dp)
    val cardGradient = getCategoryGradient(category.name, isDark)

    Column(
        modifier = modifier
            .width(155.dp)
            .clickable { onClick() }
            .testTag("big_category_card_${category.name.lowercase()}"),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .size(155.dp)
                .clip(cardShape)
                .background(cardGradient)
                .border(
                    width = 1.dp,
                    color = if (isDark) Color(0xFF2C2266) else Color(0xFFE2E4EE),
                    shape = cardShape
                )
        ) {
            // Category Icon in center
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0x33FFFFFF) else Color(0x224F2BC4)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(category.name),
                    contentDescription = null,
                    tint = if (isDark) Color.White else StreamViolet,
                    modifier = Modifier.size(30.dp)
                )
            }

            // Top Badge with "Category"
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isDark) Color(0x800E0A24) else Color(0xCCFFFFFF))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "CATEGORY",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    color = if (isDark) GlowLilac else StreamViolet
                )
            }

            // Signature Circular arrow button docked at bottom-right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(36.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFF19191B))
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = category.name,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = if (isDark) Color.White else TextDarkPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = if (category.channelCount > 0) "${category.channelCount} channels" else "Live streams",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = if (isDark) Color(0xFFA19BD0) else TextDarkSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Big Country Card (155dp size with country flag and channel count)
 */
@Composable
fun BigCountryCard(
    country: Country,
    isDark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .width(155.dp)
            .clickable { onClick() }
            .testTag("big_country_card_${country.code.lowercase()}"),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .size(155.dp)
                .clip(cardShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            if (isDark) Color(0xFF241A5E) else Color(0xFFEFF0F9),
                            if (isDark) Color(0xFF17113E) else Color(0xFFF7F8FD)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = if (isDark) Color(0xFF2C2266) else Color(0xFFE2E4EE),
                    shape = cardShape
                )
        ) {
            // Big Flag in center
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0x22FFFFFF) else Color(0x33B7A6FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = country.flag,
                    fontSize = 32.sp
                )
            }

            // Top Badge with ISO code
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isDark) Color(0x800E0A24) else Color(0xCCFFFFFF))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Text(
                    text = country.code,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = if (isDark) GlowLilac else StreamViolet
                )
            }

            // Bottom Right Action
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(36.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFF19191B))
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = country.name,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = if (isDark) Color.White else TextDarkPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = if (country.channelCount > 0) "${country.channelCount} channels" else "Explore country",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = if (isDark) Color(0xFFA19BD0) else TextDarkSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Big Language Card (155dp size with language icon and channel count)
 */
@Composable
fun BigLanguageCard(
    language: Language,
    isDark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .width(155.dp)
            .clickable { onClick() }
            .testTag("big_language_card_${language.name.lowercase()}"),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .size(155.dp)
                .clip(cardShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            if (isDark) Color(0xFF2A1C5A) else Color(0xFFF3F1FD),
                            if (isDark) Color(0xFF1E1446) else Color(0xFFFAF9FF)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = if (isDark) Color(0xFF2C2266) else Color(0xFFE2E4EE),
                    shape = cardShape
                )
        ) {
            // Language Icon
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0x33FFFFFF) else Color(0x224F2BC4)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = if (isDark) GlowLilac else StreamViolet,
                    modifier = Modifier.size(30.dp)
                )
            }

            // Top Badge with Lang Code
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isDark) Color(0x800E0A24) else Color(0xCCFFFFFF))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Text(
                    text = language.code.uppercase(),
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = if (isDark) GlowLilac else StreamViolet
                )
            }

            // Bottom Right Action
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(36.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFF19191B))
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = language.name,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = if (isDark) Color.White else TextDarkPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = if (language.channelCount > 0) "${language.channelCount} channels" else "Explore language",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = if (isDark) Color(0xFFA19BD0) else TextDarkSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun getCategoryGradient(name: String, isDark: Boolean): Brush {
    val lower = name.lowercase()
    return when {
        lower.contains("sport") -> Brush.linearGradient(
            listOf(
                if (isDark) Color(0xFF6B1D2F) else Color(0xFFFFECEF),
                if (isDark) Color(0xFF38101E) else Color(0xFFFFF5F7)
            )
        )
        lower.contains("movie") || lower.contains("film") || lower.contains("series") -> Brush.linearGradient(
            listOf(
                if (isDark) Color(0xFF3D1F72) else Color(0xFFF3ECFF),
                if (isDark) Color(0xFF221147) else Color(0xFFF9F5FF)
            )
        )
        lower.contains("news") -> Brush.linearGradient(
            listOf(
                if (isDark) Color(0xFF1D3F75) else Color(0xFFEAF2FF),
                if (isDark) Color(0xFF112348) else Color(0xFFF4F8FF)
            )
        )
        lower.contains("music") -> Brush.linearGradient(
            listOf(
                if (isDark) Color(0xFF75401D) else Color(0xFFFFF2EA),
                if (isDark) Color(0xFF42210E) else Color(0xFFFFF8F4)
            )
        )
        lower.contains("kid") || lower.contains("animat") -> Brush.linearGradient(
            listOf(
                if (isDark) Color(0xFF1D6A42) else Color(0xFFEBFDF3),
                if (isDark) Color(0xFF103A24) else Color(0xFFF4FEF8)
            )
        )
        else -> Brush.linearGradient(
            listOf(
                if (isDark) Color(0xFF2C2266) else Color(0xFFF3F1FC),
                if (isDark) Color(0xFF1E174A) else Color(0xFFFAF9FE)
            )
        )
    }
}

private fun getCategoryIcon(name: String): ImageVector {
    val lower = name.lowercase()
    return when {
        lower.contains("sport") -> Icons.Default.SportsSoccer
        lower.contains("movie") || lower.contains("series") || lower.contains("film") -> Icons.Default.Movie
        lower.contains("music") -> Icons.Default.MusicNote
        lower.contains("news") -> Icons.Default.Tv
        else -> Icons.Default.Public
    }
}
