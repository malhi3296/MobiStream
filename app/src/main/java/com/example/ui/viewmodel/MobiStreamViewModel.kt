package com.example.ui.viewmodel

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Category
import com.example.data.model.Channel
import com.example.data.model.Country
import com.example.data.model.Language
import com.example.data.repository.ChannelRepository
import com.example.domain.FilterOptions
import com.example.domain.GroupType
import com.example.domain.SelectedGroup
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class SlideShowItem(
    val id: String,
    val channel: Channel,
    val programTitle: String,
    val programSubtitle: String,
    val tagText: String,
    val tagColor: Color,
    val bgGradient: Brush
)

data class MobiUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val selectedChannel: Channel? = null,
    val filterOptions: FilterOptions = FilterOptions(),
    val availableCategories: List<Category> = emptyList(),
    val availableCountries: List<Country> = emptyList(),
    val availableLanguages: List<Language> = emptyList(),
    val isDarkMode: Boolean = false,
    val streamBufferMode: String = "Low Latency",
    val detectedCountryCode: String = "US",
    val detectedCountryName: String = "United States",
    val hasAskedLocationPermission: Boolean = false
)

class MobiStreamViewModel(
    private val repository: ChannelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MobiUiState(
            isDarkMode = repository.preferences.isDarkMode,
            streamBufferMode = repository.preferences.streamBufferMode,
            hasAskedLocationPermission = repository.preferences.hasRequestedPermissions
        )
    )
    val uiState: StateFlow<MobiUiState> = _uiState.asStateFlow()

    // Hoisted Selected Explore Group so back navigation from Player preserves channel list
    private val _selectedExploreGroup = MutableStateFlow<SelectedGroup?>(null)
    val selectedExploreGroup: StateFlow<SelectedGroup?> = _selectedExploreGroup.asStateFlow()

    // Rotation ticks for slideshow (every 30 mins) and trending (every 2 hours)
    private val _slideshowTick = MutableStateFlow(0)
    private val _trendingTick = MutableStateFlow(0)

    val allChannels: StateFlow<List<Channel>> = repository.allChannels
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteChannels: StateFlow<List<Channel>> = repository.favoriteChannels
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Pinned & Favorites (Pinned channels first)
    val pinnedAndFavorites: StateFlow<List<Channel>> = repository.pinnedAndFavorites
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Slideshow with 5 slides rotated every 30 minutes
    val homeSlideshow: StateFlow<List<SlideShowItem>> = combine(
        repository.allChannels,
        _slideshowTick
    ) { channels, tick ->
        generateSlideshow(channels, tick)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Channels filtered by user's detected country
    val countryChannels: StateFlow<List<Channel>> = combine(
        repository.allChannels,
        _uiState
    ) { channels, state ->
        val userCode = state.detectedCountryCode
        val matched = channels.filter {
            it.countryCode.equals(userCode, ignoreCase = true) ||
                    it.country.contains(state.detectedCountryName, ignoreCase = true)
        }
        if (matched.isNotEmpty()) matched else channels.take(10)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Popular Global Channels (ESPN, BBC, CNN, Eurosport, Sky News, Red Bull, Bloomberg, France 24, MTV, Cartoon Network)
    val globalPopularChannels: StateFlow<List<Channel>> = repository.allChannels.combine(_uiState) { channels, _ ->
        val globalKeywords = listOf(
            "espn", "bbc", "cnn", "eurosport", "sky news", "red bull",
            "bloomberg", "france 24", "mtv", "cartoon network", "al jazeera", "reuters", "dw news"
        )
        val matched = channels.filter { ch ->
            val nameLower = ch.name.lowercase()
            globalKeywords.any { kw -> nameLower.contains(kw) }
        }
        if (matched.isNotEmpty()) matched else channels.take(10)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Trending channels updated every 2 hours
    val trendingChannels: StateFlow<List<Channel>> = combine(
        repository.allChannels,
        _trendingTick
    ) { channels, tick ->
        if (channels.isEmpty()) return@combine emptyList<Channel>()
        val featured = channels.filter { it.isFeatured }
        val pool = if (featured.isNotEmpty()) featured else channels
        val offset = (tick * 5) % pool.size
        val rotated = pool.drop(offset) + pool.take(offset)
        rotated.take(10)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredChannels: StateFlow<List<Channel>> = combine(
        repository.allChannels,
        _uiState
    ) { channels, state ->
        val filters = state.filterOptions
        channels.filter { ch ->
            val matchesCategory = filters.selectedCategory == "All" ||
                    ch.category.equals(filters.selectedCategory, ignoreCase = true)

            val matchesCountry = filters.selectedCountry == "All" ||
                    ch.countryCode.equals(filters.selectedCountry, ignoreCase = true) ||
                    ch.country.equals(filters.selectedCountry, ignoreCase = true)

            val matchesLanguage = filters.selectedLanguage == "All" ||
                    ch.language.equals(filters.selectedLanguage, ignoreCase = true)

            val matchesQuality = filters.selectedQuality == "All" ||
                    ch.quality.equals(filters.selectedQuality, ignoreCase = true)

            val matchesSearch = filters.searchQuery.isBlank() ||
                    ch.name.contains(filters.searchQuery, ignoreCase = true) ||
                    ch.category.contains(filters.searchQuery, ignoreCase = true) ||
                    ch.country.contains(filters.searchQuery, ignoreCase = true)

            matchesCategory && matchesCountry && matchesLanguage && matchesQuality && matchesSearch
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        val (code, name) = repository.detectDeviceCountry()
        _uiState.update {
            it.copy(
                detectedCountryCode = code,
                detectedCountryName = name
            )
        }
        loadData()
        startPeriodicUpdaters()
    }

    private fun startPeriodicUpdaters() {
        // Slideshow ticker: updates every 30 minutes
        viewModelScope.launch {
            while (isActive) {
                delay(30 * 60 * 1000L)
                _slideshowTick.update { it + 1 }
            }
        }
        // Trending ticker: updates every 2 hours
        viewModelScope.launch {
            while (isActive) {
                delay(2 * 60 * 60 * 1000L)
                _trendingTick.update { it + 1 }
            }
        }
    }

    fun selectExploreGroup(group: SelectedGroup?) {
        _selectedExploreGroup.value = group
    }

    fun selectExploreCategory(categoryName: String) {
        _selectedExploreGroup.value = SelectedGroup(
            type = GroupType.CATEGORY,
            title = categoryName,
            identifier = categoryName
        )
    }

    fun selectExploreCountry(countryCode: String, countryName: String, flag: String) {
        _selectedExploreGroup.value = SelectedGroup(
            type = GroupType.COUNTRY,
            title = "$flag $countryName",
            identifier = countryCode
        )
    }

    fun selectExploreLanguage(languageName: String) {
        _selectedExploreGroup.value = SelectedGroup(
            type = GroupType.LANGUAGE,
            title = "$languageName Channels",
            identifier = languageName
        )
    }

    /**
     * Requirement 14: In explore, every category all popular global channels should be at top
     * like ESPN for sports, CNN for news, HBO/Retro for movies, etc.
     */
    fun sortCategoryChannels(category: String, channels: List<Channel>): List<Channel> {
        val priorityKeywords = when (category.lowercase()) {
            "sports" -> listOf("espn", "bein", "sky sports", "eurosport", "fox sports", "red bull", "willow", "star sports", "sony")
            "news" -> listOf("cnn", "bbc", "sky news", "al jazeera", "reuters", "bloomberg", "dw", "france 24", "ndtv", "india today", "dd news")
            "movies", "series", "entertainment" -> listOf("hbo", "paramount", "starz", "cinemax", "filmrise", "retro", "rakuten", "cinema")
            "kids", "animation" -> listOf("cartoon network", "disney", "nickelodeon", "toon", "boomerang", "pbs kids")
            "music" -> listOf("mtv", "vh1", "stingray", "clubbing", "trace", "kiss", "9xm", "b4u")
            else -> listOf("global", "live", "hd", "news", "tv")
        }

        return channels.sortedWith { a, b ->
            val aName = a.name.lowercase()
            val bName = b.name.lowercase()
            val aMatch = priorityKeywords.indexOfFirst { aName.contains(it) }
            val bMatch = priorityKeywords.indexOfFirst { bName.contains(it) }

            when {
                aMatch != -1 && bMatch != -1 -> aMatch.compareTo(bMatch)
                aMatch != -1 -> -1
                bMatch != -1 -> 1
                else -> a.name.compareTo(b.name, ignoreCase = true)
            }
        }
    }

    fun loadData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (forceRefresh) {
                _uiState.update { it.copy(isRefreshing = true) }
            } else {
                _uiState.update { it.copy(isLoading = true) }
            }

            try {
                repository.initializeChannels(forceRefresh = forceRefresh)
                val categories = repository.getAvailableCategories()
                val countries = repository.getAvailableCountries()
                val languages = repository.getAvailableLanguages()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        availableCategories = categories,
                        availableCountries = countries,
                        availableLanguages = languages
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
            }
        }
    }

    fun onLocationPermissionResult(granted: Boolean) {
        repository.preferences.hasRequestedPermissions = true
        _uiState.update { it.copy(hasAskedLocationPermission = true) }
        val (code, name) = repository.detectDeviceCountry()
        _uiState.update {
            it.copy(
                detectedCountryCode = code,
                detectedCountryName = name
            )
        }
    }

    fun toggleFavorite(channelId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(channelId)
            val current = _uiState.value.selectedChannel
            if (current != null && current.id == channelId) {
                _uiState.update {
                    it.copy(selectedChannel = current.copy(isFavorite = !current.isFavorite))
                }
            }
        }
    }

    fun togglePinned(channelId: String) {
        viewModelScope.launch {
            repository.togglePinned(channelId)
            val current = _uiState.value.selectedChannel
            if (current != null && current.id == channelId) {
                _uiState.update {
                    it.copy(selectedChannel = current.copy(isPinned = !current.isPinned))
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update {
            it.copy(filterOptions = it.filterOptions.copy(searchQuery = query))
        }
    }

    fun setCategoryFilter(category: String) {
        _uiState.update {
            it.copy(filterOptions = it.filterOptions.copy(selectedCategory = category))
        }
    }

    fun setCountryFilter(countryCode: String) {
        _uiState.update {
            it.copy(filterOptions = it.filterOptions.copy(selectedCountry = countryCode))
        }
    }

    fun setLanguageFilter(language: String) {
        _uiState.update {
            it.copy(filterOptions = it.filterOptions.copy(selectedLanguage = language))
        }
    }

    fun setQualityFilter(quality: String) {
        _uiState.update {
            it.copy(filterOptions = it.filterOptions.copy(selectedQuality = quality))
        }
    }

    fun resetFilters() {
        _uiState.update {
            it.copy(filterOptions = FilterOptions(searchQuery = it.filterOptions.searchQuery))
        }
    }

    fun toggleTheme(isDark: Boolean) {
        repository.preferences.isDarkMode = isDark
        _uiState.update { it.copy(isDarkMode = isDark) }
    }

    fun setStreamBufferMode(mode: String) {
        repository.preferences.streamBufferMode = mode
        _uiState.update { it.copy(streamBufferMode = mode) }
    }

    private fun generateSlideshow(channels: List<Channel>, tick: Int = 0): List<SlideShowItem> {
        if (channels.isEmpty()) return emptyList()

        val slides = mutableListOf<SlideShowItem>()

        // 1. News channel with current news program background
        val newsChannels = channels.filter { it.category.equals("News", ignoreCase = true) }
        val newsChannel = if (newsChannels.isNotEmpty()) {
            newsChannels[(tick) % newsChannels.size]
        } else channels[0]

        slides.add(
            SlideShowItem(
                id = "slide_news_${newsChannel.id}",
                channel = newsChannel,
                programTitle = "Breaking World News Bulletin",
                programSubtitle = "${newsChannel.name} • Live Global Reporting",
                tagText = "BREAKING NEWS",
                tagColor = Color(0xFFE5334B),
                bgGradient = Brush.linearGradient(
                    listOf(Color(0xFF990000), Color(0xFFCC1122), Color(0xFF1A1A2E))
                )
            )
        )

        // 2. Most important live sports event (global)
        val sportsChannels = channels.filter { it.category.equals("Sports", ignoreCase = true) && it.id != newsChannel.id }
        val sportsChannel = if (sportsChannels.isNotEmpty()) {
            sportsChannels[(tick) % sportsChannels.size]
        } else channels.getOrNull(1) ?: channels[0]

        slides.add(
            SlideShowItem(
                id = "slide_sports_${sportsChannel.id}",
                channel = sportsChannel,
                programTitle = "World Football Championship",
                programSubtitle = "${sportsChannel.name} • Live Stadium Broadcast",
                tagText = "LIVE SPORTS",
                tagColor = Color(0xFF2B7FFF),
                bgGradient = Brush.linearGradient(
                    listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                )
            )
        )

        // 3. Random slider 3 (Movies / Entertainment)
        val movieChannels = channels.filter { it.category.equals("Movies", ignoreCase = true) && it.id != newsChannel.id && it.id != sportsChannel.id }
        val movieChannel = if (movieChannels.isNotEmpty()) {
            movieChannels[(tick) % movieChannels.size]
        } else channels.getOrNull(2) ?: channels[0]

        slides.add(
            SlideShowItem(
                id = "slide_movie_${movieChannel.id}",
                channel = movieChannel,
                programTitle = "Cinema Classics: Prime Feature",
                programSubtitle = "${movieChannel.name} • FHD Broadcast",
                tagText = "FEATURE FILM",
                tagColor = Color(0xFF10B981),
                bgGradient = Brush.linearGradient(
                    listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0), Color(0xFF1F1C2C))
                )
            )
        )

        // 4. Random slider 4 (Music / Concerts)
        val musicChannels = channels.filter { it.category.equals("Music", ignoreCase = true) && it.id !in slides.map { s -> s.channel.id } }
        val musicChannel = if (musicChannels.isNotEmpty()) {
            musicChannels[(tick) % musicChannels.size]
        } else channels.getOrNull(3) ?: channels[0]

        slides.add(
            SlideShowItem(
                id = "slide_music_${musicChannel.id}",
                channel = musicChannel,
                programTitle = "World Live Tour Concerts",
                programSubtitle = "${musicChannel.name} • Non-stop Hits",
                tagText = "MUSIC LIVE",
                tagColor = Color(0xFFF59E0B),
                bgGradient = Brush.linearGradient(
                    listOf(Color(0xFFFF416C), Color(0xFFFF4B2B), Color(0xFF2B1055))
                )
            )
        )

        // 5. Random slider 5 (Documentary / Nature)
        val docChannels = channels.filter { it.category.equals("Documentary", ignoreCase = true) && it.id !in slides.map { s -> s.channel.id } }
        val docChannel = if (docChannels.isNotEmpty()) {
            docChannels[(tick) % docChannels.size]
        } else channels.getOrNull(4) ?: channels[0]

        slides.add(
            SlideShowItem(
                id = "slide_doc_${docChannel.id}",
                channel = docChannel,
                programTitle = "Planet Frontiers Exploration",
                programSubtitle = "${docChannel.name} • 4K Wildlife & Science",
                tagText = "DOCUMENTARY",
                tagColor = Color(0xFF8B5CF6),
                bgGradient = Brush.linearGradient(
                    listOf(Color(0xFF134E5E), Color(0xFF71B280), Color(0xFF0F2027))
                )
            )
        )

        return slides
    }

    companion object {
        fun provideFactory(repository: ChannelRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MobiStreamViewModel(repository) as T
                }
            }
    }
}
