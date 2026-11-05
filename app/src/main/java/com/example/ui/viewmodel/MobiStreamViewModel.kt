package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Category
import com.example.data.model.Channel
import com.example.data.model.Country
import com.example.data.model.Language
import com.example.data.repository.ChannelRepository
import com.example.domain.FilterOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MobiUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val selectedChannel: Channel? = null,
    val filterOptions: FilterOptions = FilterOptions(),
    val availableCategories: List<Category> = emptyList(),
    val availableCountries: List<Country> = emptyList(),
    val availableLanguages: List<Language> = emptyList(),
    val isDarkMode: Boolean = true,
    val streamBufferMode: String = "Low Latency"
)

class MobiStreamViewModel(
    private val repository: ChannelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MobiUiState())
    val uiState: StateFlow<MobiUiState> = _uiState.asStateFlow()

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

    val featuredChannels: StateFlow<List<Channel>> = repository.allChannels
        .combine(_uiState) { channels, _ ->
            val featured = channels.filter { it.isFeatured }
            if (featured.isNotEmpty()) featured else channels.take(5)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val popularChannels: StateFlow<List<Channel>> = repository.allChannels
        .combine(_uiState) { channels, _ ->
            channels.take(12)
        }
        .stateIn(
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
        loadData()
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

    fun selectChannel(channel: Channel) {
        _uiState.update { it.copy(selectedChannel = channel) }
    }

    fun toggleFavorite(channelId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(channelId)
            // If current playing channel is toggled, update selected channel state
            val current = _uiState.value.selectedChannel
            if (current != null && current.id == channelId) {
                _uiState.update {
                    it.copy(selectedChannel = current.copy(isFavorite = !current.isFavorite))
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
        _uiState.update { it.copy(isDarkMode = isDark) }
    }

    fun setStreamBufferMode(mode: String) {
        _uiState.update { it.copy(streamBufferMode = mode) }
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
