package com.example.domain

import com.example.data.model.Channel
import java.util.Calendar
import java.util.Locale

data class EpgProgram(
    val currentTitle: String,
    val currentDescription: String,
    val timeSlot: String,
    val elapsedPercent: Float,
    val nextTitle: String,
    val nextTime: String
)

object EpgProvider {
    fun getEpgForChannel(channel: Channel): EpgProgram {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        val nextHour = (hour + 1) % 24
        val isAm = hour < 12
        val nextIsAm = nextHour < 12

        val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        val displayNextHour = if (nextHour == 0) 12 else if (nextHour > 12) nextHour - 12 else nextHour

        val amPm = if (isAm) "AM" else "PM"
        val nextAmPm = if (nextIsAm) "AM" else "PM"

        val timeSlot = String.format(Locale.US, "%d:00 %s - %d:00 %s", displayHour, amPm, displayNextHour, nextAmPm)
        val elapsed = (minute / 60f).coerceIn(0.05f, 0.95f)

        val cat = channel.category.lowercase(Locale.ROOT)
        val (curTitle, desc, nxtTitle) = when {
            cat.contains("sport") -> Triple(
                "Live Matchday: Championship Arena",
                "Real-time stadium commentary, multi-angle replays and live score tracker.",
                "Global Match Recap & Highlights"
            )
            cat.contains("news") -> Triple(
                "World Headline News & Ground Report",
                "Live satellite coverage of developing global headlines and in-depth analysis.",
                "Prime Time Global Debate"
            )
            cat.contains("movie") || cat.contains("film") || cat.contains("cinema") -> Triple(
                "Blockbuster Showcase: Prime Feature",
                "Full high-definition digital cinema presentation in stereo sound.",
                "Director's Cut Evening Gala"
            )
            cat.contains("kid") || cat.contains("animat") -> Triple(
                "Animated Adventures & Cartoon Club",
                "Fun-filled animated stories and adventures for the whole family.",
                "Junior Toons Magical Hour"
            )
            cat.contains("music") -> Triple(
                "Live Music Countdown & Hot Hits",
                "Continuous top chart hits, official music videos and studio sessions.",
                "Non-Stop Acoustic Nights"
            )
            cat.contains("doc") || cat.contains("nature") || cat.contains("science") -> Triple(
                "Planet Frontiers: Wildlife & Science",
                "Exploring deep ecosystems, technological marvels and historical expeditions.",
                "Expedition Earth: Ocean Depths"
            )
            else -> Triple(
                "${channel.name} Live Broadcast",
                "Continuous streaming live broadcast from official network feed.",
                "Evening Prime Showcase"
            )
        }

        return EpgProgram(
            currentTitle = curTitle,
            currentDescription = desc,
            timeSlot = timeSlot,
            elapsedPercent = elapsed,
            nextTitle = nxtTitle,
            nextTime = String.format(Locale.US, "at %d:00 %s", displayNextHour, nextAmPm)
        )
    }
}
