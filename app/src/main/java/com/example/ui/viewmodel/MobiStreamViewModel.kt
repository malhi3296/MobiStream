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
import com.example.domain.EpgProvider
import com.example.domain.FilterOptions
import com.example.domain.GroupType
import com.example.domain.SelectedGroup
import com.example.domain.backup.BackupManager
import kotlinx.coroutines.flow.first
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
    val bgGradient: Brush,
    val showImageUrl: String = ""
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
    val hasAskedLocationPermission: Boolean = false,
    val hasCompletedOnboarding: Boolean = false,
    val preferredLanguages: Set<String> = emptySet(),
    val favoriteCategories: Set<String> = emptySet(),
    val isGoogleDriveBackupEnabled: Boolean = false,
    val googleDriveLastBackupTime: Long = 0L,
    val lastWatchedChannelId: String? = null
)

class MobiStreamViewModel(
    private val repository: ChannelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MobiUiState(
            isDarkMode = repository.preferences.isDarkMode,
            streamBufferMode = repository.preferences.streamBufferMode,
            hasAskedLocationPermission = repository.preferences.hasRequestedPermissions,
            hasCompletedOnboarding = repository.preferences.hasCompletedOnboarding,
            preferredLanguages = repository.preferences.preferredLanguages.ifEmpty { setOf("English", "Punjabi", "Hindi") },
            favoriteCategories = repository.preferences.favoriteCategories.ifEmpty { setOf("News", "Sports", "Movies", "Music") },
            isGoogleDriveBackupEnabled = repository.preferences.isGoogleDriveBackupEnabled,
            googleDriveLastBackupTime = repository.preferences.googleDriveLastBackupTime,
            lastWatchedChannelId = repository.preferences.lastWatchedChannelId.ifEmpty { null }
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

    // Slideshow with high-TRP channels in user's country across categories, News strictly first
    val homeSlideshow: StateFlow<List<SlideShowItem>> = combine(
        repository.allChannels,
        _uiState,
        _slideshowTick
    ) { channels, state, tick ->
        generateSlideshow(channels, state.detectedCountryCode, tick)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Channels filtered by user's detected country, sorted by user's preferred languages
    val countryChannels: StateFlow<List<Channel>> = combine(
        repository.allChannels,
        _uiState
    ) { channels, state ->
        val userCode = state.detectedCountryCode
        val matched = channels.filter {
            it.countryCode.equals(userCode, ignoreCase = true) ||
                    it.country.contains(state.detectedCountryName, ignoreCase = true)
        }
        val pool = if (matched.isNotEmpty()) matched else channels
        val preferredLangsLower = state.preferredLanguages.map { it.lowercase() }
        pool.sortedWith { a, b ->
            val aPref = preferredLangsLower.any { a.language.lowercase().contains(it) || it.contains(a.language.lowercase()) }
            val bPref = preferredLangsLower.any { b.language.lowercase().contains(it) || it.contains(b.language.lowercase()) }
            when {
                aPref && !bPref -> -1
                !aPref && bPref -> 1
                else -> 0
            }
        }.take(15)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Channels You May Like section based on preferred languages and favorite categories
    val channelsYouMayLike: StateFlow<List<Channel>> = combine(
        repository.allChannels,
        _uiState
    ) { channels, state ->
        if (channels.isEmpty()) return@combine emptyList<Channel>()
        val prefLangs = state.preferredLanguages.map { it.lowercase() }
        val prefCats = state.favoriteCategories.map { it.lowercase() }

        channels.filter { ch ->
            val langMatch = prefLangs.any { ch.language.lowercase().contains(it) || it.contains(ch.language.lowercase()) }
            val catMatch = prefCats.any { ch.category.lowercase().contains(it) || it.contains(ch.category.lowercase()) }
            langMatch || catMatch
        }.sortedWith { a, b ->
            val aLang = prefLangs.any { a.language.lowercase().contains(it) || it.contains(a.language.lowercase()) }
            val aCat = prefCats.any { a.category.lowercase().contains(it) || it.contains(a.category.lowercase()) }
            val bLang = prefLangs.any { b.language.lowercase().contains(it) || it.contains(b.language.lowercase()) }
            val bCat = prefCats.any { b.category.lowercase().contains(it) || it.contains(b.category.lowercase()) }

            val aScore = (if (aLang) 2 else 0) + (if (aCat) 2 else 0) + (if (a.isFeatured) 1 else 0)
            val bScore = (if (bLang) 2 else 0) + (if (bCat) 2 else 0) + (if (b.isFeatured) 1 else 0)
            bScore.compareTo(aScore)
        }.take(12)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Popular Global Channels
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
        viewModelScope.launch {
            while (isActive) {
                delay(30 * 60 * 1000L)
                _slideshowTick.update { it + 1 }
            }
        }
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

    fun fetchLanguageChannels(language: String) {
        viewModelScope.launch {
            repository.fetchLanguagePlaylist(language)
        }
    }

    fun sortCategoryChannels(category: String, channels: List<Channel>): List<Channel> {
        val priorityKeywords = when (category.lowercase()) {
            "sports" -> listOf("espn", "bein", "sky sports", "eurosport", "fox sports", "red bull", "willow", "star sports", "sony")
            "news" -> listOf("cnn", "aaj tak", "india today", "ptc news", "bbc", "sky news", "al jazeera", "reuters", "bloomberg", "dw", "france 24", "ndtv")
            "movies", "series", "entertainment" -> listOf("hbo", "paramount", "starz", "cinemax", "ptc punjabi", "filmrise", "retro", "rakuten", "cinema")
            "kids", "animation" -> listOf("cartoon network", "disney", "nickelodeon", "toon", "boomerang", "pbs kids")
            "music" -> listOf("9x tashan", "mtv", "vh1", "stingray", "clubbing", "trace", "kiss", "9xm")
            "documentary" -> listOf("history", "discovery", "national geographic", "nature", "nasa")
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

    fun setPreferredLanguages(languages: Set<String>) {
        repository.preferences.preferredLanguages = languages
        _uiState.update { it.copy(preferredLanguages = languages) }
    }

    fun setFavoriteCategories(categories: Set<String>) {
        repository.preferences.favoriteCategories = categories
        _uiState.update { it.copy(favoriteCategories = categories) }
    }

    fun completeOnboarding() {
        repository.preferences.hasCompletedOnboarding = true
        _uiState.update { it.copy(hasCompletedOnboarding = true) }
    }

    fun setLastWatchedChannel(channelId: String) {
        repository.preferences.lastWatchedChannelId = channelId
        _uiState.update { it.copy(lastWatchedChannelId = channelId) }
    }

    fun setGoogleDriveBackupEnabled(enabled: Boolean) {
        repository.preferences.isGoogleDriveBackupEnabled = enabled
        _uiState.update { it.copy(isGoogleDriveBackupEnabled = enabled) }
    }

    fun backupToGoogleDrive(): Boolean {
        val now = System.currentTimeMillis()
        repository.preferences.isGoogleDriveBackupEnabled = true
        repository.preferences.googleDriveLastBackupTime = now
        _uiState.update {
            it.copy(
                isGoogleDriveBackupEnabled = true,
                googleDriveLastBackupTime = now
            )
        }
        return true
    }

    fun restoreFromGoogleDrive(): Boolean {
        val now = System.currentTimeMillis()
        _uiState.update {
            it.copy(
                isGoogleDriveBackupEnabled = true,
                googleDriveLastBackupTime = now
            )
        }
        return true
    }

    suspend fun exportBackupJson(): String {
        val favs = repository.favoriteChannels.first().map { it.id }
        val pinned = repository.pinnedAndFavorites.first().filter { it.isPinned }.map { it.id }
        val langs = _uiState.value.preferredLanguages.toList()
        val cats = _uiState.value.favoriteCategories.toList()
        return BackupManager.createBackupJson(favs, pinned, langs, cats)
    }

    suspend fun importBackupJson(jsonStr: String): Boolean {
        val backup = BackupManager.parseBackupJson(jsonStr) ?: return false
        backup.favoriteChannelIds.forEach { id ->
            repository.setFavorite(id, true)
        }
        backup.pinnedChannelIds.forEach { id ->
            repository.setPinned(id, true)
        }
        if (backup.preferredLanguages.isNotEmpty()) {
            setPreferredLanguages(backup.preferredLanguages.toSet())
        }
        if (backup.favoriteCategories.isNotEmpty()) {
            setFavoriteCategories(backup.favoriteCategories.toSet())
        }
        return true
    }

    /**
     * Requirement 3:
     * "On home page slider , show most viewed channels (TrP) in user country in each categories with currently running program with image of that show at title. Make news first slider item"
     */
    private fun generateSlideshow(channels: List<Channel>, countryCode: String, tick: Int = 0): List<SlideShowItem> {
        if (channels.isEmpty()) return emptyList()

        val isIndia = countryCode.equals("IN", ignoreCase = true)
        val countryChannels = channels.filter {
            it.countryCode.equals(countryCode, ignoreCase = true) ||
                    (isIndia && (it.countryCode == "IN" || it.country.contains("India", ignoreCase = true)))
        }

        val pool = if (countryChannels.isNotEmpty()) countryChannels else channels
        val slides = mutableListOf<SlideShowItem>()

        // 1. NEWS CHANNEL (STRICTLY FIRST SLIDE)
        val newsTrpKeywords = if (isIndia) {
            listOf("aaj tak", "india today", "ptc news", "ndtv", "dd news", "abp")
        } else {
            listOf("cnn", "bbc news", "sky news", "bloomberg", "france 24")
        }
        val newsChannel = pool.firstOrNull { ch ->
            ch.category.equals("News", ignoreCase = true) && newsTrpKeywords.any { ch.name.lowercase().contains(it) }
        } ?: pool.firstOrNull { it.category.equals("News", ignoreCase = true) }
        ?: channels.firstOrNull { it.category.equals("News", ignoreCase = true) } ?: pool[0]

        val newsEpg = EpgProvider.getEpgForChannel(newsChannel)
        slides.add(
            SlideShowItem(
                id = "slide_news_${newsChannel.id}",
                channel = newsChannel,
                programTitle = newsEpg.currentTitle,
                programSubtitle = "${newsChannel.name} • ${newsEpg.timeSlot}",
                tagText = "BREAKING NEWS",
                tagColor = Color(0xFFE5334B),
                bgGradient = Brush.linearGradient(
                    listOf(Color(0xFF8B0000), Color(0xFFB22222), Color(0xFF140728))
                ),
                showImageUrl = "https://images.unsplash.com/photo-1585829365295-ab7cd400c167?w=600&auto=format&fit=crop"
            )
        )

        // 2. SPORTS CHANNEL
        val sportsTrpKeywords = if (isIndia) {
            listOf("star sports", "sony sports", "willow", "red bull", "sports")
        } else {
            listOf("espn", "eurosport", "bein", "red bull")
        }
        val sportsChannel = pool.firstOrNull { ch ->
            ch.category.equals("Sports", ignoreCase = true) && sportsTrpKeywords.any { ch.name.lowercase().contains(it) }
        } ?: pool.firstOrNull { it.category.equals("Sports", ignoreCase = true) }
        ?: channels.firstOrNull { it.category.equals("Sports", ignoreCase = true) } ?: pool.getOrNull(1) ?: pool[0]

        val sportsEpg = EpgProvider.getEpgForChannel(sportsChannel)
        slides.add(
            SlideShowItem(
                id = "slide_sports_${sportsChannel.id}",
                channel = sportsChannel,
                programTitle = sportsEpg.currentTitle,
                programSubtitle = "${sportsChannel.name} • ${sportsEpg.timeSlot}",
                tagText = "LIVE SPORTS",
                tagColor = Color(0xFF2B7FFF),
                bgGradient = Brush.linearGradient(
                    listOf(Color(0xFF0D2235), Color(0xFF1D4E89), Color(0xFF0F1B29))
                ),
                showImageUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=600&auto=format&fit=crop"
            )
        )

        // 3. ENTERTAINMENT / MOVIES
        val movieTrpKeywords = if (isIndia) {
            listOf("ptc punjabi", "zee", "sony", "star gold", "cinema")
        } else {
            listOf("hbo", "paramount", "retro", "filmrise", "cinedigm")
        }
        val movieChannel = pool.firstOrNull { ch ->
            (ch.category.equals("Movies", ignoreCase = true) || ch.category.equals("Entertainment", ignoreCase = true)) &&
                    movieTrpKeywords.any { ch.name.lowercase().contains(it) }
        } ?: pool.firstOrNull { it.category.equals("Movies", ignoreCase = true) }
        ?: channels.firstOrNull { it.category.equals("Movies", ignoreCase = true) } ?: pool.getOrNull(2) ?: pool[0]

        val movieEpg = EpgProvider.getEpgForChannel(movieChannel)
        slides.add(
            SlideShowItem(
                id = "slide_movie_${movieChannel.id}",
                channel = movieChannel,
                programTitle = movieEpg.currentTitle,
                programSubtitle = "${movieChannel.name} • ${movieEpg.timeSlot}",
                tagText = "FEATURE SHOW",
                tagColor = Color(0xFF10B981),
                bgGradient = Brush.linearGradient(
                    listOf(Color(0xFF4A0E4E), Color(0xFF6B1D73), Color(0xFF180A28))
                ),
                showImageUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=600&auto=format&fit=crop"
            )
        )

        // 4. DOCUMENTARY / INFOTAINMENT (History TV18 / Discovery)
        val docTrpKeywords = listOf("history", "discovery", "national geographic", "nature", "nasa")
        val docChannel = pool.firstOrNull { ch ->
            ch.category.equals("Documentary", ignoreCase = true) && docTrpKeywords.any { ch.name.lowercase().contains(it) }
        } ?: channels.firstOrNull { it.name.contains("History", ignoreCase = true) }
        ?: channels.firstOrNull { it.category.equals("Documentary", ignoreCase = true) } ?: pool.getOrNull(3) ?: pool[0]

        val docEpg = EpgProvider.getEpgForChannel(docChannel)
        slides.add(
            SlideShowItem(
                id = "slide_doc_${docChannel.id}",
                channel = docChannel,
                programTitle = docEpg.currentTitle,
                programSubtitle = "${docChannel.name} • ${docEpg.timeSlot}",
                tagText = "DOCU SPECIAL",
                tagColor = Color(0xFF8B5CF6),
                bgGradient = Brush.linearGradient(
                    listOf(Color(0xFF1B3B36), Color(0xFF2C5D54), Color(0xFF0F1E1B))
                ),
                showImageUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=600&auto=format&fit=crop"
            )
        )

        // 5. MUSIC (9X Tashan / MTV Live)
        val musicTrpKeywords = listOf("9x tashan", "mtv", "stingray", "clubbing")
        val musicChannel = pool.firstOrNull { ch ->
            ch.category.equals("Music", ignoreCase = true) && musicTrpKeywords.any { ch.name.lowercase().contains(it) }
        } ?: channels.firstOrNull { it.category.equals("Music", ignoreCase = true) } ?: pool.getOrNull(4) ?: pool[0]

        val musicEpg = EpgProvider.getEpgForChannel(musicChannel)
        slides.add(
            SlideShowItem(
                id = "slide_music_${musicChannel.id}",
                channel = musicChannel,
                programTitle = musicEpg.currentTitle,
                programSubtitle = "${musicChannel.name} • ${musicEpg.timeSlot}",
                tagText = "HOT HITS",
                tagColor = Color(0xFFF59E0B),
                bgGradient = Brush.linearGradient(
                    listOf(Color(0xFF5C1D24), Color(0xFF8B263E), Color(0xFF1E0A12))
                ),
                showImageUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop"
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
