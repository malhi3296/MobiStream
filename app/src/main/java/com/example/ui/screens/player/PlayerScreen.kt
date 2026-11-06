package com.example.ui.screens.player

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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

enum class GestureType {
    BRIGHTNESS,
    VOLUME
}

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
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    var isFullscreen by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(true) }
    var streamError by remember { mutableStateOf<String?>(null) }
    var isMuted by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(true) }
    var resizeModeIndex by remember { mutableIntStateOf(0) }

    // Gesture control states (Requirement 1)
    var playerSize by remember { mutableStateOf(IntSize.Zero) }
    var activeGesture by remember { mutableStateOf<GestureType?>(null) }
    var gestureLevel by remember { mutableFloatStateOf(0.5f) }
    var showGestureIndicator by remember { mutableStateOf(false) }

    val epgInfo = remember(channel.id) {
        EpgProvider.getEpgForChannel(channel)
    }

    val resizeModes = listOf(
        AspectRatioFrameLayout.RESIZE_MODE_FIT,
        AspectRatioFrameLayout.RESIZE_MODE_ZOOM,
        AspectRatioFrameLayout.RESIZE_MODE_FILL
    )

    // Requirement 5: Truly immersive fullscreen, hiding status bar & system navigation bar
    DisposableEffect(isFullscreen) {
        val window = activity?.window
        if (window != null) {
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            if (isFullscreen) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
            } else {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }

        onDispose {
            val windowObj = activity?.window
            if (windowObj != null) {
                val insetsController = WindowCompat.getInsetsController(windowObj, windowObj.decorView)
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    val handleExitPlayer = {
        val window = activity?.window
        if (window != null) {
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
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
        }
    }

    // Auto-hide controls after 4.5 seconds
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(4500)
            showControls = false
        }
    }

    // Auto-hide gesture indicator HUD
    LaunchedEffect(showGestureIndicator) {
        if (showGestureIndicator) {
            delay(1500)
            showGestureIndicator = false
            activeGesture = null
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
                    .onSizeChanged { playerSize = it }
                    .clickable { showControls = !showControls }
                    // Requirement 1: On-screen vertical drag gestures for Brightness (Left) and Volume (Right)
                    .pointerInput(playerSize) {
                        detectVerticalDragGestures(
                            onDragStart = { offset ->
                                val midX = if (playerSize.width > 0) playerSize.width / 2 else 500
                                if (offset.x < midX) {
                                    activeGesture = GestureType.BRIGHTNESS
                                    val currentBrightness = activity?.window?.attributes?.screenBrightness ?: 0.5f
                                    gestureLevel = if (currentBrightness < 0f) 0.5f else currentBrightness
                                } else {
                                    activeGesture = GestureType.VOLUME
                                    val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
                                    val curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
                                    gestureLevel = if (maxVol > 0f) (curVol / maxVol).coerceIn(0f, 1f) else 0.5f
                                }
                                showGestureIndicator = true
                            },
                            onVerticalDrag = { _, dragAmount ->
                                val height = if (playerSize.height > 0) playerSize.height.toFloat() else 800f
                                val delta = -dragAmount / height
                                gestureLevel = (gestureLevel + delta).coerceIn(0.02f, 1.0f)
                                showGestureIndicator = true

                                when (activeGesture) {
                                    GestureType.BRIGHTNESS -> {
                                        val window = activity?.window
                                        if (window != null) {
                                            val lp = window.attributes
                                            lp.screenBrightness = gestureLevel
                                            window.attributes = lp
                                        }
                                    }
                                    GestureType.VOLUME -> {
                                        val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                                        val targetVol = (gestureLevel * maxVol).toInt().coerceIn(0, maxVol)
                                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVol, 0)
                                    }
                                    null -> Unit
                                }
                            },
                            onDragEnd = {
                                // Indicator will auto-dismiss via LaunchedEffect
                            },
                            onDragCancel = {
                                showGestureIndicator = false
                                activeGesture = null
                            }
                        )
                    }
            ) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            resizeMode = resizeModes[resizeModeIndex]
                        }
                    },
                    update = { view ->
                        view.resizeMode = resizeModes[resizeModeIndex]
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Requirement 1: On-Screen Gesture Indicator HUD
                if (showGestureIndicator && activeGesture != null) {
                    Box(
                        modifier = Modifier
                            .align(
                                if (activeGesture == GestureType.BRIGHTNESS) Alignment.CenterStart
                                else Alignment.CenterEnd
                            )
                            .padding(horizontal = 48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xCC0E0A24))
                            .padding(horizontal = 18.dp, vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (activeGesture == GestureType.BRIGHTNESS) Icons.Default.BrightnessMedium
                                else Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = GlowLilac,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${(gestureLevel * 100).toInt()}%",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            // Vertical Progress Bar
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .height(90.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0x44FFFFFF))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height((90 * gestureLevel).dp)
                                        .align(Alignment.BottomCenter)
                                        .background(StreamViolet)
                                )
                            }
                        }
                    }
                }

                // Player Controls Overlay
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
                        if (isPlaying) exoPlayer.pause() else exoPlayer.play()
                    },
                    onToggleMute = {
                        isMuted = !isMuted
                        exoPlayer.volume = if (isMuted) 0f else 1f
                    },
                    onToggleFullscreen = {
                        isFullscreen = false
                    },
                    onToggleResize = {
                        resizeModeIndex = (resizeModeIndex + 1) % resizeModes.size
                    },
                    onToggleFavorite = { onToggleFavorite(channel.id) },
                    onRetry = {
                        streamError = null
                        isBuffering = true
                        exoPlayer.prepare()
                        exoPlayer.play()
                    }
                )
            }
        } else {
            // Portrait Mode with Details below
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MidnightBackground)
                    .verticalScroll(rememberScrollState())
            ) {
                // Video Surface (16:9 ratio)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(Color.Black)
                        .clickable { showControls = !showControls }
                ) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = false
                                layoutParams = FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                resizeMode = resizeModes[resizeModeIndex]
                            }
                        },
                        update = { view ->
                            view.resizeMode = resizeModes[resizeModeIndex]
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
                            if (isPlaying) exoPlayer.pause() else exoPlayer.play()
                        },
                        onToggleMute = {
                            isMuted = !isMuted
                            exoPlayer.volume = if (isMuted) 0f else 1f
                        },
                        onToggleFullscreen = {
                            isFullscreen = true
                        },
                        onToggleResize = {
                            resizeModeIndex = (resizeModeIndex + 1) % resizeModes.size
                        },
                        onToggleFavorite = { onToggleFavorite(channel.id) },
                        onRetry = {
                            streamError = null
                            isBuffering = true
                            exoPlayer.prepare()
                            exoPlayer.play()
                        }
                    )
                }

                // Channel Info Card
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
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
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = channel.name,
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = TextWhite,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.White.copy(alpha = 0.15f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "LIVE",
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp,
                                        color = Color.White.copy(alpha = 0.40f)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.White.copy(alpha = 0.15f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = channel.quality.ifBlank { "FHD" },
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp,
                                        color = Color.White.copy(alpha = 0.40f)
                                    )
                                }
                            }
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

                        IconButton(
                            onClick = { onToggleFavorite(channel.id) },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1B143E))
                        ) {
                            Icon(
                                imageVector = if (channel.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (channel.isFavorite) LiveRed else Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // EPG Detail Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MidnightCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(LiveRed)
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "NOW PLAYING",
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = epgInfo.timeSlot,
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp,
                                    color = GlowLilac
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = epgInfo.currentTitle,
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextWhite
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = epgInfo.currentDescription,
                                fontFamily = InterFontFamily,
                                fontSize = 12.sp,
                                color = TextMuted,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            LinearProgressIndicator(
                                progress = { epgInfo.elapsedPercent },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = StreamViolet,
                                trackColor = Color(0x33FFFFFF)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "UP NEXT: ",
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = "${epgInfo.nextTitle} (${epgInfo.nextTime})",
                                    fontFamily = InterFontFamily,
                                    fontSize = 11.sp,
                                    color = TextWhite,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Related Live Channels",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextWhite
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxWidth()
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

/**
 * Player Controls Overlay:
 * Requirement 6:
 * - Remove top right live, fhd and fav btns and add it alongside name make it smaller make it white with 40% opacity.
 * - Add fav btn before audio on bottom right.
 */
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
            // Top Bar: Back, Channel Name with small translucent LIVE/FHD badges alongside name
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = channel.name,
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = TextWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Requirement 6: Smaller LIVE & FHD badges alongside name, white with 40% opacity
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "LIVE",
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 7.5.sp,
                                    color = Color.White.copy(alpha = 0.40f)
                                )
                            }
                            Spacer(modifier = Modifier.width(3.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = channel.quality.ifBlank { "FHD" },
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 7.5.sp,
                                    color = Color.White.copy(alpha = 0.40f)
                                )
                            }
                        }
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

                // Top right buttons removed per Requirement 6
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

            // Bottom Bar: EPG Info Bar + Controls
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
                        // Requirement 6: Favorite button added BEFORE audio on bottom right
                        IconButton(onClick = onToggleFavorite) {
                            Icon(
                                imageVector = if (channel.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (channel.isFavorite) LiveRed else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

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
