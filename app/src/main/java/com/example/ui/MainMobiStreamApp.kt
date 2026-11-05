package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Channel
import com.example.ui.components.MobiStreamBottomNav
import com.example.ui.components.NavDestination
import com.example.ui.screens.explore.ExploreScreen
import com.example.ui.screens.favorites.FavoritesScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.player.PlayerScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MobiStreamTheme
import com.example.ui.viewmodel.MobiStreamViewModel

enum class ScreenState {
    SPLASH,
    MAIN,
    PLAYER
}

@Composable
fun MainMobiStreamApp(
    viewModel: MobiStreamViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allChannels by viewModel.allChannels.collectAsStateWithLifecycle()
    val filteredChannels by viewModel.filteredChannels.collectAsStateWithLifecycle()
    val featuredChannels by viewModel.featuredChannels.collectAsStateWithLifecycle()
    val popularChannels by viewModel.popularChannels.collectAsStateWithLifecycle()
    val favoriteChannels by viewModel.favoriteChannels.collectAsStateWithLifecycle()

    var screenState by remember { mutableStateOf(ScreenState.SPLASH) }
    var currentDestination by remember { mutableStateOf(NavDestination.HOME) }
    var playingChannel by remember { mutableStateOf<Channel?>(null) }

    MobiStreamTheme(darkTheme = uiState.isDarkMode) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MidnightBackground)
        ) {
            when (screenState) {
                ScreenState.SPLASH -> {
                    SplashScreen(
                        isLoading = uiState.isLoading,
                        onSplashFinished = {
                            screenState = ScreenState.MAIN
                        }
                    )
                }

                ScreenState.PLAYER -> {
                    val current = playingChannel
                    if (current != null) {
                        val related = allChannels.filter { it.category == current.category }
                        PlayerScreen(
                            channel = current,
                            relatedChannels = related,
                            onBack = {
                                screenState = ScreenState.MAIN
                            },
                            onToggleFavorite = { id ->
                                viewModel.toggleFavorite(id)
                                if (current.id == id) {
                                    playingChannel = current.copy(isFavorite = !current.isFavorite)
                                }
                            },
                            onSelectChannel = { newChannel ->
                                playingChannel = newChannel
                            }
                        )
                    } else {
                        screenState = ScreenState.MAIN
                    }
                }

                ScreenState.MAIN -> {
                    // Back handler for main tabs
                    BackHandler(enabled = currentDestination != NavDestination.HOME) {
                        currentDestination = NavDestination.HOME
                    }

                    Scaffold(
                        bottomBar = {
                            MobiStreamBottomNav(
                                currentDestination = currentDestination,
                                onNavigate = { destination ->
                                    currentDestination = destination
                                }
                            )
                        },
                        containerColor = MidnightBackground
                    ) { innerPadding ->
                        AnimatedContent(
                            targetState = currentDestination,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "navScreenTransition",
                            modifier = Modifier.padding(innerPadding)
                        ) { destination ->
                            when (destination) {
                                NavDestination.HOME -> {
                                    HomeScreen(
                                        channels = filteredChannels,
                                        featuredChannels = featuredChannels,
                                        popularChannels = popularChannels,
                                        categories = uiState.availableCategories,
                                        filterOptions = uiState.filterOptions,
                                        isLoading = uiState.isLoading,
                                        onSelectChannel = { ch ->
                                            playingChannel = ch
                                            screenState = ScreenState.PLAYER
                                        },
                                        onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                                        onSearchQueryChange = { q -> viewModel.updateSearchQuery(q) },
                                        onSelectCategory = { cat -> viewModel.setCategoryFilter(cat) },
                                        onApplyFilters = { filters ->
                                            viewModel.setCategoryFilter(filters.selectedCategory)
                                            viewModel.setCountryFilter(filters.selectedCountry)
                                            viewModel.setLanguageFilter(filters.selectedLanguage)
                                            viewModel.setQualityFilter(filters.selectedQuality)
                                        },
                                        onNavigateToExplore = {
                                            currentDestination = NavDestination.EXPLORE
                                        }
                                    )
                                }

                                NavDestination.EXPLORE -> {
                                    ExploreScreen(
                                        channels = filteredChannels,
                                        categories = uiState.availableCategories,
                                        countries = uiState.availableCountries,
                                        languages = uiState.availableLanguages,
                                        filterOptions = uiState.filterOptions,
                                        onSelectChannel = { ch ->
                                            playingChannel = ch
                                            screenState = ScreenState.PLAYER
                                        },
                                        onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                                        onSearchQueryChange = { q -> viewModel.updateSearchQuery(q) },
                                        onSetCategory = { cat -> viewModel.setCategoryFilter(cat) },
                                        onSetCountry = { c -> viewModel.setCountryFilter(c) },
                                        onSetLanguage = { l -> viewModel.setLanguageFilter(l) },
                                        onResetFilters = { viewModel.resetFilters() },
                                        onApplyFilters = { filters ->
                                            viewModel.setCategoryFilter(filters.selectedCategory)
                                            viewModel.setCountryFilter(filters.selectedCountry)
                                            viewModel.setLanguageFilter(filters.selectedLanguage)
                                            viewModel.setQualityFilter(filters.selectedQuality)
                                        }
                                    )
                                }

                                NavDestination.FAVORITES -> {
                                    FavoritesScreen(
                                        favoriteChannels = favoriteChannels,
                                        onSelectChannel = { ch ->
                                            playingChannel = ch
                                            screenState = ScreenState.PLAYER
                                        },
                                        onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                                        onExploreChannels = {
                                            currentDestination = NavDestination.EXPLORE
                                        }
                                    )
                                }

                                NavDestination.SETTINGS -> {
                                    SettingsScreen(
                                        isDarkMode = uiState.isDarkMode,
                                        bufferMode = uiState.streamBufferMode,
                                        onToggleTheme = { isDark -> viewModel.toggleTheme(isDark) },
                                        onSetBufferMode = { mode -> viewModel.setStreamBufferMode(mode) },
                                        onClearCache = { viewModel.loadData(forceRefresh = true) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
