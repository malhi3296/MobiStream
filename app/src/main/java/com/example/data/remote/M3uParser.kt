package com.example.data.remote

import com.example.data.model.Channel
import java.util.UUID

object M3uParser {

    private val TVG_ID_REGEX = Regex("""tvg-id="([^"]*)"""")
    private val TVG_NAME_REGEX = Regex("""tvg-name="([^"]*)"""")
    private val TVG_LOGO_REGEX = Regex("""tvg-logo="([^"]*)"""")
    private val GROUP_TITLE_REGEX = Regex("""group-title="([^"]*)"""")
    private val TVG_COUNTRY_REGEX = Regex("""tvg-country="([^"]*)"""")
    private val TVG_LANGUAGE_REGEX = Regex("""tvg-language="([^"]*)"""")

    fun parse(m3uContent: String, defaultCategory: String = "General"): List<Channel> {
        val channels = mutableListOf<Channel>()
        val lines = m3uContent.lineSequence().map { it.trim() }.iterator()

        var currentExtInf: String? = null

        while (lines.hasNext()) {
            val line = lines.next()
            if (line.isEmpty()) continue

            if (line.startsWith("#EXTINF", ignoreCase = true)) {
                currentExtInf = line
            } else if (currentExtInf != null && !line.startsWith("#")) {
                val streamUrl = line
                if (streamUrl.startsWith("http://") || streamUrl.startsWith("https://")) {
                    val channel = parseChannelEntry(currentExtInf, streamUrl, defaultCategory)
                    if (channel != null) {
                        channels.add(channel)
                    }
                }
                currentExtInf = null
            }
        }
        return channels
    }

    private fun parseChannelEntry(extInf: String, streamUrl: String, defaultCategory: String): Channel? {
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
        val language = detectLanguage(tvgLanguage, countryCode)

        val id = tvgId?.takeIf { it.isNotBlank() } ?: "ch_${Math.abs(streamUrl.hashCode())}"

        return Channel(
            id = id,
            name = cleanName,
            logoUrl = logoUrl,
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
        val lower = group.lowercase()
        return when {
            lower.contains("sport") -> "Sports"
            lower.contains("news") -> "News"
            lower.contains("movie") || lower.contains("film") || lower.contains("cinema") -> "Movies"
            lower.contains("series") || lower.contains("drama") -> "Series"
            lower.contains("music") -> "Music"
            lower.contains("kid") || lower.contains("child") || lower.contains("animat") || lower.contains("cartoon") -> "Kids"
            lower.contains("doc") || lower.contains("nature") || lower.contains("science") -> "Documentary"
            lower.contains("entertain") || lower.contains("comedy") -> "Entertainment"
            else -> group.split(";").firstOrNull()?.trim()?.replaceFirstChar { it.uppercase() } ?: "General"
        }
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
        if (!tvgCountry.isNullOrBlank()) return tvgCountry.uppercase()
        if (tvgId != null && tvgId.contains(".")) {
            val parts = tvgId.split(".")
            if (parts.size >= 2) {
                val code = parts[1].substringBefore("@").uppercase()
                if (code.length in 2..3) return code
            }
        }
        val lowerName = name.lowercase()
        return when {
            lowerName.contains(" usa") || lowerName.contains(" us") -> "US"
            lowerName.contains(" uk") || lowerName.contains(" bbc") || lowerName.contains(" itv") -> "UK"
            lowerName.contains(" fr") || lowerName.contains(" france") -> "FR"
            lowerName.contains(" de") || lowerName.contains(" germany") -> "DE"
            lowerName.contains(" es") || lowerName.contains(" spain") -> "ES"
            lowerName.contains(" it") || lowerName.contains(" italy") -> "IT"
            lowerName.contains(" in") || lowerName.contains(" india") -> "IN"
            lowerName.contains(" ca") || lowerName.contains(" canada") -> "CA"
            else -> "INT"
        }
    }

    private fun countryCodeToName(code: String): String {
        return when (code) {
            "US" -> "United States"
            "UK", "GB" -> "United Kingdom"
            "FR" -> "France"
            "DE" -> "Germany"
            "ES" -> "Spain"
            "IT" -> "Italy"
            "CA" -> "Canada"
            "IN" -> "India"
            "BR" -> "Brazil"
            "AU" -> "Australia"
            "TR" -> "Turkey"
            "IL" -> "Israel"
            "EU" -> "European Union"
            "QA" -> "Qatar"
            else -> "International"
        }
    }

    private fun detectLanguage(tvgLang: String?, countryCode: String): String {
        if (!tvgLang.isNullOrBlank()) return tvgLang.replaceFirstChar { it.uppercase() }
        return when (countryCode) {
            "US", "UK", "GB", "CA", "AU" -> "English"
            "FR" -> "French"
            "DE" -> "German"
            "ES" -> "Spanish"
            "IT" -> "Italian"
            "BR" -> "Portuguese"
            "IN" -> "Hindi"
            "TR" -> "Turkish"
            "QA" -> "Arabic"
            else -> "English"
        }
    }
}
