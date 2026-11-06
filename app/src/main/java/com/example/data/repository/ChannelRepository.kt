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
        if (count == 0) {
            val seedEntities = SeedChannels.curatedChannels.map { ChannelEntity.fromDomain(it) }
            channelDao.insertChannels(seedEntities)
        }
        if (count < 3000 || forceRefresh) {
            fetchRemotePlaylists()
        }
    }

    private suspend fun fetchRemotePlaylists() = withContext(Dispatchers.IO) {
        // First fetch top priority Indian & International language playlists from PLAYLISTS.md
        val priorityLanguagePlaylists = listOf(
            "pan" to "Punjabi",
            "hin" to "Hindi",
            "eng" to "English",
            "urd" to "Urdu",
            "tam" to "Tamil",
            "tel" to "Telugu",
            "ben" to "Bengali",
            "mal" to "Malayalam"
        )

        for ((code, langName) in priorityLanguagePlaylists) {
            try {
                val url = "https://iptv-org.github.io/iptv/languages/$code.m3u"
                val response = apiService.fetchPlaylist(url)
                val m3uString = response.string()
                val parsed = M3uParser.parse(m3uString, defaultCategory = "General")
                if (parsed.isNotEmpty()) {
                    val entities = parsed.map {
                        ChannelEntity.fromDomain(it.copy(language = langName))
                    }
                    channelDao.insertChannels(entities)
                }
            } catch (e: Exception) {
                // Continue
            }
        }

        // Fetch master or category playlists
        val masterPlaylistUrl = "https://iptv-org.github.io/iptv/index.m3u"
        try {
            val response = apiService.fetchPlaylist(masterPlaylistUrl)
            response.byteStream().use { stream ->
                M3uParser.parseStream(stream, batchSize = 500) { batch ->
                    val entities = batch.map { ChannelEntity.fromDomain(it) }
                    channelDao.insertChannels(entities)
                }
            }
        } catch (e: Exception) {
            fetchCategoryPlaylistsFallback()
        }
    }

    private suspend fun fetchCategoryPlaylistsFallback() = withContext(Dispatchers.IO) {
        val playlistEndpoints = listOf(
            "https://iptv-org.github.io/iptv/categories/sports.m3u" to "Sports",
            "https://iptv-org.github.io/iptv/categories/news.m3u" to "News",
            "https://iptv-org.github.io/iptv/categories/movies.m3u" to "Movies",
            "https://iptv-org.github.io/iptv/categories/music.m3u" to "Music",
            "https://iptv-org.github.io/iptv/categories/animation.m3u" to "Animation",
            "https://iptv-org.github.io/iptv/categories/entertainment.m3u" to "Entertainment",
            "https://iptv-org.github.io/iptv/categories/comedy.m3u" to "Comedy",
            "https://iptv-org.github.io/iptv/categories/documentary.m3u" to "Documentary"
        )

        for ((url, cat) in playlistEndpoints) {
            try {
                val response = apiService.fetchPlaylist(url)
                val m3uString = response.string()
                val parsed = M3uParser.parse(m3uString, defaultCategory = cat)
                if (parsed.isNotEmpty()) {
                    val entities = parsed.map { ChannelEntity.fromDomain(it) }
                    channelDao.insertChannels(entities)
                }
            } catch (e: Exception) {
                // Continue with next
            }
        }
    }

    /**
     * Requirement 4: On-demand language playlist fetch directly from PLAYLISTS.md
     * Ensures selecting any language in Explore never displays "no channels found".
     */
    suspend fun fetchLanguagePlaylist(languageNameOrCode: String): Int = withContext(Dispatchers.IO) {
        val (code, canonicalName) = resolveLanguageInfo(languageNameOrCode)
        val url = "https://iptv-org.github.io/iptv/languages/$code.m3u"

        try {
            val response = apiService.fetchPlaylist(url)
            val m3uString = response.string()
            val parsed = M3uParser.parse(m3uString, defaultCategory = "General")
            if (parsed.isNotEmpty()) {
                val entities = parsed.map {
                    ChannelEntity.fromDomain(it.copy(language = canonicalName))
                }
                channelDao.insertChannels(entities)
                return@withContext entities.size
            }
        } catch (e: Exception) {
            // Ignore error
        }
        return@withContext 0
    }

    private fun resolveLanguageInfo(input: String): Pair<String, String> {
        val lower = input.trim().lowercase(Locale.ROOT)
        return when {
            lower in listOf("punjabi", "panjabi", "pan") -> "pan" to "Punjabi"
            lower in listOf("hindi", "hin") -> "hin" to "Hindi"
            lower in listOf("english", "eng") -> "eng" to "English"
            lower in listOf("urdu", "urd") -> "urd" to "Urdu"
            lower in listOf("tamil", "tam") -> "tam" to "Tamil"
            lower in listOf("telugu", "tel") -> "tel" to "Telugu"
            lower in listOf("bengali", "bangla", "ben") -> "ben" to "Bengali"
            lower in listOf("malayalam", "mal") -> "mal" to "Malayalam"
            lower in listOf("kannada", "kan") -> "kan" to "Kannada"
            lower in listOf("marathi", "mar") -> "mar" to "Marathi"
            lower in listOf("gujarati", "guj") -> "guj" to "Gujarati"
            lower in listOf("spanish", "spa") -> "spa" to "Spanish"
            lower in listOf("french", "fra") -> "fra" to "French"
            lower in listOf("german", "deu") -> "deu" to "German"
            lower in listOf("italian", "ita") -> "ita" to "Italian"
            lower in listOf("russian", "rus") -> "rus" to "Russian"
            lower in listOf("arabic", "ara") -> "ara" to "Arabic"
            lower in listOf("portuguese", "por") -> "por" to "Portuguese"
            lower in listOf("turkish", "tur") -> "tur" to "Turkish"
            lower in listOf("chinese", "zho") -> "zho" to "Chinese"
            lower in listOf("japanese", "jpn") -> "jpn" to "Japanese"
            lower in listOf("korean", "kor") -> "kor" to "Korean"
            lower in listOf("dutch", "nld") -> "nld" to "Dutch"
            lower in listOf("polish", "pol") -> "pol" to "Polish"
            lower in listOf("ukrainian", "ukr") -> "ukr" to "Ukrainian"
            lower in listOf("vietnamese", "vie") -> "vie" to "Vietnamese"
            lower in listOf("indonesian", "ind") -> "ind" to "Indonesian"
            lower in listOf("thai", "tha") -> "tha" to "Thai"
            lower in listOf("swedish", "swe") -> "swe" to "Swedish"
            lower in listOf("danish", "dan") -> "dan" to "Danish"
            lower in listOf("norwegian", "nor") -> "nor" to "Norwegian"
            lower in listOf("greek", "ell") -> "ell" to "Greek"
            lower in listOf("hungarian", "hun") -> "hun" to "Hungarian"
            lower in listOf("czech", "ces") -> "ces" to "Czech"
            lower in listOf("romanian", "ron") -> "ron" to "Romanian"
            lower in listOf("hebrew", "heb") -> "heb" to "Hebrew"
            lower in listOf("persian", "fas") -> "fas" to "Persian"
            else -> lower.take(3) to input.replaceFirstChar { it.uppercase() }
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
        val savedCode = preferences.detectedCountryCode
        val savedName = preferences.detectedCountryName
        if (savedCode.isNotBlank() && savedName.isNotBlank()) {
            return savedCode to savedName
        }

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
        val predefinedOrder = listOf(
            "Sports", "News", "Movies", "Series", "Music", "Animation",
            "Kids", "Documentary", "Comedy", "Entertainment", "Education",
            "Auto", "Business", "Lifestyle", "Science", "Travel"
        )

        val result = mutableListOf<Category>()
        for (cat in predefinedOrder) {
            val count = grouped[cat]?.size ?: 0
            if (count > 0) {
                result.add(Category(id = cat.lowercase(), name = cat, channelCount = count))
            }
        }
        for ((cat, list) in grouped) {
            if (cat !in predefinedOrder && list.isNotEmpty() && cat != "Undefined") {
                result.add(Category(id = cat.lowercase(), name = cat, channelCount = list.size))
            }
        }
        result
    }

    suspend fun getAvailableCountries(): List<Country> = withContext(Dispatchers.IO) {
        val channels = channelDao.getAllChannels().first()
        channels.groupBy { it.countryCode to it.country }
            .map { (key, list) ->
                Country(
                    code = key.first,
                    name = key.second,
                    flag = getCountryEmoji(key.first),
                    channelCount = list.size
                )
            }
            .filter { it.name.isNotBlank() && it.code != "INT" }
            .sortedBy { it.name }
    }

    suspend fun getAvailableLanguages(): List<Language> = withContext(Dispatchers.IO) {
        val channels = channelDao.getAllChannels().first()
        val indianPriority = listOf(
            "Punjabi" to "pan",
            "Hindi" to "hin",
            "English" to "eng",
            "Urdu" to "urd",
            "Tamil" to "tam",
            "Telugu" to "tel",
            "Bengali" to "ben",
            "Malayalam" to "mal",
            "Kannada" to "kan",
            "Marathi" to "mar",
            "Gujarati" to "guj"
        )

        val channelLanguageCount = channels.groupBy { it.language.lowercase(Locale.ROOT) }
            .mapValues { it.value.size }

        val result = mutableListOf<Language>()

        // 1. Indian priority languages at top (Punjabi strictly first)
        for ((lang, code) in indianPriority) {
            val dbCount = (channelLanguageCount[lang.lowercase()] ?: 0) +
                    (channelLanguageCount[code] ?: 0) +
                    (if (lang == "Punjabi") (channelLanguageCount["panjabi"] ?: 0) else 0)
            result.add(
                Language(
                    code = code,
                    name = lang,
                    channelCount = dbCount
                )
            )
        }

        // 2. All other canonical languages from PLAYLISTS.md
        val otherCanonical = listOf(
            "Arabic" to "ara", "Bulgarian" to "bul", "Catalan" to "cat", "Chinese" to "zho",
            "Croatian" to "hrv", "Czech" to "ces", "Danish" to "dan", "Dutch" to "nld",
            "French" to "fra", "German" to "deu", "Greek" to "ell", "Hebrew" to "heb",
            "Hungarian" to "hun", "Indonesian" to "ind", "Italian" to "ita", "Japanese" to "jpn",
            "Korean" to "kor", "Norwegian" to "nor", "Persian" to "fas", "Polish" to "pol",
            "Portuguese" to "por", "Romanian" to "ron", "Russian" to "rus", "Spanish" to "spa",
            "Swedish" to "swe", "Thai" to "tha", "Turkish" to "tur", "Ukrainian" to "ukr",
            "Vietnamese" to "vie"
        )

        val others = otherCanonical.map { (lang, code) ->
            val dbCount = (channelLanguageCount[lang.lowercase()] ?: 0) + (channelLanguageCount[code] ?: 0)
            Language(
                code = code,
                name = lang,
                channelCount = dbCount
            )
        }.sortedBy { it.name }

        result + others
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
