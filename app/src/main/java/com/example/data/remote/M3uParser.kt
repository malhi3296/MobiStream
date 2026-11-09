package com.example.data.remote

import com.example.data.model.Channel
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Locale

object M3uParser {

    private val TVG_ID_REGEX = Regex("""tvg-id="([^"]*)"""")
    private val TVG_NAME_REGEX = Regex("""tvg-name="([^"]*)"""")
    private val TVG_LOGO_REGEX = Regex("""tvg-logo="([^"]*)"""")
    private val GROUP_TITLE_REGEX = Regex("""group-title="([^"]*)"""")
    private val TVG_COUNTRY_REGEX = Regex("""tvg-country="([^"]*)"""")
    private val TVG_LANGUAGE_REGEX = Regex("""tvg-language="([^"]*)"""")

    /**
     * Efficiently streams through M3U playlists without buffering the full 11k+ channels in memory.
     * Emits batches directly into Room DB for lightning-fast loading.
     */
    suspend fun parseStream(
        inputStream: InputStream,
        batchSize: Int = 500,
        onBatch: suspend (List<Channel>) -> Unit
    ) {
        val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8), 32768)
        val currentBatch = mutableListOf<Channel>()
        var currentExtInf: String? = null

        var line: String?
        while (reader.readLine().also { line = it } != null) {
            val trimmed = line!!.trim()
            if (trimmed.isEmpty()) continue

            if (trimmed.startsWith("#EXTINF", ignoreCase = true)) {
                currentExtInf = trimmed
            } else if (trimmed.startsWith("#")) {
                // Keep currentExtInf across options like #EXTVLCOPT
                continue
            } else if (currentExtInf != null) {
                if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                    val channel = parseChannelEntry(currentExtInf, trimmed)
                    if (channel != null) {
                        currentBatch.add(channel)
                        if (currentBatch.size >= batchSize) {
                            onBatch(currentBatch.toList())
                            currentBatch.clear()
                        }
                    }
                }
                currentExtInf = null
            }
        }

        if (currentBatch.isNotEmpty()) {
            onBatch(currentBatch.toList())
            currentBatch.clear()
        }
    }

    fun parse(m3uContent: String, defaultCategory: String = "General"): List<Channel> {
        val channels = mutableListOf<Channel>()
        val lines = m3uContent.lineSequence().map { it.trim() }.iterator()

        var currentExtInf: String? = null

        while (lines.hasNext()) {
            val line = lines.next()
            if (line.isEmpty()) continue

            if (line.startsWith("#EXTINF", ignoreCase = true)) {
                currentExtInf = line
            } else if (line.startsWith("#")) {
                continue
            } else if (currentExtInf != null) {
                if (line.startsWith("http://") || line.startsWith("https://")) {
                    val channel = parseChannelEntry(currentExtInf, line, defaultCategory)
                    if (channel != null) {
                        channels.add(channel)
                    }
                }
                currentExtInf = null
            }
        }
        return channels
    }

    fun parseChannelEntry(extInf: String, streamUrl: String, defaultCategory: String = "General"): Channel? {
        val commaIndex = extInf.lastIndexOf(',')
        val rawTitle = if (commaIndex != -1 && commaIndex < extInf.length - 1) {
            extInf.substring(commaIndex + 1).trim()
        } else {
            TVG_NAME_REGEX.find(extInf)?.groupValues?.get(1)?.trim() ?: "Live Stream"
        }

        if (rawTitle.isBlank()) return null

        val tvgId = TVG_ID_REGEX.find(extInf)?.groupValues?.get(1)?.trim()
        val logoUrl = TVG_LOGO_REGEX.find(extInf)?.groupValues?.get(1)?.trim() ?: ""
        val groupTitle = GROUP_TITLE_REGEX.find(extInf)?.groupValues?.get(1)?.trim()
        val tvgCountry = TVG_COUNTRY_REGEX.find(extInf)?.groupValues?.get(1)?.trim()
        val tvgLanguage = TVG_LANGUAGE_REGEX.find(extInf)?.groupValues?.get(1)?.trim()

        val category = when {
            !groupTitle.isNullOrBlank() -> normalizeCategory(groupTitle)
            defaultCategory.isNotBlank() -> defaultCategory
            else -> "General"
        }

        val quality = detectQuality(rawTitle, streamUrl)
        val cleanName = cleanChannelName(rawTitle)
        val countryCode = detectCountryCode(tvgId, tvgCountry, cleanName)
        val countryName = countryCodeToName(countryCode)
        val language = detectLanguage(tvgLanguage, countryCode, cleanName, tvgId)
        val finalLogo = resolveFallbackLogo(cleanName, tvgId, logoUrl)

        val id = if (!tvgId.isNullOrBlank()) {
            tvgId
        } else {
            "ch_${Math.abs(streamUrl.hashCode())}"
        }

        return Channel(
            id = id,
            name = cleanName,
            logoUrl = finalLogo,
            streamUrl = streamUrl,
            category = category,
            country = countryName,
            countryCode = countryCode,
            language = language,
            quality = quality,
            isLive = true,
            isFeatured = false
        )
    }

    private fun normalizeCategory(group: String): String {
        val parts = group.split(';').map { it.trim() }.filter { it.isNotBlank() }
        for (part in parts) {
            val lower = part.lowercase(Locale.ROOT)
            when {
                lower.contains("sport") -> return "Sports"
                lower.contains("news") -> return "News"
                lower.contains("movie") || lower.contains("film") || lower.contains("cinema") -> return "Movies"
                lower.contains("series") || lower.contains("drama") -> return "Series"
                lower.contains("music") -> return "Music"
                lower.contains("animat") || lower.contains("cartoon") -> return "Animation"
                lower.contains("kid") || lower.contains("child") -> return "Kids"
                lower.contains("doc") || lower.contains("nature") -> return "Documentary"
                lower.contains("science") -> return "Science"
                lower.contains("comedy") -> return "Comedy"
                lower.contains("cook") || lower.contains("food") -> return "Cooking"
                lower.contains("auto") || lower.contains("motor") -> return "Auto"
                lower.contains("business") || lower.contains("finance") -> return "Business"
                lower.contains("classic") -> return "Classic"
                lower.contains("culture") -> return "Culture"
                lower.contains("edu") -> return "Education"
                lower.contains("fam") -> return "Family"
                lower.contains("relig") -> return "Religious"
                lower.contains("travel") -> return "Travel"
                lower.contains("weather") -> return "Weather"
                lower.contains("shop") -> return "Shop"
                lower.contains("outdoor") -> return "Outdoor"
                lower.contains("lifestyle") -> return "Lifestyle"
                lower.contains("entertain") -> return "Entertainment"
                lower.contains("legislat") || lower.contains("parliament") -> return "Legislative"
                lower.contains("relax") -> return "Relax"
            }
        }
        val firstPart = parts.firstOrNull() ?: "General"
        if (firstPart.equals("Undefined", ignoreCase = true) || firstPart.isBlank()) {
            return "General"
        }
        return firstPart.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }

    private fun cleanChannelName(name: String): String {
        return name
            .replace(Regex("""\(\d+p\)""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\b(FHD|UHD|HD|SD|4K|1080p|720p)\b""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\[.*?\]"""), "")
            .trim()
            .takeIf { it.isNotBlank() } ?: name
    }

    private fun detectQuality(title: String, url: String): String {
        val combined = "$title $url".lowercase()
        return when {
            combined.contains("4k") || combined.contains("2160") -> "4K"
            combined.contains("1080") || combined.contains("fhd") -> "FHD"
            combined.contains("720") || combined.contains("hd") -> "HD"
            else -> "HD"
        }
    }

    private fun detectCountryCode(tvgId: String?, tvgCountry: String?, name: String): String {
        if (!tvgCountry.isNullOrBlank() && tvgCountry.length in 2..3) {
            return tvgCountry.uppercase(Locale.ROOT)
        }
        if (tvgId != null && tvgId.contains(".")) {
            val code = tvgId.substringAfter('.').substringBefore('@').uppercase(Locale.ROOT)
            if (code.length in 2..3 && code.all { it.isLetter() }) {
                return code
            }
        }
        val lowerName = name.lowercase(Locale.ROOT)
        return when {
            lowerName.contains(" usa") || lowerName.contains(" us ") || lowerName.endsWith(" us") -> "US"
            lowerName.contains(" uk ") || lowerName.contains(" bbc") || lowerName.contains(" itv") || lowerName.endsWith(" uk") -> "UK"
            lowerName.contains(" fr ") || lowerName.contains(" france") -> "FR"
            lowerName.contains(" de ") || lowerName.contains(" germany") -> "DE"
            lowerName.contains(" es ") || lowerName.contains(" spain") -> "ES"
            lowerName.contains(" it ") || lowerName.contains(" italy") -> "IT"
            lowerName.contains(" in ") || lowerName.contains(" india") || lowerName.contains("zee") -> "IN"
            lowerName.contains(" ca ") || lowerName.contains(" canada") -> "CA"
            lowerName.contains(" au ") || lowerName.contains(" australia") -> "AU"
            else -> "INT"
        }
    }

    private fun countryCodeToName(code: String): String {
        if (code == "INT") return "International"
        if (code == "UK") return "United Kingdom"
        val display = Locale("", code).displayCountry
        return if (!display.isNullOrBlank()) display else when (code) {
            "US" -> "United States"
            "GB" -> "United Kingdom"
            "FR" -> "France"
            "DE" -> "Germany"
            "ES" -> "Spain"
            "IT" -> "Italy"
            "CA" -> "Canada"
            "IN" -> "India"
            "BR" -> "Brazil"
            "AU" -> "Australia"
            "TR" -> "Turkey"
            "RU" -> "Russia"
            "UA" -> "Ukraine"
            "GE" -> "Georgia"
            "ZA" -> "South Africa"
            "NL" -> "Netherlands"
            else -> code
        }
    }

    private fun detectLanguage(tvgLang: String?, countryCode: String, name: String, tvgId: String?): String {
        val lower = "$name $tvgId".lowercase(Locale.ROOT)
        if (lower.contains("punjabi") || lower.contains("ptc") || lower.contains("mh1") || lower.contains("chardikla") || lower.contains(".pan@")) {
            return "Punjabi"
        }
        if (lower.contains("tamil") || lower.contains(".tam@")) return "Tamil"
        if (lower.contains("telugu") || lower.contains(".tel@")) return "Telugu"
        if (lower.contains("bengali") || lower.contains("bangla") || lower.contains(".ben@")) return "Bengali"
        if (lower.contains("urdu") || lower.contains(".urd@")) return "Urdu"
        if (lower.contains("malayalam") || lower.contains(".mal@")) return "Malayalam"
        if (lower.contains("kannada") || lower.contains(".kan@")) return "Kannada"
        if (lower.contains("marathi") || lower.contains(".mar@")) return "Marathi"
        if (lower.contains("gujarati") || lower.contains(".guj@")) return "Gujarati"

        if (!tvgLang.isNullOrBlank()) {
            val langParts = tvgLang.split(';', ',').map { it.trim() }
            val first = langParts.firstOrNull() ?: ""
            if (first.equals("pan", ignoreCase = true) || first.equals("pa", ignoreCase = true)) return "Punjabi"
            if (first.equals("hin", ignoreCase = true) || first.equals("hi", ignoreCase = true)) return "Hindi"
            if (first.equals("urd", ignoreCase = true) || first.equals("ur", ignoreCase = true)) return "Urdu"
            if (first.equals("tam", ignoreCase = true) || first.equals("ta", ignoreCase = true)) return "Tamil"
            if (first.equals("tel", ignoreCase = true) || first.equals("te", ignoreCase = true)) return "Telugu"
            if (first.equals("ben", ignoreCase = true) || first.equals("bn", ignoreCase = true)) return "Bengali"
            if (first.equals("mal", ignoreCase = true) || first.equals("ml", ignoreCase = true)) return "Malayalam"
            if (first.equals("kan", ignoreCase = true) || first.equals("kn", ignoreCase = true)) return "Kannada"
            if (first.equals("mar", ignoreCase = true) || first.equals("mr", ignoreCase = true)) return "Marathi"
            if (first.equals("guj", ignoreCase = true) || first.equals("gu", ignoreCase = true)) return "Gujarati"
            if (first.length in 2..3) {
                val display = Locale(first).displayLanguage
                if (display.isNotBlank() && !display.equals(first, ignoreCase = true)) {
                    return display.replaceFirstChar { it.uppercase() }
                }
            }
            return first.replaceFirstChar { it.uppercase() }
        }
        return when (countryCode) {
            "US", "UK", "GB", "CA", "AU", "NZ", "IE" -> "English"
            "FR" -> "French"
            "DE", "AT", "CH" -> "German"
            "ES", "MX", "AR", "CO", "CL", "PE" -> "Spanish"
            "IT" -> "Italian"
            "BR", "PT" -> "Portuguese"
            "IN" -> "Hindi"
            "PK" -> "Urdu"
            "TR" -> "Turkish"
            "RU" -> "Russian"
            "UA" -> "Ukrainian"
            "GE" -> "Georgian"
            "NL" -> "Dutch"
            "JP" -> "Japanese"
            "KR" -> "Korean"
            "CN", "TW", "HK" -> "Chinese"
            "SA", "AE", "QA", "EG" -> "Arabic"
            else -> "English"
        }
    }

    private fun resolveFallbackLogo(name: String, tvgId: String?, existingLogo: String): String {
        if (existingLogo.isNotBlank()) return existingLogo
        val n = name.lowercase(Locale.ROOT)
        val id = (tvgId ?: "").lowercase(Locale.ROOT)
        return when {
            n.contains("ptc news") || id.contains("ptcnews") -> "https://upload.wikimedia.org/wikipedia/commons/e/ee/PTC_News_Logo.png"
            n.contains("ptc punjabi") || id.contains("ptcpunjabi") -> "https://upload.wikimedia.org/wikipedia/commons/9/90/PTC_Punjabi_Logo.png"
            n.contains("aaj tak") || id.contains("aajtak") -> "https://upload.wikimedia.org/wikipedia/commons/2/28/Aaj_tak_logo.png"
            n.contains("ndtv") -> "https://upload.wikimedia.org/wikipedia/commons/7/77/NDTV_logo.svg"
            n.contains("dd news") -> "https://upload.wikimedia.org/wikipedia/commons/3/36/DD_News_Logo.png"
            n.contains("history") -> "https://upload.wikimedia.org/wikipedia/commons/f/f5/History_Logo.svg"
            n.contains("espn") -> "https://upload.wikimedia.org/wikipedia/commons/a/a2/ESPN_logo.svg"
            n.contains("bbc") -> "https://upload.wikimedia.org/wikipedia/commons/4/41/BBC_Logo_2021.svg"
            n.contains("cnn") -> "https://upload.wikimedia.org/wikipedia/commons/b/b1/CNN.svg"
            n.contains("red bull") -> "https://upload.wikimedia.org/wikipedia/en/thumb/f/f5/Red_Bull_TV_logo.svg/300px-Red_Bull_TV_logo.svg.png"
            n.contains("9x tashan") || n.contains("tashan") -> "https://upload.wikimedia.org/wikipedia/en/3/30/9X_Tashan_Logo.jpg"
            n.contains("france 24") -> "https://upload.wikimedia.org/wikipedia/en/thumb/6/65/France_24_logo.svg/300px-France_24_logo.svg.png"
            n.contains("dw") || n.contains("deutsche welle") -> "https://upload.wikimedia.org/wikipedia/commons/thumb/7/75/Deutsche_Welle_logo.svg/300px-Deutsche_Welle_logo.svg.png"
            n.contains("al jazeera") -> "https://upload.wikimedia.org/wikipedia/en/thumb/f/f2/Al_Jazeera_English_logo.svg/300px-Al_Jazeera_English_logo.svg.png"
            n.contains("bloomberg") -> "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/Bloomberg_Television_logo.svg/300px-Bloomberg_Television_logo.svg.png"
            else -> ""
        }
    }
}
