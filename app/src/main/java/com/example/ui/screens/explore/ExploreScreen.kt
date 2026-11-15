package com.example.ui.screens.explore

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.data.model.Channel
import com.example.data.model.Country
import com.example.data.model.Language
import com.example.domain.FilterOptions
import com.example.domain.GroupType
import com.example.domain.SelectedGroup
import com.example.ui.components.ChannelListItem
import com.example.ui.components.MobiChip
import com.example.ui.components.MobiSearchBar
import com.example.ui.components.MobiSectionHeader
import com.example.ui.components.getCountryFlag
import com.example.ui.components.getLanguageGlyph
import com.example.ui.theme.MobiCardShape
import com.example.ui.theme.MobiIconButtonShape
import com.example.ui.theme.MobiStreamTheme
import com.example.ui.theme.mobiColors
import kotlin.math.abs

private data class ExploreCategoryCardMeta(
    val name: String,
    val icon: ImageVector,
    val countHint: String
)

@OptIn(ExperimentalLayoutApi::class)
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
    onFetchLanguageChannels: (String) -> Unit = {},
    onSelectChannel: (Channel) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    lastWatchedChannelId: String? = null,
    onMenuClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val mobi = MaterialTheme.mobiColors

    var searchQuery by remember { mutableStateOf("") }
    var showAllCategories by remember { mutableStateOf(false) }
    var showAllCountries by remember { mutableStateOf(false) }
    var showAllLanguages by remember { mutableStateOf(false) }

    LaunchedEffect(selectedGroup) {
        if (selectedGroup?.type == GroupType.LANGUAGE) {
            onFetchLanguageChannels(selectedGroup.identifier)
        }
    }

    BackHandler(
        enabled = selectedGroup != null ||
            showAllCategories ||
            showAllCountries ||
            showAllLanguages ||
            searchQuery.isNotBlank()
    ) {
        when {
            searchQuery.isNotBlank() -> {
                searchQuery = ""
                onSearchQueryChange("")
            }
            selectedGroup != null -> onSelectGroup(null)
            showAllCategories -> showAllCategories = false
            showAllCountries -> showAllCountries = false
            showAllLanguages -> showAllLanguages = false
        }
    }

    val categoryCounts = remember(allChannels) {
        allChannels.groupingBy { it.category.trim().lowercase() }.eachCount()
    }

    val exploreCategories = remember(categories, categoryCounts) {
        val baseNames = listOf(
            "Sports", "News", "Movies", "Business", "Comedy",
            "Music", "Documentary", "Kids", "Education", "Science", "Auto", "Entertainment"
        )
        val combined = (baseNames + categories.map { it.name.trim() })
            .filter { it.isNotBlank() }
            .distinct()
        combined.map { name ->
            val icon = when {
                name.contains("sport", ignoreCase = true) -> Icons.Outlined.SportsSoccer
                name.contains("news", ignoreCase = true) -> Icons.Outlined.Newspaper
                name.contains("movie", ignoreCase = true) || name.contains("series", ignoreCase = true) -> Icons.Outlined.Movie
                name.contains("music", ignoreCase = true) -> Icons.Outlined.MusicNote
                name.contains("kid", ignoreCase = true) || name.contains("anim", ignoreCase = true) -> Icons.Outlined.ChildCare
                name.contains("educat", ignoreCase = true) -> Icons.Outlined.School
                name.contains("science", ignoreCase = true) || name.contains("doc", ignoreCase = true) -> Icons.Outlined.Science
                name.contains("auto", ignoreCase = true) -> Icons.Outlined.DirectionsCar
                name.contains("entertain", ignoreCase = true) || name.contains("live", ignoreCase = true) -> Icons.Outlined.LiveTv
                else -> Icons.Outlined.Category
            }
            val count = categoryCounts[name.lowercase()] ?: 0
            ExploreCategoryCardMeta(
                name = name,
                icon = icon,
                countHint = if (count > 0) "$count channels" else "Live TV"
            )
        }
    }

    val exploreCountries = remember(countries) {
        val fallback = listOf(
            "US" to "United States",
            "IN" to "India",
            "GB" to "United Kingdom",
            "CA" to "Canada",
            "AU" to "Australia",
            "QA" to "Qatar",
            "DE" to "Germany",
            "FR" to "France",
            "ES" to "Spain",
            "BR" to "Brazil",
            "JP" to "Japan"
        )
        if (countries.isEmpty()) fallback
        else countries.map { it.code to it.name }.ifEmpty { fallback }
    }

    val exploreLanguages = remember(languages) {
        val fallback = listOf(
            "English", "Hindi", "Punjabi", "Spanish", "Arabic",
            "French", "German", "Urdu", "Tamil", "Telugu", "Russian"
        )
        if (languages.isEmpty()) fallback
        else languages.map { it.name }.filter { it.isNotBlank() }.distinct().ifEmpty { fallback }
    }

    val groupFilteredChannels = remember(allChannels, selectedGroup, searchQuery) {
        val base = when (selectedGroup?.type) {
            GroupType.CATEGORY -> {
                val matched = allChannels.filter {
                    it.category.equals(selectedGroup.identifier, ignoreCase = true)
                }
                sortCategoryChannels(selectedGroup.identifier, matched)
            }
            GroupType.COUNTRY -> allChannels.filter {
                it.countryCode.equals(selectedGroup.identifier, ignoreCase = true) ||
                    it.country.equals(selectedGroup.identifier, ignoreCase = true)
            }
            GroupType.LANGUAGE -> allChannels.filter {
                it.language.contains(selectedGroup.identifier, ignoreCase = true)
            }
            null -> allChannels
        }

        if (searchQuery.isBlank()) {
            base
        } else {
            base.filter { channel ->
                channel.name.contains(searchQuery, ignoreCase = true) ||
                    channel.category.contains(searchQuery, ignoreCase = true) ||
                    channel.country.contains(searchQuery, ignoreCase = true) ||
                    channel.language.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .testTag("explore_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 4.dp, bottom = 110.dp)
        ) {
            // 1. Search Bar pill + round accent menu button
            item {
                MobiSearchBar(
                    query = searchQuery,
                    onQueryChange = { q ->
                        searchQuery = q
                        onSearchQueryChange(q)
                    },
                    hint = "Search 11,000+ channels by name...",
                    onMenuClick = onMenuClick
                )
            }

            // 2. Drilldown / See-all subview header when active
            if (selectedGroup != null || showAllCategories || showAllCountries || showAllLanguages) {
                item {
                    val subTitle = when {
                        selectedGroup != null -> selectedGroup.title
                        showAllCategories -> "Categories"
                        showAllCountries -> "Countries"
                        else -> "Languages"
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(MobiIconButtonShape)
                                .clickable {
                                    when {
                                        selectedGroup != null -> onSelectGroup(null)
                                        showAllCategories -> showAllCategories = false
                                        showAllCountries -> showAllCountries = false
                                        showAllLanguages -> showAllLanguages = false
                                    }
                                },
                            shape = MobiIconButtonShape,
                            color = colors.surface,
                            border = BorderStroke(1.dp, colors.outline)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = colors.onSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = subTitle,
                                style = MaterialTheme.typography.headlineMedium,
                                color = colors.onBackground
                            )
                            if (selectedGroup != null) {
                                Text(
                                    text = "${groupFilteredChannels.size} channels",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                // Main Explore Intro
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Discover 11,000+ Channels",
                            style = MaterialTheme.typography.headlineMedium,
                            color = colors.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Browse live television across 50+ categories, 150+ countries, and all global languages.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }

            // 3. Expanded sub-grids for See all modes
            when {
                showAllCategories && selectedGroup == null -> {
                    item {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            exploreCategories.forEach { catMeta ->
                                ExploreCategoryTile(
                                    meta = catMeta,
                                    onClick = {
                                        showAllCategories = false
                                        onSelectGroup(
                                            SelectedGroup(
                                                type = GroupType.CATEGORY,
                                                title = catMeta.name,
                                                identifier = catMeta.name
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                showAllCountries && selectedGroup == null -> {
                    item {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            exploreCountries.forEach { (cc, name) ->
                                MobiChip(
                                    label = name,
                                    leadingGlyph = getCountryFlag(cc),
                                    selected = false,
                                    onClick = {
                                        showAllCountries = false
                                        onSelectGroup(
                                            SelectedGroup(
                                                type = GroupType.COUNTRY,
                                                title = "${getCountryFlag(cc)} $name",
                                                identifier = cc
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                showAllLanguages && selectedGroup == null -> {
                    item {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            exploreLanguages.forEach { lang ->
                                MobiChip(
                                    label = lang,
                                    leadingGlyph = getLanguageGlyph(lang),
                                    selected = false,
                                    onClick = {
                                        showAllLanguages = false
                                        onSelectGroup(
                                            SelectedGroup(
                                                type = GroupType.LANGUAGE,
                                                title = "$lang Channels",
                                                identifier = lang
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                selectedGroup == null && searchQuery.isBlank() -> {
                    // Categories Section with "See all"
                    item {
                        MobiSectionHeader(
                            title = "Categories",
                            actionText = "See all",
                            onActionClick = { showAllCategories = true }
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            items(exploreCategories.take(10), key = { it.name }) { catMeta ->
                                ExploreCategoryTile(
                                    meta = catMeta,
                                    onClick = {
                                        onSelectGroup(
                                            SelectedGroup(
                                                type = GroupType.CATEGORY,
                                                title = catMeta.name,
                                                identifier = catMeta.name
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }

                    // Countries Section with "See all"
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        MobiSectionHeader(
                            title = "Countries",
                            actionText = "See all",
                            onActionClick = { showAllCountries = true }
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(exploreCountries.take(12), key = { it.first }) { (cc, name) ->
                                MobiChip(
                                    label = name,
                                    leadingGlyph = getCountryFlag(cc),
                                    selected = false,
                                    onClick = {
                                        onSelectGroup(
                                            SelectedGroup(
                                                type = GroupType.COUNTRY,
                                                title = "${getCountryFlag(cc)} $name",
                                                identifier = cc
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }

                    // Languages Section with "See all"
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        MobiSectionHeader(
                            title = "Languages",
                            actionText = "See all",
                            onActionClick = { showAllLanguages = true }
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(exploreLanguages.take(12)) { lang ->
                                MobiChip(
                                    label = lang,
                                    leadingGlyph = getLanguageGlyph(lang),
                                    selected = false,
                                    onClick = {
                                        onSelectGroup(
                                            SelectedGroup(
                                                type = GroupType.LANGUAGE,
                                                title = "$lang Channels",
                                                identifier = lang
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 4. Channel List Section (either drilldown, search results, or all channels preview)
            if (!showAllCategories && !showAllCountries && !showAllLanguages) {
                val listHeader = when {
                    selectedGroup != null -> "${selectedGroup.title} (${groupFilteredChannels.size})"
                    searchQuery.isNotBlank() -> "Search Results (${groupFilteredChannels.size})"
                    else -> "All Channels (${groupFilteredChannels.size})"
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    MobiSectionHeader(
                        title = listHeader,
                        actionText = if (selectedGroup != null) "Clear" else null,
                        onActionClick = { onSelectGroup(null) }
                    )
                }

                items(groupFilteredChannels, key = { it.id }) { channel ->
                    ChannelListItem(
                        channel = channel,
                        onClick = { onSelectChannel(channel) },
                        onToggleFavorite = { onToggleFavorite(channel.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ExploreCategoryTile(
    meta: ExploreCategoryCardMeta,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mobi = MaterialTheme.mobiColors
    val baseColor = mobi.channelTileColors[abs(meta.name.hashCode()) % mobi.channelTileColors.size]
    val tileBrush = Brush.linearGradient(
        colors = listOf(baseColor, mobi.heroGradientEnd)
    )

    Box(
        modifier = modifier
            .size(width = 152.dp, height = 104.dp)
            .clip(MobiCardShape)
            .background(tileBrush)
            .clickable { onClick() }
            .padding(14.dp)
            .testTag("category_card_${meta.name}")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = meta.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Text(
                    text = meta.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = meta.countHint,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.80f),
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ExploreScreenPreview() {
    val sample = Channel(
        id = "cnn",
        name = "CNN International",
        logoUrl = "",
        streamUrl = "",
        category = "News",
        country = "United States",
        countryCode = "US",
        language = "English",
        quality = "FHD",
        isLive = true
    )
    MobiStreamTheme {
        ExploreScreen(
            allChannels = listOf(sample, sample.copy(id = "espn", name = "ESPN", category = "Sports")),
            countryChannels = listOf(sample),
            categories = listOf(Category("News", "News", channelCount = 320), Category("Sports", "Sports", channelCount = 210)),
            countries = emptyList(),
            languages = emptyList(),
            detectedCountryName = "United States",
            detectedCountryCode = "US",
            filterOptions = FilterOptions(),
            isDarkMode = false,
            selectedGroup = null,
            onSelectGroup = {},
            sortCategoryChannels = { _, list -> list },
            onSelectChannel = {},
            onToggleFavorite = {},
            onSearchQueryChange = {}
        )
    }
}
