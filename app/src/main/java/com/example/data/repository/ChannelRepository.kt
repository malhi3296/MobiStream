package com.example.data.repository

import android.content.Context
import android.telephony.TelephonyManager
import com.example.data.local.UserPreferencesManager
import com.example.data.local.dao.ChannelDao
import com.example.data.local.dao.FavoriteDao
import com.example.data.local.dao.PinnedDao
import com.example.data.local.entity.ChannelEntity
import com.example.data.local.entity.FavoriteChannelEntity
import com.example.data.local.entity.PinnedChannelEntity
import com.example.data.model.Category
import com.example.data.model.Channel
import com.example.data.model.Country
import com.example.data.model.Language
import com.example.data.remote.IptvApiService
import com.example.data.remote.M3uParser
import com.example.data.seed.SeedChannels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.Locale

class ChannelRepository(
    private val channelDao: ChannelDao,
    private val favoriteDao: FavoriteDao,
    private val pinnedDao: PinnedDao,
    private val apiService: IptvApiService,
    val preferences: UserPreferencesManager,
    private val context: Context
) {
    val favoriteIds: Flow<List<String>> = favoriteDao.getFavoriteIds()
    val pinnedIds: Flow<List<String>> = pinnedDao.getPinnedIds()

    val allChannels: Flow<List<Channel>> = combine(
        channelDao.getAllChannels(),
        favoriteDao.getFavoriteIds(),
        pinnedDao.getPinnedIds()
    ) { entities, favIds, pinIds ->
        val favSet = favIds.toSet()
        val pinSet = pinIds.toSet()
        entities.map {
            it.toDomain(
                isFavorite = favSet.contains(it.id),
                isPinned = pinSet.contains(it.id)
            )
        }
    }.flowOn(Dispatchers.IO)

    val favoriteChannels: Flow<List<Channel>> = combine(
        channelDao.getAllChannels(),
        favoriteDao.getFavoriteIds(),
        pinnedDao.getPinnedIds()
    ) { entities, favIds, pinIds ->
        val favSet = favIds.toSet()
        val pinSet = pinIds.toSet()
        entities.filter { favSet.contains(it.id) }
            .map {
                it.toDomain(
                    isFavorite = true,
                    isPinned = pinSet.contains(it.id)
                )
            }
    }.flowOn(Dispatchers.IO)

    // Pinned and Favourites combined, with Pinned channels strictly first!
    val pinnedAndFavorites: Flow<List<Channel>> = combine(
        channelDao.getAllChannels(),
        favoriteDao.getFavoriteIds(),
        pinnedDao.getPinnedIds()
    ) { entities, favIds, pinIds ->
        val favSet = favIds.toSet()
        val pinSet = pinIds.toSet()
        val channelMap = entities.associateBy { it.id }

        val pinnedList = pinIds.mapNotNull { channelMap[it] }
            .map { it.toDomain(isFavorite = favSet.contains(it.id), isPinned = true) }

        val pinnedIdSet = pinnedList.map { it.id }.toSet()

        val favList = favIds.filter { !pinnedIdSet.contains(it) }
            .mapNotNull { channelMap[it] }
            .map { it.toDomain(isFavorite = true, isPinned = false) }

        pinnedList + favList
    }.flowOn(Dispatchers.IO)

    suspend fun initializeChannels(forceRefresh: Boolean = false) = withContext(Dispatchers.IO) {
        val count = channelDao.getCount()
        if (count == 0 || forceRefresh) {
            if (count == 0) {
                val seedEntities = SeedChannels.curatedChannels.map { ChannelEntity.fromDomain(it) }
                channelDao.insertChannels(seedEntities)
            }
            fetchRemotePlaylists()
        }
    }

    private suspend fun fetchRemotePlaylists() = withContext(Dispatchers.IO) {
        val playlistEndpoints = listOf(
            "https://iptv-org.github.io/iptv/categories/sports.m3u" to "Sports",
            "https://iptv-org.github.io/iptv/categories/news.m3u" to "News",
            "https://iptv-org.github.io/iptv/categories/movies.m3u" to "Movies",
            "https://iptv-org.github.io/iptv/categories/music.m3u" to "Music",
            "https://iptv-org.github.io/iptv/categories/animation.m3u" to "Kids",
            "https://iptv-org.github.io/iptv/categories/entertainment.m3u" to "Entertainment"
        )

        val fetchedChannels = mutableListOf<Channel>()

        for ((url, cat) in playlistEndpoints) {
            try {
                val response = apiService.fetchPlaylist(url)
                val m3uString = response.string()
                val parsed = M3uParser.parse(m3uString, defaultCategory = cat)
                fetchedChannels.addAll(parsed.take(40))
            } catch (e: Exception) {
                // Ignore single playlist failure and continue
            }
        }

        if (fetchedChannels.isNotEmpty()) {
            val entities = fetchedChannels.map { ChannelEntity.fromDomain(it) }
            channelDao.insertChannels(entities)
        }
    }

    suspend fun toggleFavorite(channelId: String) = withContext(Dispatchers.IO) {
        val isFav = favoriteDao.isFavorite(channelId)
        if (isFav) {
            favoriteDao.deleteFavorite(channelId)
        } else {
            favoriteDao.insertFavorite(FavoriteChannelEntity(channelId = channelId))
        }
    }

    suspend fun togglePinned(channelId: String) = withContext(Dispatchers.IO) {
        val isPin = pinnedDao.isPinned(channelId)
        if (isPin) {
            pinnedDao.deletePinned(channelId)
        } else {
            pinnedDao.insertPinned(PinnedChannelEntity(channelId = channelId))
        }
    }

    fun detectDeviceCountry(): Pair<String, String> {
        // Check saved country preference first
        val savedCode = preferences.detectedCountryCode
        val savedName = preferences.detectedCountryName
        if (savedCode.isNotBlank() && savedName.isNotBlank()) {
            return savedCode to savedName
        }

        // Try TelephonyManager (SIM/Network country)
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        val simCountry = tm?.simCountryIso?.uppercase(Locale.US)
        val networkCountry = tm?.networkCountryIso?.uppercase(Locale.US)

        val countryCode = when {
            !simCountry.isNullOrBlank() && simCountry.length == 2 -> simCountry
            !networkCountry.isNullOrBlank() && networkCountry.length == 2 -> networkCountry
            else -> Locale.getDefault().country.uppercase(Locale.US)
        }.takeIf { it.length == 2 } ?: "US"

        val countryName = Locale("", countryCode).displayCountry.takeIf { it.isNotBlank() } ?: "United States"

        preferences.detectedCountryCode = countryCode
        preferences.detectedCountryName = countryName

        return countryCode to countryName
    }

    suspend fun getAvailableCategories(): List<Category> = withContext(Dispatchers.IO) {
        val channels = channelDao.getAllChannels().first()
        val grouped = channels.groupBy { it.category }
        val predefinedOrder = listOf("Sports", "News", "Movies", "Series", "Music", "Kids", "Documentary", "Entertainment")

        val result = mutableListOf<Category>()
        for (cat in predefinedOrder) {
            val count = grouped[cat]?.size ?: 0
            if (count > 0) {
                result.add(Category(id = cat.lowercase(), name = cat, channelCount = count))
            }
        }
        for ((cat, list) in grouped) {
            if (cat !in predefinedOrder && list.isNotEmpty()) {
                result.add(Category(id = cat.lowercase(), name = cat, channelCount = list.size))
            }
        }
        result
    }

    suspend fun getAvailableCountries(): List<Country> = withContext(Dispatchers.IO) {
        val channels = channelDao.getAllChannels().first()
        channels.groupBy { it.countryCode to it.country }
            .map { (key, _) ->
                Country(
                    code = key.first,
                    name = key.second,
                    flag = getCountryEmoji(key.first)
                )
            }
            .sortedBy { it.name }
    }

    suspend fun getAvailableLanguages(): List<Language> = withContext(Dispatchers.IO) {
        val channels = channelDao.getAllChannels().first()
        channels.map { it.language }
            .distinct()
            .filter { it.isNotBlank() }
            .sorted()
            .map { Language(code = it.lowercase().take(2), name = it) }
    }

    private fun getCountryEmoji(countryCode: String): String {
        if (countryCode.length != 2) return "🌐"
        val firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
        val secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6
        return if (firstLetter in 0x1F1E6..0x1F1FF && secondLetter in 0x1F1E6..0x1F1FF) {
            String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
        } else {
            "🌐"
        }
    }
}
