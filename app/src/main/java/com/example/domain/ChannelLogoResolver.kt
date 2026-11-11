package com.example.domain

import java.util.Locale

object ChannelLogoResolver {

    /**
     * Resolves high quality, guaranteed CDN logos for channels.
     * Fixes broken GitHub/epg URLs or missing logos in iptv-org playlists.
     */
    fun resolve(channelName: String, rawLogoUrl: String): String {
        // If the URL is already valid and not from broken raw.githubusercontent.com/iptv-org/epg
        val trimmed = rawLogoUrl.trim()
        val isBrokenGithub = trimmed.contains("raw.githubusercontent.com/iptv-org/epg") ||
                trimmed.contains("raw.githubusercontent.com") && trimmed.contains("/logos/")

        val nameLower = channelName.lowercase(Locale.ROOT)

        // Known priority channel overrides
        val verifiedUrl = when {
            nameLower.contains("9x") || nameLower.contains("tashan") ->
                "https://xstreamcp-assets-msp.streamready.in/assets/LIVETV/LIVECHANNEL/LIVETV_LIVETVCHANNEL_9X_TASHAN/images/LOGO_HD/image.png"

            nameLower.contains("abp sanjha") || nameLower.contains("sanjha") ->
                "https://jiotvimages.cdn.jio.com/dare_images/images/ABP_Sanjha.png"

            nameLower.contains("aaj tak") ->
                "https://upload.wikimedia.org/wikipedia/commons/2/28/Aaj_tak_logo.png"

            nameLower.contains("ptc news") ->
                "https://xstreamcp-assets-msp.streamready.in/assets/LIVETV/LIVECHANNEL/LIVETV_LIVETVCHANNEL_PTC_NEWS/images/LOGO_HD/image.png"

            nameLower.contains("ptc punjabi") ->
                "https://xstreamcp-assets-msp.streamready.in/assets/LIVETV/LIVECHANNEL/LIVETV_LIVETVCHANNEL_PTC_PUNJABI/images/LOGO_HD/image.png"

            nameLower.contains("ptc chakde") ->
                "https://xstreamcp-assets-msp.streamready.in/assets/LIVETV/LIVECHANNEL/LIVETV_LIVETVCHANNEL_PTC_CHAKDE/images/LOGO_HD/image.png"

            nameLower.contains("ptc music") ->
                "https://xstreamcp-assets-msp.streamready.in/assets/LIVETV/LIVECHANNEL/LIVETV_LIVETVCHANNEL_PTC_MUSIC/images/LOGO_HD/image.png"

            nameLower.contains("chardikla") || nameLower.contains("time tv") ->
                "https://xstreamcp-assets-msp.streamready.in/assets/LIVETV/LIVECHANNEL/LIVETV_LIVETVCHANNEL_CHARDIKLA_TIME_TV/images/LOGO_HD/image.png"

            nameLower.contains("dd news") ->
                "https://ltsk-cdn.s3.eu-west-1.amazonaws.com/jumpstart/Temp_Live/cdn/HLS/Channel/transparentImages/DD%20News%20HD.png"

            nameLower.contains("dd punjabi") ->
                "https://dtil.tmsimg.com/assets/s142800_ld_h15_aa.png?lock=720x540"

            nameLower.contains("dd sports") ->
                "https://dtil.tmsimg.com/assets/s142797_ld_h15_aa.png?lock=720x540"

            nameLower.contains("ndtv") ->
                "https://dtil.tmsimg.com/assets/s154895_ld_h15_aa.png?lock=720x540"

            nameLower.contains("history") ->
                "https://dtil.tmsimg.com/assets/s143133_ld_h15_aa.png?lock=720x540"

            nameLower.contains("bloomberg") ->
                "https://upload.wikimedia.org/wikipedia/commons/thumb/5/56/Bloomberg_Television_logo.svg/512px-Bloomberg_Television_logo.svg.png"

            nameLower.contains("france 24") ->
                "https://upload.wikimedia.org/wikipedia/commons/thumb/8/82/France_24_logo.svg/512px-France_24_logo.svg.png"

            nameLower.contains("red bull") ->
                "https://i.imgur.com/gK1u9eO.png"

            nameLower.contains("sky news") ->
                "https://upload.wikimedia.org/wikipedia/en/thumb/e/e4/Sky_News_2018.svg/512px-Sky_News_2018.svg.png"

            nameLower.contains("espn") ->
                "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/ESPN_logo.svg/512px-ESPN_logo.svg.png"

            nameLower.contains("bbc") ->
                "https://upload.wikimedia.org/wikipedia/commons/thumb/4/41/BBC_Logo_2021.svg/512px-BBC_Logo_2021.svg.png"

            nameLower.contains("cnn") ->
                "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b1/CNN.svg/512px-CNN.svg.png"

            nameLower.contains("al jazeera") ->
                "https://upload.wikimedia.org/wikipedia/en/thumb/f/f2/Aljazeera_eng.svg/512px-Aljazeera_eng.svg.png"

            nameLower.contains("&tv") || nameLower.contains("andtv") ->
                "https://xstreamcp-assets-msp.streamready.in/assets/LIVETV/LIVECHANNEL/LIVETV_LIVETVCHANNEL_SYMANDTV/images/LOGO_HD/LOGO_HD_image.png"

            nameLower.contains("india today") ->
                "https://jiotvimages.cdn.jio.com/dare_images/images/India_Today.png"

            else -> null
        }

        if (verifiedUrl != null) {
            return verifiedUrl
        }

        if (isBrokenGithub || trimmed.isBlank()) {
            return ""
        }

        return trimmed
    }
}
