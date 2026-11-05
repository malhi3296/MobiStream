package com.example

import com.example.data.remote.M3uParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class M3uParserTest {

    @Test
    fun `parse valid m3u content extracts channel details`() {
        val sampleM3u = """
            #EXTM3U
            #EXTINF:-1 tvg-id="SkyNews.uk@SD" tvg-logo="https://example.com/logo.png" group-title="News",Sky News (1080p)
            https://example.com/stream.m3u8
            #EXTINF:-1 tvg-id="ESPN.us@SD" tvg-logo="https://example.com/espn.png" group-title="Sports",ESPN HD (720p)
            https://example.com/espn.m3u8
        """.trimIndent()

        val channels = M3uParser.parse(sampleM3u)
        assertEquals(2, channels.size)

        val channel1 = channels[0]
        assertEquals("Sky News", channel1.name)
        assertEquals("News", channel1.category)
        assertEquals("UK", channel1.countryCode)
        assertEquals("United Kingdom", channel1.country)
        assertEquals("FHD", channel1.quality)
        assertEquals("https://example.com/stream.m3u8", channel1.streamUrl)

        val channel2 = channels[1]
        assertEquals("ESPN", channel2.name)
        assertEquals("Sports", channel2.category)
        assertEquals("US", channel2.countryCode)
        assertEquals("United States", channel2.country)
        assertEquals("HD", channel2.quality)
    }
}
