package com.example

import com.example.data.model.Channel
import com.example.data.remote.M3uParser
import com.example.domain.EpgProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testEpgProviderGeneratesProgramData() {
        val testChannel = Channel(
            id = "espn_sports",
            name = "ESPN Live",
            logoUrl = "https://example.com/logo.png",
            streamUrl = "https://example.com/stream.m3u8",
            category = "Sports",
            country = "United States",
            countryCode = "US",
            language = "English",
            quality = "FHD",
            isLive = true,
            isFeatured = true
        )

        val epg = EpgProvider.getEpgForChannel(testChannel)
        assertNotNull(epg)
        assertTrue(epg.currentTitle.isNotBlank())
        assertTrue(epg.timeSlot.contains(" - "))
        assertTrue(epg.elapsedPercent in 0f..1f)
        assertTrue(epg.nextTitle.isNotBlank())
    }

    @Test
    fun testDetectLanguageRecognizesPunjabiAndIndian() {
        val testLine = "#EXTINF:-1 tvg-id=\"PTCNews.in\" group-title=\"News\",PTC News Live"
        val m3uContent = "$testLine\nhttps://example.com/stream.m3u8"
        val parsed = M3uParser.parse(m3uContent, defaultCategory = "News")

        assertEquals(1, parsed.size)
        assertEquals("Punjabi", parsed[0].language)
        assertEquals("India", parsed[0].country)
        assertEquals("IN", parsed[0].countryCode)
    }
}
