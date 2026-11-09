package com.example.ui

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Channel
import com.example.ui.components.FilterBottomSheet
import com.example.ui.components.MobiStreamBottomNav
import com.example.ui.components.MobiStreamUnifiedTopBar
import com.example.ui.components.NavDestination
import com.example.ui.components.UserInfoModal
import com.example.ui.screens.explore.ExploreScreen
import com.example.ui.screens.favorites.FavoritesScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.player.PlayerScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.theme.MobiStreamTheme
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.OnboardingModal
import com.example.ui.viewmodel.MobiStreamViewModel
import kotlinx.coroutines.launch

enum class ScreenState {
    MAIN,
    PLAYER,
    SETTINGS_OVERLAY
}

@Composable
fun MainMobiStreamApp(
    viewModel: MobiStreamViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allChannels by viewModel.allChannels.collectAsStateWithLifecycle()
    val filteredChannels by viewModel.filteredChannels.collectAsStateWithLifecycle()
    val pinnedAndFavorites by viewModel.pinnedAndFavorites.collectAsStateWithLifecycle()
    val countryChannels by viewModel.countryChannels.collectAsStateWithLifecycle()
    val channelsYouMayLike by viewModel.channelsYouMayLike.collectAsStateWithLifecycle()
    val globalPopularChannels by viewModel.globalPopularChannels.collectAsStateWithLifecycle()
    val trendingChannels by viewModel.trendingChannels.collectAsStateWithLifecycle()
    val favoriteChannels by viewModel.favoriteChannels.collectAsStateWithLifecycle()
    val slideshowItems by viewModel.homeSlideshow.collectAsStateWithLifecycle()
    val selectedExploreGroup by viewModel.selectedExploreGroup.collectAsStateWithLifecycle()

    var screenState by remember { mutableStateOf(ScreenState.MAIN) }
    var currentDestination by remember { mutableStateOf(NavDestination.HOME) }
    var playingChannel by remember { mutableStateOf<Channel?>(null) }
    var showUserInfoModal by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    // Request runtime location permissions on install/first launch
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.onLocationPermissionResult(isGranted)
    }

    // SAF Document Launchers for Portable Backup Export & Import
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    val json = viewModel.exportBackupJson()
                    context.contentResolver.openOutputStream(uri)?.use { os ->
                        os.write(json.toByteArray())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    val json = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        inputStream.bufferedReader().use { it.readText() }
                    }
                    if (!json.isNullOrBlank()) {
                        viewModel.importBackupJson(json)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!uiState.hasAskedLocationPermission && uiState.hasCompletedOnboarding) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    MobiStreamTheme(darkTheme = uiState.isDarkMode) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (screenState) {

                ScreenState.PLAYER -> {
                    val current = playingChannel
                    if (current != null) {
                        val related = allChannels.filter { it.category == current.category }
                        PlayerScreen(
                            channel = current,
                            relatedChannels = related,
                            onBack = {
                                // Returning from player retains exact channel list state in Explore
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

                ScreenState.MAIN, ScreenState.SETTINGS_OVERLAY -> {
                    BackHandler(enabled = screenState == ScreenState.SETTINGS_OVERLAY || currentDestination != NavDestination.HOME) {
                        if (screenState == ScreenState.SETTINGS_OVERLAY) {
                            screenState = ScreenState.MAIN
                        } else {
                            currentDestination = NavDestination.HOME
                        }
                    }

                    Scaffold(
                        topBar = {
                            // Persistent Top Bar across entire app (safely padded below notification bar)
                            MobiStreamUnifiedTopBar(
                                isDarkMode = uiState.isDarkMode,
                                onToggleTheme = { viewModel.toggleTheme(!uiState.isDarkMode) },
                                onSettingsClick = {
                                    screenState = if (screenState == ScreenState.SETTINGS_OVERLAY) {
                                        ScreenState.MAIN
                                    } else {
                                        ScreenState.SETTINGS_OVERLAY
                                    }
                                },
                                onFilterClick = { showFilterSheet = true },
                                hasActiveFilters = uiState.filterOptions.hasActiveFilters
                            )
                        },
                        bottomBar = {
                            // Bottom Menubar with Home, Explore, Favourite, Profile
                            MobiStreamBottomNav(
                                currentDestination = currentDestination,
                                onNavigate = { destination ->
                                    if (destination == NavDestination.PROFILE) {
                                        showUserInfoModal = true
                                    } else {
                                        if (screenState == ScreenState.SETTINGS_OVERLAY) {
                                            screenState = ScreenState.MAIN
                                        }
                                        currentDestination = destination
                                    }
                                }
                            )
                        },
                        containerColor = MaterialTheme.colorScheme.background
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            if (screenState == ScreenState.SETTINGS_OVERLAY) {
                                SettingsScreen(
                                    isDarkMode = uiState.isDarkMode,
                                    bufferMode = uiState.streamBufferMode,
                                    preferredLanguages = uiState.preferredLanguages,
                                    favoriteCategories = uiState.favoriteCategories,
                                    onToggleTheme = { isDark -> viewModel.toggleTheme(isDark) },
                                    onSetBufferMode = { mode -> viewModel.setStreamBufferMode(mode) },
                                    onSetPreferredLanguages = { viewModel.setPreferredLanguages(it) },
                                    onSetFavoriteCategories = { viewModel.setFavoriteCategories(it) },
                                    onExportBackupFile = { exportLauncher.launch("mobistream_backup.json") },
                                    onImportBackupFile = { importLauncher.launch(arrayOf("application/json", "text/*", "*/*")) },
                                    onClearCache = { viewModel.loadData(forceRefresh = true) }
                                )
                            } else {
                                AnimatedContent(
                                    targetState = currentDestination,
                                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                                    label = "navScreenTransition"
                                ) { destination ->
                                    when (destination) {
                                        NavDestination.HOME -> {
                                            HomeScreen(
                                                slides = slideshowItems,
                                                pinnedAndFavorites = pinnedAndFavorites,
                                                countryChannels = countryChannels,
                                                channelsYouMayLike = channelsYouMayLike,
                                                globalPopularChannels = globalPopularChannels,
                                                trendingChannels = trendingChannels,
                                                categories = uiState.availableCategories,
                                                detectedCountryName = uiState.detectedCountryName,
                                                detectedCountryCode = uiState.detectedCountryCode,
                                                filterOptions = uiState.filterOptions,
                                                isDarkMode = uiState.isDarkMode,
                                                isLoading = uiState.isLoading,
                                                onToggleTheme = { viewModel.toggleTheme(!uiState.isDarkMode) },
                                                onSelectChannel = { ch ->
                                                    viewModel.setLastWatchedChannel(ch.id)
                                                    playingChannel = ch
                                                    screenState = ScreenState.PLAYER
                                                },
                                                onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                                                onTogglePin = { id -> viewModel.togglePinned(id) },
                                                onSelectCategory = { cat ->
                                                    viewModel.selectExploreCategory(cat)
                                                },
                                                onApplyFilters = { filters ->
                                                    viewModel.setCategoryFilter(filters.selectedCategory)
                                                    viewModel.setCountryFilter(filters.selectedCountry)
                                                    viewModel.setLanguageFilter(filters.selectedLanguage)
                                                    viewModel.setQualityFilter(filters.selectedQuality)
                                                },
                                                onNavigateToExplore = {
                                                    currentDestination = NavDestination.EXPLORE
                                                },
                                                onNavigateToSettings = {
                                                    screenState = ScreenState.SETTINGS_OVERLAY
                                                }
                                            )
                                        }

                                        NavDestination.EXPLORE -> {
                                            ExploreScreen(
                                                allChannels = allChannels,
                                                countryChannels = countryChannels,
                                                categories = uiState.availableCategories,
                                                countries = uiState.availableCountries,
                                                languages = uiState.availableLanguages,
                                                detectedCountryName = uiState.detectedCountryName,
                                                detectedCountryCode = uiState.detectedCountryCode,
                                                filterOptions = uiState.filterOptions,
                                                isDarkMode = uiState.isDarkMode,
                                                selectedGroup = selectedExploreGroup,
                                                onSelectGroup = { group -> viewModel.selectExploreGroup(group) },
                                                sortCategoryChannels = viewModel::sortCategoryChannels,
                                                onSelectChannel = { ch ->
                                                    viewModel.setLastWatchedChannel(ch.id)
                                                    playingChannel = ch
                                                    screenState = ScreenState.PLAYER
                                                },
                                                onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                                                onSearchQueryChange = { q -> viewModel.updateSearchQuery(q) },
                                                onFetchLanguageChannels = viewModel::fetchLanguageChannels,
                                                lastWatchedChannelId = uiState.lastWatchedChannelId
                                            )
                                        }

                                        NavDestination.FAVOURITE -> {
                                            FavoritesScreen(
                                                favoriteChannels = favoriteChannels,
                                                onSelectChannel = { ch ->
                                                    viewModel.setLastWatchedChannel(ch.id)
                                                    playingChannel = ch
                                                    screenState = ScreenState.PLAYER
                                                },
                                                onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                                                onExploreChannels = {
                                                    currentDestination = NavDestination.EXPLORE
                                                }
                                            )
                                        }

                                        NavDestination.PROFILE -> {
                                            currentDestination = NavDestination.HOME
                                            showUserInfoModal = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Global Filter Bottom Sheet triggered by the top bar filter button
            if (showFilterSheet) {
                FilterBottomSheet(
                    filterOptions = uiState.filterOptions,
                    categories = uiState.availableCategories,
                    countries = uiState.availableCountries,
                    languages = uiState.availableLanguages,
                    onApplyFilters = { filters ->
                        viewModel.setCategoryFilter(filters.selectedCategory)
                        viewModel.setCountryFilter(filters.selectedCountry)
                        viewModel.setLanguageFilter(filters.selectedLanguage)
                        viewModel.setQualityFilter(filters.selectedQuality)
                    },
                    onDismiss = { showFilterSheet = false }
                )
            }

            // User Info Modal with UPI QR, GitHub, PayPal, and About app
            if (showUserInfoModal) {
                UserInfoModal(
                    onDismiss = { showUserInfoModal = false }
                )
            }

            // Onboarding Modal with Permissions, Backup check, Preferred Languages & Categories
            if (!uiState.hasCompletedOnboarding) {
                OnboardingModal(
                    onDismiss = { viewModel.completeOnboarding() },
                    onRequestPermissions = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    onImportFile = {
                        importLauncher.launch(arrayOf("application/json", "text/*", "*/*"))
                        viewModel.completeOnboarding()
                    },
                    onFinish = { langs, cats ->
                        viewModel.setPreferredLanguages(langs)
                        viewModel.setFavoriteCategories(cats)
                        viewModel.completeOnboarding()
                    },
                    isDark = uiState.isDarkMode
                )
            }
        }
    }
}
