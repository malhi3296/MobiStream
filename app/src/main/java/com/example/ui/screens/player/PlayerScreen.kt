package com.example.ui.screens.player

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.AudioManager
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.SubcomposeAsyncImage
import com.example.data.model.Channel
import com.example.domain.ChannelLogoResolver
import com.example.domain.EpgProgram
import com.example.domain.EpgProvider
import com.example.ui.components.ChannelCard
import com.example.ui.components.ChannelLogoBox
import com.example.ui.components.LiveBadge
import com.example.ui.components.MobiCard
import com.example.ui.components.getCountryFlag
import com.example.ui.theme.MobiStreamTheme
import com.example.ui.theme.mobiColors
import kotlinx.coroutines.delay
import kotlin.math.abs

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
    val view = LocalView.current
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    var isFullscreen by rememberSaveable { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(true) }
    var streamError by remember { mutableStateOf<String?>(null) }
    var isMuted by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(true) }
    var resizeModeIndex by remember { mutableIntStateOf(0) }
    var hasClosedCaptions by remember(channel.id) {
        mutableStateOf(
            channel.name.contains("CC", ignoreCase = true) ||
                channel.quality.contains("CC", ignoreCase = true)
        )
    }
    var isClosedCaptionsEnabled by remember { mutableStateOf(true) }
    var isLocked by rememberSaveable { mutableStateOf(false) }
    var showLockedNotice by remember { mutableStateOf(false) }

    // Brightness & volume levels for sliders & drag gestures
    var brightnessLevel by remember {
        val initial = activity?.window?.attributes?.screenBrightness ?: 0.65f
        mutableFloatStateOf(if (initial < 0f) 0.65f else initial.coerceIn(0.05f, 1f))
    }
    var volumeLevel by remember {
        val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1).toFloat()
        val curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        mutableFloatStateOf((curVol / maxVol).coerceIn(0f, 1f))
    }

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

    // Keep screen awake while locked or actively playing
    DisposableEffect(isLocked, isPlaying) {
        view.keepScreenOn = isLocked || isPlaying
        onDispose {
            view.keepScreenOn = false
        }
    }

    // Orientation + transient system bars
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

            override fun onTracksChanged(tracks: Tracks) {
                var ccFound = false
                for (group in tracks.groups) {
                    if (group.type == C.TRACK_TYPE_TEXT && group.length > 0) {
                        ccFound = true
                        break
                    }
                }
                if (!ccFound && (channel.name.contains("CC", ignoreCase = true) || channel.quality.contains("CC", ignoreCase = true))) {
                    ccFound = true
                }
                hasClosedCaptions = ccFound
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    LaunchedEffect(showControls, isPlaying, isLocked) {
        if (showControls && isPlaying && !isLocked) {
            delay(4500)
            showControls = false
        }
    }

    LaunchedEffect(showGestureIndicator) {
        if (showGestureIndicator) {
            delay(1500)
            showGestureIndicator = false
            activeGesture = null
        }
    }

    LaunchedEffect(showLockedNotice) {
        if (showLockedNotice) {
            delay(3000)
            showLockedNotice = false
        }
    }

    val onToggleClosedCaptions = {
        val next = !isClosedCaptionsEnabled
        isClosedCaptionsEnabled = next
        exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
            .buildUpon()
            .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, !next)
            .build()
    }

    val applyBrightness: (Float) -> Unit = { value ->
        val clamped = value.coerceIn(0.05f, 1.0f)
        brightnessLevel = clamped
        val window = activity?.window
        if (window != null) {
            val lp = window.attributes
            lp.screenBrightness = clamped
            window.attributes = lp
        }
    }

    val applyVolume: (Float) -> Unit = { value ->
        val clamped = value.coerceIn(0f, 1f)
        volumeLevel = clamped
        val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val targetVol = (clamped * maxVol).toInt().coerceIn(0, maxVol)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVol, 0)
    }

    // Consume back while locked; exit when unlocked
    BackHandler {
        if (isLocked) {
            showLockedNotice = true
        } else {
            handleExitPlayer()
        }
    }

    PlayerScreenContent(
        channel = channel,
        relatedChannels = relatedChannels,
        epgInfo = epgInfo,
        isFullscreen = isFullscreen,
        isPlaying = isPlaying,
        isBuffering = isBuffering,
        streamError = streamError,
        isMuted = isMuted,
        showControls = showControls,
        hasClosedCaptions = hasClosedCaptions,
        isClosedCaptionsEnabled = isClosedCaptionsEnabled,
        isLocked = isLocked,
        showLockedNotice = showLockedNotice,
        brightnessLevel = brightnessLevel,
        volumeLevel = volumeLevel,
        activeGesture = activeGesture,
        gestureLevel = gestureLevel,
        showGestureIndicator = showGestureIndicator,
        onVideoSurfaceSizeChanged = { playerSize = it },
        onVideoTap = {
            if (isLocked) {
                showLockedNotice = !showLockedNotice
            } else {
                showControls = !showControls
            }
        },
        onDragStart = { startX ->
            if (!isLocked) {
                val midX = if (playerSize.width > 0) playerSize.width / 2 else 500
                if (startX < midX) {
                    activeGesture = GestureType.BRIGHTNESS
                    gestureLevel = brightnessLevel
                } else {
                    activeGesture = GestureType.VOLUME
                    val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
                    val curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
                    volumeLevel = if (maxVol > 0f) (curVol / maxVol).coerceIn(0f, 1f) else volumeLevel
                    gestureLevel = volumeLevel
                }
                showGestureIndicator = true
            }
        },
        onVerticalDragDelta = { dragAmount ->
            if (!isLocked) {
                val height = if (playerSize.height > 0) playerSize.height.toFloat() else 800f
                val delta = -dragAmount / height
                gestureLevel = (gestureLevel + delta).coerceIn(0.02f, 1.0f)
                showGestureIndicator = true
                when (activeGesture) {
                    GestureType.BRIGHTNESS -> applyBrightness(gestureLevel)
                    GestureType.VOLUME -> applyVolume(gestureLevel)
                    null -> Unit
                }
            }
        },
        onDragCancel = {
            showGestureIndicator = false
            activeGesture = null
        },
        onBack = handleExitPlayer,
        onTogglePlay = {
            if (!isLocked) {
                if (isPlaying) exoPlayer.pause() else exoPlayer.play()
            }
        },
        onToggleMute = {
            if (!isLocked) {
                isMuted = !isMuted
                exoPlayer.volume = if (isMuted) 0f else 1f
            }
        },
        onToggleFullscreen = {
            if (!isLocked) {
                isFullscreen = !isFullscreen
            }
        },
        onToggleResize = {
            if (!isLocked) {
                resizeModeIndex = (resizeModeIndex + 1) % resizeModes.size
            }
        },
        onToggleFavorite = { onToggleFavorite(channel.id) },
        onToggleClosedCaptions = {
            if (!isLocked) onToggleClosedCaptions()
        },
        onLock = {
            isLocked = true
            showControls = false
            showLockedNotice = true
        },
        onUnlock = {
            isLocked = false
            showControls = true
            showLockedNotice = false
        },
        onRetry = {
            streamError = null
            isBuffering = true
            exoPlayer.prepare()
            exoPlayer.play()
        },
        onSelectChannel = onSelectChannel,
        videoSlot = {
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
                        subtitleView?.setUserDefaultStyle()
                        subtitleView?.setUserDefaultTextSize()
                    }
                },
                update = { playerView ->
                    playerView.resizeMode = resizeModes[resizeModeIndex]
                    playerView.subtitleView?.visibility = if (isClosedCaptionsEnabled) View.VISIBLE else View.GONE
                },
                modifier = Modifier.fillMaxSize()
            )
        },
        modifier = modifier
    )
}

@Composable
fun PlayerScreenContent(
    channel: Channel,
    relatedChannels: List<Channel>,
    epgInfo: EpgProgram,
    isFullscreen: Boolean,
    isPlaying: Boolean,
    isBuffering: Boolean,
    streamError: String?,
    isMuted: Boolean,
    showControls: Boolean,
    hasClosedCaptions: Boolean,
    isClosedCaptionsEnabled: Boolean,
    isLocked: Boolean,
    showLockedNotice: Boolean,
    brightnessLevel: Float,
    volumeLevel: Float,
    activeGesture: GestureType?,
    gestureLevel: Float,
    showGestureIndicator: Boolean,
    onVideoSurfaceSizeChanged: (IntSize) -> Unit = {},
    onVideoTap: () -> Unit = {},
    onDragStart: (Float) -> Unit = {},
    onVerticalDragDelta: (Float) -> Unit = {},
    onDragCancel: () -> Unit = {},
    onBack: () -> Unit = {},
    onTogglePlay: () -> Unit = {},
    onToggleMute: () -> Unit = {},
    onToggleFullscreen: () -> Unit = {},
    onToggleResize: () -> Unit = {},
    onToggleFavorite: () -> Unit = {},
    onToggleClosedCaptions: () -> Unit = {},
    onLock: () -> Unit = {},
    onUnlock: () -> Unit = {},
    onRetry: () -> Unit = {},
    onSelectChannel: (Channel) -> Unit = {},
    videoSlot: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B1324))
        )
    },
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val mobi = MaterialTheme.mobiColors

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isFullscreen) Color.Black else colors.background)
            .testTag("player_screen")
    ) {
        if (isFullscreen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged(onVideoSurfaceSizeChanged)
                    .clickable { onVideoTap() }
                    .then(
                        if (!isLocked) {
                            Modifier.pointerInput(Unit) {
                                detectVerticalDragGestures(
                                    onDragStart = { offset -> onDragStart(offset.x) },
                                    onVerticalDrag = { _, dragAmount -> onVerticalDragDelta(dragAmount) },
                                    onDragCancel = onDragCancel
                                )
                            }
                        } else {
                            Modifier
                        }
                    )
            ) {
                videoSlot()

                // Drag HUD when swiping brightness/volume
                if (!isLocked && showGestureIndicator && activeGesture != null) {
                    GestureHudBadge(
                        activeGesture = activeGesture,
                        gestureLevel = gestureLevel,
                        accentColor = colors.primary,
                        modifier = Modifier
                            .align(if (activeGesture == GestureType.BRIGHTNESS) Alignment.CenterStart else Alignment.CenterEnd)
                            .padding(horizontal = 56.dp)
                    )
                }

                // Fullscreen Landscape Overlay
                LandscapeControlsOverlay(
                    channel = channel,
                    isPlaying = isPlaying,
                    isBuffering = isBuffering,
                    streamError = streamError,
                    isClosedCaptionsEnabled = isClosedCaptionsEnabled,
                    hasClosedCaptions = hasClosedCaptions,
                    showControls = showControls && !isLocked,
                    brightnessLevel = brightnessLevel,
                    volumeLevel = volumeLevel,
                    epgInfo = epgInfo,
                    onBack = onBack,
                    onTogglePlay = onTogglePlay,
                    onToggleFavorite = onToggleFavorite,
                    onLock = onLock,
                    onToggleClosedCaptions = onToggleClosedCaptions,
                    onToggleResize = onToggleResize,
                    onExitFullscreen = onToggleFullscreen,
                    onRetry = onRetry
                )

                // Locked state: ONLY right-edge Unlock button + hint
                if (isLocked) {
                    LockedUnlockOverlay(
                        showHint = showLockedNotice,
                        onUnlock = onUnlock,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 20.dp)
                    )
                }
            }
        } else {
            // Portrait Mode: Video (top 16:9) + EPG & Related details below (themed Dark/Light)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background)
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(Color.Black)
                        .clickable { onVideoTap() }
                ) {
                    videoSlot()

                    PortraitVideoOverlay(
                        channel = channel,
                        isPlaying = isPlaying,
                        isBuffering = isBuffering,
                        streamError = streamError,
                        hasClosedCaptions = hasClosedCaptions,
                        isClosedCaptionsEnabled = isClosedCaptionsEnabled,
                        showControls = showControls && !isLocked,
                        onBack = onBack,
                        onTogglePlay = onTogglePlay,
                        onToggleFavorite = onToggleFavorite,
                        onLock = onLock,
                        onToggleClosedCaptions = onToggleClosedCaptions,
                        onToggleResize = onToggleResize,
                        onToggleFullscreen = onToggleFullscreen,
                        onRetry = onRetry
                    )

                    if (isLocked) {
                        LockedUnlockOverlay(
                            showHint = showLockedNotice,
                            onUnlock = onUnlock,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 12.dp)
                        )
                    }
                }

                // Channel Info Row + Mute + Favorite + EPG Card + Related Channels
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ChannelLogoBox(
                            channel = channel,
                            compact = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .width(76.dp)
                                .height(46.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = channel.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = colors.onBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${getCountryFlag(channel.countryCode)} ${channel.country}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${channel.category}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = mobi.accentText
                                )
                            }
                        }

                        // Mute button in info row
                        Surface(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable { onToggleMute() }
                                .testTag("portrait_info_mute_button"),
                            shape = CircleShape,
                            color = colors.surface,
                            border = BorderStroke(1.dp, colors.outline)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                    contentDescription = if (isMuted) "Unmute" else "Mute",
                                    tint = colors.onSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Existing Favorite control in info row (synced with video overlay Favorite)
                        Surface(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable { onToggleFavorite() }
                                .testTag("portrait_info_favorite_button"),
                            shape = CircleShape,
                            color = colors.surface,
                            border = BorderStroke(1.dp, colors.outline)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (channel.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (channel.isFavorite) mobi.liveBadge else colors.onSurface,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Programme Card (NOW PLAYING, title + description, progress)
                    MobiCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LiveBadge(text = "NOW PLAYING")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = epgInfo.timeSlot,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = mobi.accentText
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = epgInfo.currentTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = epgInfo.currentDescription,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.onSurfaceVariant,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            LinearProgressIndicator(
                                progress = { epgInfo.elapsedPercent },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = colors.primary,
                                trackColor = colors.outline
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "UP NEXT: ",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.onSurfaceVariant
                                )
                                Text(
                                    text = "${epgInfo.nextTitle} (${epgInfo.nextTime})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Text(
                        text = "Related Live Channels",
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.onBackground
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(relatedChannels.filter { it.id != channel.id }.take(10), key = { it.id }) { relChannel ->
                            ChannelCard(
                                channel = relChannel,
                                onClick = { onSelectChannel(relChannel) },
                                onToggleFavorite = {}
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

/**
 * Portrait Video Overlay:
 * - Top: Back button, LIVE + FHD badges with Lock and Favorite beside them!
 * - Center: Buffering / Error Retry / Pause button.
 * - Bottom: CC, Aspect Ratio, Fullscreen.
 */
@Composable
private fun PortraitVideoOverlay(
    channel: Channel,
    isPlaying: Boolean,
    isBuffering: Boolean,
    streamError: String?,
    hasClosedCaptions: Boolean,
    isClosedCaptionsEnabled: Boolean,
    showControls: Boolean,
    onBack: () -> Unit,
    onTogglePlay: () -> Unit,
    onToggleFavorite: () -> Unit,
    onLock: () -> Unit,
    onToggleClosedCaptions: () -> Unit,
    onToggleResize: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onRetry: () -> Unit
) {
    val mobi = MaterialTheme.mobiColors

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
                            Color(0xB8000000),
                            Color(0x26000000),
                            Color(0xC8000000)
                        )
                    )
                )
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            // Top bar: Back + LIVE + FHD + Lock + Favorite
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OverlayCircleButton(
                    onClick = onBack,
                    testTag = "player_back_button"
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LiveBadge(text = "LIVE")

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.22f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = channel.quality.ifBlank { "FHD" },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    OverlayCircleButton(
                        onClick = onLock,
                        testTag = "portrait_overlay_lock_button"
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = "Lock controls",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    OverlayCircleButton(
                        onClick = onToggleFavorite,
                        testTag = "portrait_overlay_favorite_button"
                    ) {
                        Icon(
                            imageVector = if (channel.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (channel.isFavorite) mobi.liveBadge else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Center Pause / Buffering / Retry
            PlayerCenterStateControl(
                isPlaying = isPlaying,
                isBuffering = isBuffering,
                streamError = streamError,
                onTogglePlay = onTogglePlay,
                onRetry = onRetry,
                modifier = Modifier.align(Alignment.Center)
            )

            // Bottom row: CC, Aspect Ratio, Fullscreen
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (hasClosedCaptions) {
                    CcOverlayButton(
                        enabled = isClosedCaptionsEnabled,
                        onClick = onToggleClosedCaptions
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                OverlayCircleButton(
                    onClick = onToggleResize,
                    testTag = "portrait_aspect_button"
                ) {
                    Icon(
                        imageVector = Icons.Default.AspectRatio,
                        contentDescription = "Aspect ratio",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                OverlayCircleButton(
                    onClick = onToggleFullscreen,
                    testTag = "portrait_fullscreen_button"
                ) {
                    Icon(
                        imageVector = Icons.Default.Fullscreen,
                        contentDescription = "Fullscreen",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Fullscreen Landscape Overlay:
 * - Top-left: Back + channel name + LIVE/FHD
 * - Top-right: Favorite, Lock, CC, Aspect ratio, Exit fullscreen
 * - Center: Large Pause
 * - Left vertical Brightness slider, Right vertical Volume slider
 * - Bottom: Programme title, description, progress
 */
@Composable
private fun LandscapeControlsOverlay(
    channel: Channel,
    isPlaying: Boolean,
    isBuffering: Boolean,
    streamError: String?,
    hasClosedCaptions: Boolean,
    isClosedCaptionsEnabled: Boolean,
    showControls: Boolean,
    brightnessLevel: Float,
    volumeLevel: Float,
    epgInfo: EpgProgram,
    onBack: () -> Unit,
    onTogglePlay: () -> Unit,
    onToggleFavorite: () -> Unit,
    onLock: () -> Unit,
    onToggleClosedCaptions: () -> Unit,
    onToggleResize: () -> Unit,
    onExitFullscreen: () -> Unit,
    onRetry: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val mobi = MaterialTheme.mobiColors

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
                            Color(0xC8000000),
                            Color(0x2E000000),
                            Color(0xD8000000)
                        )
                    )
                )
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Top bar: Left (Back + Channel Name + LIVE/FHD), Right (Favorite, Lock, CC, Aspect Ratio, Exit Fullscreen)
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
                    OverlayCircleButton(
                        onClick = onBack,
                        testTag = "player_back_button"
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = channel.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    LiveBadge(text = "LIVE")
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.22f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = channel.quality.ifBlank { "FHD" },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OverlayCircleButton(
                        onClick = onToggleFavorite,
                        testTag = "landscape_favorite_button"
                    ) {
                        Icon(
                            imageVector = if (channel.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (channel.isFavorite) mobi.liveBadge else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    OverlayCircleButton(
                        onClick = onLock,
                        testTag = "player_lock_button"
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = "Lock controls",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (hasClosedCaptions) {
                        CcOverlayButton(
                            enabled = isClosedCaptionsEnabled,
                            onClick = onToggleClosedCaptions
                        )
                    }

                    OverlayCircleButton(
                        onClick = onToggleResize,
                        testTag = "landscape_aspect_button"
                    ) {
                        Icon(
                            imageVector = Icons.Default.AspectRatio,
                            contentDescription = "Aspect ratio",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    OverlayCircleButton(
                        onClick = onExitFullscreen,
                        testTag = "landscape_exit_fullscreen_button"
                    ) {
                        Icon(
                            imageVector = Icons.Default.FullscreenExit,
                            contentDescription = "Exit fullscreen",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Left edge vertical Brightness slider
            EdgeVerticalSlider(
                icon = Icons.Default.BrightnessMedium,
                level = brightnessLevel,
                accentColor = colors.primary,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 6.dp)
            )

            // Center Pause / Play / Buffering / Retry
            PlayerCenterStateControl(
                isPlaying = isPlaying,
                isBuffering = isBuffering,
                streamError = streamError,
                onTogglePlay = onTogglePlay,
                onRetry = onRetry,
                modifier = Modifier.align(Alignment.Center)
            )

            // Right edge vertical Volume slider
            EdgeVerticalSlider(
                icon = Icons.Default.VolumeUp,
                level = volumeLevel,
                accentColor = colors.primary,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 6.dp)
            )

            // Bottom programme title, description, progress
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Text(
                    text = epgInfo.currentTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = epgInfo.currentDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.78f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { epgInfo.elapsedPercent },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = colors.primary,
                    trackColor = Color(0x4DFFFFFF)
                )
            }
        }
    }
}

@Composable
private fun EdgeVerticalSlider(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    level: Float,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x73000000))
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
        Box(
            modifier = Modifier
                .width(5.dp)
                .height(92.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.25f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(level.coerceIn(0.05f, 1f))
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(3.dp))
                    .background(accentColor)
            )
        }
    }
}

@Composable
private fun GestureHudBadge(
    activeGesture: GestureType,
    gestureLevel: Float,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xCC0B1324))
            .padding(horizontal = 18.dp, vertical = 18.dp),
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
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${(gestureLevel * 100).toInt()}%",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

/**
 * Locked state UI:
 * Only an Unlock button on the right edge + exact hint text "Controls locked · tap the lock to unlock".
 */
@Composable
private fun LockedUnlockOverlay(
    showHint: Boolean,
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AnimatedVisibility(
            visible = showHint,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xD90B1324))
                    .border(1.dp, colors.primary, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Controls locked · tap the lock to unlock",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }
        }

        Surface(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .clickable { onUnlock() }
                .testTag("player_unlock_button"),
            shape = CircleShape,
            color = Color(0xE60B1324),
            border = BorderStroke(1.5.dp, colors.primary)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Unlock controls",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun OverlayCircleButton(
    onClick: () -> Unit,
    testTag: String = "",
    content: @Composable () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color(0x660B1324))
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier)
    ) {
        content()
    }
}

@Composable
private fun CcOverlayButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .testTag("player_cc_button")
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (enabled) colors.primary else Color.White.copy(alpha = 0.16f))
                .border(
                    width = 1.dp,
                    color = if (enabled) colors.primary else Color.White.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 6.dp, vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "CC",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                color = if (enabled) colors.onPrimary else Color.White.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
private fun PlayerCenterStateControl(
    isPlaying: Boolean,
    isBuffering: Boolean,
    streamError: String?,
    onTogglePlay: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val mobi = MaterialTheme.mobiColors

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when {
            isBuffering -> {
                CircularProgressIndicator(
                    color = colors.primary,
                    trackColor = Color(0x66FFFFFF),
                    modifier = Modifier.size(54.dp)
                )
            }
            streamError != null -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Stream Unavailable",
                        style = MaterialTheme.typography.titleMedium,
                        color = mobi.liveBadge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    IconButton(
                        onClick = onRetry,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(colors.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry",
                            tint = colors.onPrimary
                        )
                    }
                }
            }
            else -> {
                IconButton(
                    onClick = onTogglePlay,
                    modifier = Modifier
                        .size(66.dp)
                        .clip(CircleShape)
                        .background(colors.primary.copy(alpha = 0.88f))
                        .testTag("play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = colors.onPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}

private val previewSampleChannel = Channel(
    id = "bbc",
    name = "BBC World News",
    logoUrl = "",
    streamUrl = "",
    category = "News",
    country = "United Kingdom",
    countryCode = "GB",
    language = "English",
    quality = "FHD",
    isLive = true,
    isFavorite = true
)

private val previewEpg = EpgProgram(
    currentTitle = "Global Headlines & Markets",
    currentDescription = "Live international correspondents reporting on world affairs and business.",
    timeSlot = "08:00 PM - 09:00 PM",
    elapsedPercent = 0.62f,
    nextTitle = "World Sports Today",
    nextTime = "at 09:00 PM"
)

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PlayerPortraitPreview() {
    MobiStreamTheme {
        PlayerScreenContent(
            channel = previewSampleChannel,
            relatedChannels = listOf(previewSampleChannel.copy(id = "cnn", name = "CNN Live")),
            epgInfo = previewEpg,
            isFullscreen = false,
            isPlaying = true,
            isBuffering = false,
            streamError = null,
            isMuted = false,
            showControls = true,
            hasClosedCaptions = true,
            isClosedCaptionsEnabled = true,
            isLocked = false,
            showLockedNotice = false,
            brightnessLevel = 0.7f,
            volumeLevel = 0.6f,
            activeGesture = null,
            gestureLevel = 0.6f,
            showGestureIndicator = false
        )
    }
}

@Preview(showBackground = true, widthDp = 760, heightDp = 360)
@Preview(showBackground = true, widthDp = 760, heightDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PlayerLandscapePreview() {
    MobiStreamTheme {
        PlayerScreenContent(
            channel = previewSampleChannel,
            relatedChannels = emptyList(),
            epgInfo = previewEpg,
            isFullscreen = true,
            isPlaying = true,
            isBuffering = false,
            streamError = null,
            isMuted = false,
            showControls = true,
            hasClosedCaptions = true,
            isClosedCaptionsEnabled = true,
            isLocked = false,
            showLockedNotice = false,
            brightnessLevel = 0.7f,
            volumeLevel = 0.6f,
            activeGesture = null,
            gestureLevel = 0.6f,
            showGestureIndicator = false
        )
    }
}

@Preview(showBackground = true, widthDp = 760, heightDp = 360)
@Composable
private fun PlayerLockedPreview() {
    MobiStreamTheme(darkTheme = true) {
        PlayerScreenContent(
            channel = previewSampleChannel,
            relatedChannels = emptyList(),
            epgInfo = previewEpg,
            isFullscreen = true,
            isPlaying = true,
            isBuffering = false,
            streamError = null,
            isMuted = false,
            showControls = false,
            hasClosedCaptions = true,
            isClosedCaptionsEnabled = true,
            isLocked = true,
            showLockedNotice = true,
            brightnessLevel = 0.7f,
            volumeLevel = 0.6f,
            activeGesture = null,
            gestureLevel = 0.6f,
            showGestureIndicator = false
        )
    }
}
