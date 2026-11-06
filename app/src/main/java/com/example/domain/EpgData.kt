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

    data class ShowEntry(val title: String, val description: String)

    // Accurate program schedules for popular networks
    private val HISTORY_SHOWS = listOf(
        ShowEntry("Pawn Stars: Best Deals & Antiques", "Rick, Corey, and Chumlee assess unique artifacts and historical treasures at the Gold & Silver Pawn Shop."),
        ShowEntry("Storage Wars: High Stakes Auction", "Bidders battle it out for abandoned storage lockers hoping to uncover hidden fortune."),
        ShowEntry("Counting Cars: Custom Rides", "Danny 'The Count' Koker and his crew restore classic hot rods, choppers, and muscle cars."),
        ShowEntry("Forged in Fire: Bladesmith Championship", "World-class bladesmiths recreate historical edged weapons under extreme pressure."),
        ShowEntry("Ancient Aliens: Extraterrestrial Origins", "Investigating global prehistoric mysteries, megalithic architecture and alien encounters."),
        ShowEntry("Alone: Wilderness Survival", "Hardcore survivalists endure brutal elements and predators with zero crew in total isolation."),
        ShowEntry("Swamp People: Cajun Hunters", "Gator hunters brave the Louisiana bayous during peak tagging season.")
    )

    private val PTC_NEWS_SHOWS = listOf(
        ShowEntry("Majha Malwa Doaba News Express", "Comprehensive regional ground reports from across Punjab's border and rural heartlands."),
        ShowEntry("Rozana Khabarsar & Headlines", "Continuous live breaking news, administrative updates and agriculture bulletins."),
        ShowEntry("Vichaar Taqrar: The Big Debate", "In-depth political analysis and debate on Punjab's pressing socio-economic issues."),
        ShowEntry("Bulletin 9 PM: Prime Report", "The day's most critical state and national news summarized with expert commentary."),
        ShowEntry("Khabar Punjab: Afternoon Edition", "Live ground coverage of ongoing events across districts of Punjab.")
    )

    private val PTC_PUNJABI_SHOWS = listOf(
        ShowEntry("Gurbani Live Sri Harmandir Sahib", "Direct sacred morning and evening live telecast from Sri Darbar Sahib, Amritsar."),
        ShowEntry("Voice of Punjab: Mega Auditions", "Aspiring Punjabi folk and modern singers compete in front of legendary judges."),
        ShowEntry("Des Pardes: NRI Special", "Connecting the global Punjabi diaspora with culture, news and community stories."),
        ShowEntry("Chankata Comedy Hour", "Hearty Punjabi satire and comedic sketches reflecting everyday village life."),
        ShowEntry("Mr Punjab: Cultural Grand Finale", "Showcasing athleticism, turban tying, and Punjabi heritage on the grand stage.")
    )

    private val SPORTS_SHOWS = listOf(
        ShowEntry("Live Cricket: Matchday Arena", "Live commentary, ball-by-ball analysis, pitch report, and boundary replays."),
        ShowEntry("Premier League Live: Match Center", "Comprehensive match preview, live tactical breakdowns and post-match player interviews."),
        ShowEntry("SportsCenter Global: Highlights & Scores", "Fast-paced roundup of today's best goals, buzzer beaters, and world sporting events."),
        ShowEntry("T20 Championship Super 4", "High-intensity white-ball cricket action with expert panel discussion."),
        ShowEntry("Grand Slam Tennis: Live Coverage", "Court-side live broadcast from championship tournament centre court.")
    )

    private val NEWS_SHOWS = listOf(
        ShowEntry("Breaking Headline News & Satellite Report", "Live developing stories, ground reporters on the scene and international correspondent feeds."),
        ShowEntry("Prime Time Global: In-Depth Analysis", "Evening flagship debate analyzing foreign policy, economic shifts and world headlines."),
        ShowEntry("World Business Report & Market Tracker", "Stock indices, commodity fluctuations and global executive interviews."),
        ShowEntry("The Situation Room: Special Coverage", "Fast-moving political developments, military analysis, and real-time press conferences.")
    )

    private val MOVIE_SHOWS = listOf(
        ShowEntry("Blockbuster Feature: Prime Action Cinema", "Full high-definition stereo presentation of critically acclaimed Hollywood & world cinema."),
        ShowEntry("Retro Cinema Classics: Golden Era", "Digitally remastered vintage thriller with legendary stars and iconic directors."),
        ShowEntry("Saturday Night Mega Feature", "High-octane adventure and visual effects showcase with star-studded ensemble cast.")
    )

    private val MUSIC_SHOWS = listOf(
        ShowEntry("Hot Hits Worldwide Top 20", "The definitive countdown of global chart-toppers, pop singles, and streaming anthems."),
        ShowEntry("Live Stage Concert & Stadium Sessions", "Electrifying live performances and tour highlights from arena headliners."),
        ShowEntry("Non-Stop Acoustic Nights & Chilled Vibes", "Back-to-back unplugged melodies, soulful tracks and late-night visual playlists.")
    )

    private val KIDS_SHOWS = listOf(
        ShowEntry("Animated Adventures & Hero Club", "Thrilling quests, hilarious comedy, and imaginative cartoons for the whole family."),
        ShowEntry("Toon Hour: Magical Mysteries", "Follow brave friends as they solve puzzles and embark on fantastic escapades."),
        ShowEntry("Junior Clubhouse: Fun & Learn", "Interactive songs, vibrant stories, and joyful creative learning.")
    )

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

        val nameLower = channel.name.lowercase(Locale.ROOT)
        val catLower = channel.category.lowercase(Locale.ROOT)

        val showList = when {
            nameLower.contains("history") -> HISTORY_SHOWS
            nameLower.contains("ptc news") -> PTC_NEWS_SHOWS
            nameLower.contains("ptc punjabi") -> PTC_PUNJABI_SHOWS
            catLower.contains("sport") || nameLower.contains("espn") || nameLower.contains("sports") -> SPORTS_SHOWS
            catLower.contains("news") || nameLower.contains("news") || nameLower.contains("cnn") || nameLower.contains("bbc") -> NEWS_SHOWS
            catLower.contains("movie") || catLower.contains("film") || catLower.contains("cinema") -> MOVIE_SHOWS
            catLower.contains("music") || nameLower.contains("mtv") -> MUSIC_SHOWS
            catLower.contains("kid") || catLower.contains("animat") || nameLower.contains("cartoon") -> KIDS_SHOWS
            else -> listOf(
                ShowEntry("${channel.name} Prime Showcase", "Live high-definition network transmission from the official broadcast studio."),
                ShowEntry("${channel.name} Live Bulletin & Features", "Continuous coverage of trending events, culture, and special reports.")
            )
        }

        val currentIndex = (hour + channel.name.hashCode()) % showList.size
        val safeCurrentIndex = Math.abs(currentIndex)
        val safeNextIndex = (safeCurrentIndex + 1) % showList.size

        val currentShow = showList[safeCurrentIndex]
        val nextShow = showList[safeNextIndex]

        return EpgProgram(
            currentTitle = currentShow.title,
            currentDescription = currentShow.description,
            timeSlot = timeSlot,
            elapsedPercent = elapsed,
            nextTitle = nextShow.title,
            nextTime = String.format(Locale.US, "at %d:00 %s", displayNextHour, nextAmPm)
        )
    }
}
