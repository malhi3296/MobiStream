package com.example.ui.screens.player

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.data.model.Channel
import com.example.domain.EpgProvider
import com.example.ui.components.ChannelCard
import com.example.ui.components.StreamTagBadge
import com.example.ui.components.StreamTagType
import com.example.ui.components.getCountryFlag
import com.example.ui.theme.GlowLilac
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LiveRed
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightCard
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.StreamGradientStart
import com.example.ui.theme.StreamViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    channel: Channel,
    relatedChannels: List<Channel>,
    onBack: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onSelectChannel: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Requirement 3: Auto open all channels in landscape mode (full screen)
    var isFullscreen by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(true) }
    var streamError by remember { mutableStateOf<String?>(null) }
    var isMuted by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(true) }
    var resizeModeIndex by remember { mutableIntStateOf(0) }

    val epgInfo = remember(channel.id) {
        EpgProvider.getEpgForChannel(channel)
    }

    val resizeModes = listOf(
        AspectRatioFrameLayout.RESIZE_MODE_FIT,
        AspectRatioFrameLayout.RESIZE_MODE_ZOOM,
        AspectRatioFrameLayout.RESIZE_MODE_FILL
    )

    // Set landscape orientation immediately on entering PlayerScreen
    LaunchedEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    val handleExitPlayer = {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onBack()
    }

    // ExoPlayer Instance
    val exoPlayer = remember(channel.id) {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
            val mediaItem = MediaItem.fromUri(channel.streamUrl)
            setMediaItem(mediaItem)
            prepare()
        }
    }

    DisposableEffect(channel.id) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = (playbackState == Player.STATE_BUFFERING)
                if (playbackState == Player.STATE_READY) {
                    streamError = null
                }
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlayerError(error: PlaybackException) {
                isBuffering = false
                streamError = "Stream temporarily unavailable: ${error.message ?: "Network error"}"
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    // Auto-hide controls after 4.5 seconds
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(4500)
            showControls = false
        }
    }

    BackHandler {
        handleExitPlayer()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("player_screen")
    ) {
        if (isFullscreen) {
            // Fullscreen Landscape Player Mode (Default)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showControls = !showControls }
            ) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false
                            resizeMode = resizeModes[resizeModeIndex]
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    update = { playerView ->
                        playerView.resizeMode = resizeModes[resizeModeIndex]
                    },
                    modifier = Modifier.fillMaxSize()
                )

                PlayerControlsOverlay(
                    channel = channel,
                    isPlaying = isPlaying,
                    isBuffering = isBuffering,
                    streamError = streamError,
                    isMuted = isMuted,
                    isFullscreen = true,
                    showControls = showControls,
                    epgProgramTitle = epgInfo.currentTitle,
                    epgTimeSlot = epgInfo.timeSlot,
                    epgProgress = epgInfo.elapsedPercent,
                    epgNextTitle = epgInfo.nextTitle,
                    onBack = handleExitPlayer,
                    onTogglePlay = {
                        if (exoPlayer.isPlaying) {
                            exoPlayer.pause()
                        } else {
                            if (streamError != null) {
                                exoPlayer.prepare()
                            }
                            exoPlayer.play()
                        }
                    },
                    onToggleMute = {
                        isMuted = !isMuted
                        exoPlayer.volume = if (isMuted) 0f else 1f
                    },
                    onToggleFullscreen = {
                        isFullscreen = false
                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    },
                    onToggleResize = {
                        resizeModeIndex = (resizeModeIndex + 1) % resizeModes.size
                    },
                    onToggleFavorite = { onToggleFavorite(channel.id) },
                    onRetry = {
                        streamError = null
                        exoPlayer.prepare()
                        exoPlayer.play()
                    }
                )
            }
        } else {
            // Portrait Mode fallback: Player + Channel Info + EPG Schedule + Related Channels
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MidnightBackground)
                    .verticalScroll(rememberScrollState())
            ) {
                // Video Player Container (16:9 ratio)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(Color.Black)
                        .clickable { showControls = !showControls }
                        .testTag("video_player_container")
                ) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = false
                                resizeMode = resizeModes[resizeModeIndex]
                                layoutParams = FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            }
                        },
                        update = { playerView ->
                            playerView.resizeMode = resizeModes[resizeModeIndex]
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    PlayerControlsOverlay(
                        channel = channel,
                        isPlaying = isPlaying,
                        isBuffering = isBuffering,
                        streamError = streamError,
                        isMuted = isMuted,
                        isFullscreen = false,
                        showControls = showControls,
                        epgProgramTitle = epgInfo.currentTitle,
                        epgTimeSlot = epgInfo.timeSlot,
                        epgProgress = epgInfo.elapsedPercent,
                        epgNextTitle = epgInfo.nextTitle,
                        onBack = handleExitPlayer,
                        onTogglePlay = {
                            if (exoPlayer.isPlaying) {
                                exoPlayer.pause()
                            } else {
                                if (streamError != null) {
                                    exoPlayer.prepare()
                                }
                                exoPlayer.play()
                            }
                        },
                        onToggleMute = {
                            isMuted = !isMuted
                            exoPlayer.volume = if (isMuted) 0f else 1f
                        },
                        onToggleFullscreen = {
                            isFullscreen = true
                            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        },
                        onToggleResize = {
                            resizeModeIndex = (resizeModeIndex + 1) % resizeModes.size
                        },
                        onToggleFavorite = { onToggleFavorite(channel.id) },
                        onRetry = {
                            streamError = null
                            exoPlayer.prepare()
                            exoPlayer.play()
                        }
                    )
                }

                // Channel Details Underneath Player
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (channel.logoUrl.isNotBlank()) {
                                AsyncImage(
                                    model = channel.logoUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF1B143E))
                                        .padding(4.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                            Column {
                                Text(
                                    text = channel.name,
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = TextWhite,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${getCountryFlag(channel.countryCode)} ${channel.country}",
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 12.sp,
                                        color = TextMuted
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "• ${channel.category}",
                                        fontFamily = InterFontFamily,
                                        fontSize = 12.sp,
                                        color = GlowLilac
                                    )
                                }
                            }
                        }

                        // Favorite Heart Button
                        IconButton(
                            onClick = { onToggleFavorite(channel.id) },
                            modifier = Modifier
                                .size(44.dp)
                                .background(MidnightCard, CircleShape)
                                .testTag("player_favorite_toggle")
                        ) {
                            Icon(
                                imageVector = if (channel.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Toggle favorite",
                                tint = if (channel.isFavorite) LiveRed else Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // EPG Live Program Card (Requirement 10)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = MidnightCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = GlowLilac,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "LIVE EPG PROGRAM",
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = GlowLilac
                                    )
                                }

                                Text(
                                    text = epgInfo.timeSlot,
                                    fontFamily = InterFontFamily,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = epgInfo.currentTitle,
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextWhite
                            )

                            Text(
                                text = epgInfo.currentDescription,
                                fontFamily = InterFontFamily,
                                fontSize = 12.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { epgInfo.elapsedPercent },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = StreamViolet,
                                trackColor = Color(0xFF281E57)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Up Next: ${epgInfo.nextTitle} (${epgInfo.nextTime})",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp,
                                color = GlowLilac
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stream Info Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = MidnightCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StreamStatItem(label = "Format", value = "HLS (.m3u8)")
                            StreamStatItem(label = "Quality", value = channel.quality)
                            StreamStatItem(label = "Status", value = "ONLINE", valueColor = Color(0xFF10B981))
                            StreamStatItem(label = "Category", value = channel.category)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Up Next / Related Channels (Single-tap channel switcher)
                    Text(
                        text = "More Channels in ${channel.category}",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextWhite
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(relatedChannels.filter { it.id != channel.id }.take(10)) { relChannel ->
                            Box(modifier = Modifier.width(150.dp)) {
                                ChannelCard(
                                    channel = relChannel,
                                    onClick = { onSelectChannel(relChannel) },
                                    onToggleFavorite = { onToggleFavorite(relChannel.id) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
private fun PlayerControlsOverlay(
    channel: Channel,
    isPlaying: Boolean,
    isBuffering: Boolean,
    streamError: String?,
    isMuted: Boolean,
    isFullscreen: Boolean,
    showControls: Boolean,
    epgProgramTitle: String,
    epgTimeSlot: String,
    epgProgress: Float,
    epgNextTitle: String,
    onBack: () -> Unit,
    onTogglePlay: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onToggleResize: () -> Unit,
    onToggleFavorite: () -> Unit,
    onRetry: () -> Unit
) {
    AnimatedVisibility(
        visible = showControls || isBuffering || streamError != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xD9000000),
                            Color(0x33000000),
                            Color(0xEE000000)
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            // Top Bar: Back, Channel Name, Favorite, Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0x800E0A24), CircleShape)
                            .testTag("player_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = channel.name,
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${getCountryFlag(channel.countryCode)} ${channel.country}",
                                fontFamily = InterFontFamily,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${channel.category}",
                                fontFamily = InterFontFamily,
                                fontSize = 11.sp,
                                color = GlowLilac
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    StreamTagBadge(type = StreamTagType.LIVE)
                    Spacer(modifier = Modifier.width(6.dp))
                    StreamTagBadge(type = StreamTagType.QUALITY_4K, customText = channel.quality)
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0x800E0A24), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (channel.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (channel.isFavorite) LiveRed else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Center Controls: Buffering, Error with Retry, or Play/Pause
            Box(
                modifier = Modifier.align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isBuffering -> {
                        CircularProgressIndicator(
                            color = StreamGradientStart,
                            trackColor = Color(0x66FFFFFF),
                            modifier = Modifier.size(54.dp)
                        )
                    }
                    streamError != null -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Stream Unavailable",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = LiveRed,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            IconButton(
                                onClick = onRetry,
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(StreamViolet, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Retry",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                    else -> {
                        IconButton(
                            onClick = onTogglePlay,
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color(0x994F2BC4), CircleShape)
                                .testTag("play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }

            // Bottom Bar: EPG Info Bar (Requirement 10) + Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                // EPG Live Stream Overlay Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(LiveRed)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "LIVE NOW",
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = epgProgramTitle,
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "($epgTimeSlot)",
                                fontFamily = InterFontFamily,
                                fontSize = 10.sp,
                                color = GlowLilac
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Next: $epgNextTitle",
                            fontFamily = InterFontFamily,
                            fontSize = 10.sp,
                            color = TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Mute / Unmute
                        IconButton(onClick = onToggleMute) {
                            Icon(
                                imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                contentDescription = if (isMuted) "Unmute" else "Mute",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Aspect Ratio
                        IconButton(onClick = onToggleResize) {
                            Icon(
                                imageVector = Icons.Default.AspectRatio,
                                contentDescription = "Aspect ratio",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Fullscreen toggle
                        IconButton(onClick = onToggleFullscreen) {
                            Icon(
                                imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = "Toggle Fullscreen",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // EPG Live Elapsed Progress Bar
                LinearProgressIndicator(
                    progress = { epgProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = StreamViolet,
                    trackColor = Color(0x4DFFFFFF)
                )
            }
        }
    }
}

@Composable
private fun StreamStatItem(
    label: String,
    value: String,
    valueColor: Color = TextWhite
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = valueColor
        )
    }
}
