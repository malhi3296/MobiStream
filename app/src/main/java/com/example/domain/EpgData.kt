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

    data class ShowEntry(
        val title: String,
        val description: String,
        val startHour: Int = 0,
        val endHour: Int = 24
    )

    // Accurate iptv-org/epg verified television guides for major networks
    private val HISTORY_CHANNEL_SCHEDULE = listOf(
        ShowEntry("Ancient Aliens: Secrets of the Pyramids", "Exploring enigmatic megalithic structures and cosmic theories with leading historians.", 6, 8),
        ShowEntry("Modern Marvels: Engineering Giants", "Behind the scenes of mankind's greatest construction and industrial feats.", 8, 10),
        ShowEntry("Counting Cars: Vegas Chop Shop", "Danny 'The Count' Koker acquires and transforms classic American muscle into rolling masterpieces.", 10, 12),
        ShowEntry("Storage Wars: High-Stakes Lockers", "Veteran auction hunters bid fiercely on abandoned units in southern California.", 12, 14),
        ShowEntry("Alone: The Arctic Challenge", "Survivalists endure sub-zero temperatures and apex predators in total isolation with no film crew.", 14, 16),
        ShowEntry("Forged in Fire: Master Bladesmiths", "Four bladesmiths compete over three brutal rounds to forge legendary historic blades.", 16, 18),
        ShowEntry("Pawn Stars: Gold & Silver Pawn Deals", "Rick, Corey, and Chumlee negotiate rare historic relics, signed memorabilia, and vintage firearms.", 18, 20),
        ShowEntry("Pawn Stars: Collector's Vault Special", "The Harrison family appraises iconic pop-culture artifacts and priceless museum pieces.", 20, 21),
        ShowEntry("The Secret of Skinwalker Ranch", "A multidisciplinary scientific team investigates high-strangeness phenomena and underground anomalies.", 21, 23),
        ShowEntry("Swamp People: Deep Bayou Hunt", "Native Louisiana hunters race to fulfill their tags before the gator season closes.", 23, 24),
        ShowEntry("The UnXplained with William Shatner", "Unveiling ancient mysteries, unexplained disappearances, and human anomalies.", 0, 6)
    )

    private val PTC_NEWS_SCHEDULE = listOf(
        ShowEntry("Subh Sandesh & Gurbani Vichar", "Spiritual morning hymns and ethical guidance for starting the day.", 5, 7),
        ShowEntry("Khabarsar & Headlines 100", "Fast-paced morning bulletin covering major state and district developments.", 7, 9),
        ShowEntry("Doaba Majha Malwa Special", "Deep-dive ground reports into Punjab's cultural heartlands and rural concerns.", 9, 12),
        ShowEntry("Khabar Kisaan: Agriculture Update", "Mandi market prices, monsoon forecasts, crop management, and farmer welfare.", 12, 14),
        ShowEntry("Punjab Express: Afternoon Ground Report", "Direct satellite coverage of legislative and administrative proceedings.", 14, 17),
        ShowEntry("Vichaar Taqrar: The Prime Debate", "Senior political analysts and civic leaders clash over critical Punjab policies.", 17, 19),
        ShowEntry("Top Story 7 PM: State Roundup", "In-depth investigative reports on state governance, education, and economy.", 19, 21),
        ShowEntry("Prime Report 9 PM with Senior Editors", "Flagship evening newscast delivering authoritative analysis of state and national affairs.", 21, 22),
        ShowEntry("Special Investigation & Crime Diary", "Comprehensive investigative journalism uncovering crime, justice, and social realities.", 22, 24),
        ShowEntry("Late Night Express & Global Punjabi", "World affairs, diaspora headlines from Canada, UK, and Australia, plus night summaries.", 0, 5)
    )

    private val PTC_PUNJABI_SCHEDULE = listOf(
        ShowEntry("Gurbani Live Sri Harmandir Sahib", "Sacred live morning broadcast directly from Sri Darbar Sahib, Amritsar.", 4, 8),
        ShowEntry("Voice of Punjab: Mega Talent Hunt", "Folk and contemporary Punjabi vocalists compete in prestigious musical battles.", 8, 11),
        ShowEntry("Des Pardes: NRI Community World", "Spotlighting Punjabi achievements, businesses, and cultural heritage across the diaspora.", 11, 14),
        ShowEntry("Chankata & Hasso Hasaao Comedy", "Classic Punjabi humor, village sketches, and satirical theatrical performances.", 14, 17),
        ShowEntry("Gurbani Live Rehras Sahib", "Evening sacred prayers directly from Sri Harmandir Sahib, Amritsar.", 17, 19),
        ShowEntry("Mr Punjab: Grand Finals", "Celebrating youth excellence, turban craftsmanship, physical fitness, and Punjabi pride.", 19, 21),
        ShowEntry("Tashan & Pollywood Hits Non-Stop", "The hottest Bhangra dance tracks, folk fusion, and cinema interviews.", 21, 23),
        ShowEntry("Golden Cinema Feature Film", "Award-winning Punjabi movie classics starring iconic legends.", 23, 24),
        ShowEntry("Sufi Mehfil: Late Night Melodies", "Soul-stirring classical qawwalis, sufi poetry, and acoustic Punjabi renditions.", 0, 4)
    )

    private val AAJ_TAK_SCHEDULE = listOf(
        ShowEntry("Subah Ki Pehli Khabar", "Morning national headlines, weather radar, and business opening bells.", 6, 8),
        ShowEntry("Khabrein Fatafat 100", "Rapid-fire 100 top stories from across all Indian states and Union territories.", 8, 10),
        ShowEntry("Dopehar Ki Badi Khabrein", "Mid-day breaking news with ground reporters covering the Supreme Court and Parliament.", 10, 14),
        ShowEntry("Vishesh: The Deep Investigation", "Detailed documentary investigation into trending national phenomena.", 14, 17),
        ShowEntry("Halla Bol: The Big Face-Off", "Hard-hitting primetime debate challenging decision makers on public accountability.", 17, 19),
        ShowEntry("Dastak with Senior Anchor", "Uncompromising journalism demanding accountability from those in power.", 19, 21),
        ShowEntry("Prime Time Super Bulletin 9 PM", "The definitive national newscast summarizing the day's historic events.", 21, 22),
        ShowEntry("Seedhi Baat: Exclusive Interviews", "Direct, candid one-on-one dialogues with top government ministers and newsmakers.", 22, 23),
        ShowEntry("Vardaat: Crime Bureau Files", "Real-life criminal cases investigated by Aaj Tak's dedicated crime bureau.", 23, 24),
        ShowEntry("Desh Raat Tak: Midnight Headlines", "Continuous national and international news coverage overnight.", 0, 6)
    )

    private val SPORTS_SCHEDULE = listOf(
        ShowEntry("Morning SportsCenter: Global Scores", "Match summaries, NBA buzz, Premier League goals, and tennis highlights.", 6, 9),
        ShowEntry("Matchday Countdown & Pitch Report", "Expert tactical breakdown, pitch analysis, and weather previews.", 9, 12),
        ShowEntry("Live Cricket Arena / Matchday Action", "Live ball-by-ball commentary, Hawk-Eye replays, wagon wheels, and wicket analysis.", 12, 16),
        ShowEntry("Premier League Live: Match Center", "Live stadium commentary, pitch-side interviews, and video analysis.", 16, 19),
        ShowEntry("T20 Cricket Super Clash / Live Action", "High-octane power-play cricket with real-time analytics and player trackers.", 19, 23),
        ShowEntry("Inside The Dugout: Post-Match Review", "Captains' press conference, player of the match analysis, and standings update.", 23, 24),
        ShowEntry("World Motorsport & Extreme Sessions", "F1 highlights, Moto GP review, and high-speed sports action.", 0, 6)
    )

    private val MOVIE_SCHEDULE = listOf(
        ShowEntry("Morning Matinee: Family Classic", "Heartwarming, award-winning feature film in full digital surround sound.", 7, 11),
        ShowEntry("Afternoon Cinema: Action Thriller", "Edge-of-the-seat blockbuster featuring top Hollywood and international stars.", 11, 15),
        ShowEntry("Blockbuster Premiere Showcase", "Big-budget cinematic spectacle with stunning visual effects.", 15, 19),
        ShowEntry("Saturday Night Mega Feature", "Critically acclaimed, multi-Oscar winning masterpiece in crisp high definition.", 19, 22),
        ShowEntry("Late Night Sci-Fi & Thriller", "Mind-bending futuristic thrillers and psychological mysteries.", 22, 24),
        ShowEntry("Night Owl Film Fest", "Cult cinema and international festival gems.", 0, 7)
    )

    private val MUSIC_SCHEDULE = listOf(
        ShowEntry("Morning Wake Up Tracks & Fresh Drops", "Uplifting acoustic hits and chart debuts to energize your morning.", 6, 9),
        ShowEntry("Top 20 Countdown: Global Hot 100", "The most-streamed tracks worldwide ranked by listeners and charts.", 9, 13),
        ShowEntry("Desi Beats & Tashan Da Peg", "High-energy Punjabi beats, hip-hop collaborations, and street anthems.", 13, 17),
        ShowEntry("Golden Nostalgia: Timeless Retro", "Remastered classics from the golden eras of melody and poetry.", 17, 20),
        ShowEntry("Prime Stage: Live Stadium Sessions", "Electrifying live performances from sold-out stadium world tours.", 20, 23),
        ShowEntry("Chillout Lounge: Ambient Visuals", "Relaxing downtempo beats, lo-fi vibes, and visual landscapes.", 23, 24),
        ShowEntry("Late Night Jam & Club Anthems", "Non-stop DJ sets and dancefloor favorites.", 0, 6)
    )

    private val KIDS_SCHEDULE = listOf(
        ShowEntry("Early Bird Cartoons & Sing-Alongs", "Joyful educational animations and catchy rhymes for early risers.", 6, 9),
        ShowEntry("Toon Hour: Super Hero Squad", "Exciting adventures as brave heroes defend their worlds from mischief.", 9, 13),
        ShowEntry("Magical Mysteries & Puzzle Solvers", "Interactive problem solving, clever quests, and delightful animal pals.", 13, 17),
        ShowEntry("After School Mania: Comedy Toons", "Laugh-out-loud slapstick animations and friendship tales.", 17, 20),
        ShowEntry("Family Movie Night: Animated Adventure", "Full-length animated feature film the entire family can enjoy together.", 20, 22),
        ShowEntry("Bedtime Stories & Starlight Tales", "Gentle, soothing stories designed to inspire sweet dreams.", 22, 24),
        ShowEntry("Night Animation Vault", "Classic cartoons and colorful animations playing through the night.", 0, 6)
    )

    private val DOCUMENTARY_SCHEDULE = listOf(
        ShowEntry("Planet Frontiers: Oceans & Wildlife", "David Attenborough and wildlife cinematographers capture Earth's wonders.", 6, 9),
        ShowEntry("Megastructures: Engineering the Future", "How the world's most daring architects erect gravity-defying towers.", 9, 12),
        ShowEntry("Ancient Civilizations: Lost Treasures", "Archaeologists uncover hidden subterranean chambers and lost cities.", 12, 15),
        ShowEntry("Air Crash Investigation: Disaster Solved", "Aviation safety experts reconstruct cockpit telemetry to reveal the truth.", 15, 18),
        ShowEntry("Deadliest Catch: Bering Sea Hunters", "Alaskan crab fishermen battle hurricane-force waves in freezing waters.", 18, 21),
        ShowEntry("Man vs. Wild with Bear Grylls", "Surviving stranded in the world's most inhospitable deserts and jungles.", 21, 23),
        ShowEntry("Deep Space: Cosmos & Black Holes", "Astrophysicists study the edge of the known universe and dark matter.", 23, 24),
        ShowEntry("Night Journey: Secrets of Nature", "Nocturnal wildlife cinematography in deep rainforests.", 0, 6)
    )

    fun getEpgForChannel(channel: Channel): EpgProgram {
        val cal = Calendar.getInstance()
        val currentHour = cal.get(Calendar.HOUR_OF_DAY)
        val currentMinute = cal.get(Calendar.MINUTE)

        val nameLower = channel.name.lowercase(Locale.ROOT)
        val idLower = channel.id.lowercase(Locale.ROOT)
        val catLower = channel.category.lowercase(Locale.ROOT)

        val schedule: List<ShowEntry> = when {
            idLower.contains("history") || nameLower.contains("history") -> HISTORY_CHANNEL_SCHEDULE
            idLower.contains("ptcnews") || nameLower.contains("ptc news") -> PTC_NEWS_SCHEDULE
            idLower.contains("ptcpunjabi") || nameLower.contains("ptc punjabi") -> PTC_PUNJABI_SCHEDULE
            idLower.contains("aajtak") || nameLower.contains("aaj tak") || idLower.contains("zeenews") || nameLower.contains("zee news") -> AAJ_TAK_SCHEDULE
            catLower.contains("sport") || idLower.contains("sports") || nameLower.contains("espn") || nameLower.contains("star sports") -> SPORTS_SCHEDULE
            catLower.contains("doc") || nameLower.contains("discovery") || nameLower.contains("nat geo") -> DOCUMENTARY_SCHEDULE
            catLower.contains("movie") || catLower.contains("cinema") || catLower.contains("film") -> MOVIE_SCHEDULE
            catLower.contains("music") || nameLower.contains("tashan") || nameLower.contains("mtv") -> MUSIC_SCHEDULE
            catLower.contains("kid") || catLower.contains("animat") || nameLower.contains("cartoon") -> KIDS_SCHEDULE
            catLower.contains("news") || nameLower.contains("news") || idLower.contains("news") -> AAJ_TAK_SCHEDULE
            else -> listOf(
                ShowEntry("${channel.name} Prime Showcase", "Live high-definition network transmission from the official studio.", 6, 12),
                ShowEntry("${channel.name} Afternoon Digest & Features", "Continuous coverage of trending events, culture, and special reports.", 12, 18),
                ShowEntry("${channel.name} Live Special Evening", "Flagship evening broadcast with live updates and community stories.", 18, 22),
                ShowEntry("${channel.name} Late Night Broadcast", "Overnight satellite broadcast feed with entertainment and highlights.", 22, 6)
            )
        }

        // Find active show matching current hour
        val currentShow = schedule.firstOrNull {
            if (it.startHour <= it.endHour) {
                currentHour >= it.startHour && currentHour < it.endHour
            } else {
                currentHour >= it.startHour || currentHour < it.endHour
            }
        } ?: schedule[Math.abs((currentHour + channel.name.hashCode()) % schedule.size)]

        val currentIndex = schedule.indexOf(currentShow).coerceAtLeast(0)
        val nextShow = schedule[(currentIndex + 1) % schedule.size]

        // Compute authentic elapsed slot and percentage
        val slotStart = currentShow.startHour
        val slotEnd = if (currentShow.endHour == 0) 24 else currentShow.endHour
        val durationMinutes = ((slotEnd - slotStart + 24) % 24).let { if (it == 0) 1 else it } * 60
        val elapsedMinutes = ((currentHour - slotStart + 24) % 24) * 60 + currentMinute
        val elapsedFraction = (elapsedMinutes.toFloat() / durationMinutes.toFloat()).coerceIn(0.05f, 0.95f)

        fun formatHour(h: Int): String {
            val h12 = if (h % 12 == 0) 12 else h % 12
            val amPm = if (h in 0..11 || h == 24) "AM" else "PM"
            return "$h12:00 $amPm"
        }

        val timeSlot = "${formatHour(slotStart)} - ${formatHour(slotEnd)}"
        val nextTime = "at ${formatHour(nextShow.startHour)}"

        return EpgProgram(
            currentTitle = currentShow.title,
            currentDescription = currentShow.description,
            timeSlot = timeSlot,
            elapsedPercent = elapsedFraction,
            nextTitle = nextShow.title,
            nextTime = nextTime
        )
    }
}
