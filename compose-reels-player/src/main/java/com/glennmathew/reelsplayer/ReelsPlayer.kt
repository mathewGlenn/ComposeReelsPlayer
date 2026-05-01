package com.glennmathew.reelsplayer

import androidx.annotation.OptIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import com.glennmathew.reelsplayer.config.ReelsPlayerConfig
import com.glennmathew.reelsplayer.model.ReelItem
import com.glennmathew.reelsplayer.model.ReelsAnalyticsEvent
import com.glennmathew.reelsplayer.model.ReelsRepeatMode
import com.glennmathew.reelsplayer.player.ReelsPlaybackTracker
import com.glennmathew.reelsplayer.player.ReelsPlayerManager
import com.glennmathew.reelsplayer.player.ReelsPreloader
import com.glennmathew.reelsplayer.ui.DefaultErrorContent
import com.glennmathew.reelsplayer.ui.DefaultLoadingContent
import com.glennmathew.reelsplayer.ui.DefaultReelsOverlay
import com.glennmathew.reelsplayer.ui.ReelsPage
import com.glennmathew.reelsplayer.util.ReelsLifecycleObserver
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
@OptIn(UnstableApi::class)
@kotlin.OptIn(ExperimentalFoundationApi::class)
fun ReelsPlayer(
    items: List<ReelItem>,
    modifier: Modifier = Modifier,
    controller: ReelsPlayerController = rememberReelsPlayerController(),
    initialIndex: Int = 0,
    config: ReelsPlayerConfig = ReelsPlayerConfig(),
    overlay: @Composable BoxScope.(
        item: ReelItem,
        state: ReelsPlayerState,
        actions: ReelsPlayerActions
    ) -> Unit = { item, state, actions ->
        DefaultReelsOverlay(item = item, state = state, actions = actions)
    },
    loadingContent: @Composable BoxScope.(item: ReelItem) -> Unit = { item ->
        DefaultLoadingContent(item = item)
    },
    errorContent: @Composable BoxScope.(
        item: ReelItem,
        error: Throwable?,
        retry: () -> Unit
    ) -> Unit = { item, error, retry ->
        DefaultErrorContent(item = item, error = error, onRetry = retry)
    },
    onCurrentReelChanged: (index: Int, item: ReelItem) -> Unit = { _, _ -> },
    onPlaybackStateChanged: (state: ReelsPlayerState) -> Unit = {},
    onAnalyticsEvent: (event: ReelsAnalyticsEvent) -> Unit = {},
    onLoadMore: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val safeInitialIndex = initialIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0))
    val pagerState = rememberPagerState(initialPage = safeInitialIndex) { items.size }
    val manager = remember {
        ReelsPlayerManager(context.applicationContext, config)
    }
    val preloader = remember {
        ReelsPreloader(context.applicationContext, config)
    }
    val state by controller.state.collectAsStateWithLifecycle()
    val latestAutoplay by rememberUpdatedState(config.autoplay)
    val tracker = remember { ReelsPlaybackTracker() }
    var lastLoadedId by remember { mutableStateOf<String?>(null) }
    var previousIndex by remember { mutableStateOf<Int?>(null) }
    var lastLoadMoreIndex by remember { mutableStateOf<Int?>(null) }
    var shouldResumeAfterLifecyclePause by remember { mutableStateOf(false) }
    var pausedByDragging by remember { mutableStateOf(false) }
    var bufferingStartedAt by remember { mutableLongStateOf(0L) }

    val actions = remember(controller) {
        object : ReelsPlayerActions {
            override fun play() = controller.play()
            override fun pause() = controller.pause()
            override fun togglePlayPause() = controller.togglePlayPause()
            override fun mute() = controller.mute()
            override fun unmute() = controller.unmute()
            override fun toggleMute() = controller.toggleMute()
            override fun seekTo(positionMs: Long) = controller.seekTo(positionMs)
            override fun replay() = controller.replay()
            override fun next() = controller.next()
            override fun previous() = controller.previous()
            override fun setPlaybackSpeed(speed: Float) = controller.setPlaybackSpeed(speed)
            override fun retry() = controller.retry()
        }
    }

    LaunchedEffect(config) {
        manager.applyConfig(config)
        preloader.updateConfig(config)
    }

    LaunchedEffect(manager) {
        manager.state.collectLatest { nextState ->
            if (nextState.isPlaying) {
                pausedByDragging = false
            }
            val decoratedState = nextState.copy(
                isDragging = pagerState.isScrollInProgress,
                isPausedByDragging = pausedByDragging && !nextState.isPlaying
            )
            controller.updateState(decoratedState)
            onPlaybackStateChanged(decoratedState)
        }
    }

    LaunchedEffect(items, pagerState.settledPage) {
        val currentIndex = pagerState.settledPage.coerceIn(0, (items.size - 1).coerceAtLeast(0))
        val item = items.getOrNull(currentIndex)
        if (item == null) {
            controller.updateState(ReelsPlayerState())
            return@LaunchedEffect
        }
        if (lastLoadedId != item.id) {
            previousIndex?.takeIf { it != currentIndex }?.let { from ->
                onAnalyticsEvent(ReelsAnalyticsEvent.VideoSkipped(item, from, currentIndex))
            }
            previousIndex = currentIndex
            lastLoadedId = item.id
            tracker.reset()
            manager.load(currentIndex, item, config.autoplay && !pagerState.isScrollInProgress)
            onCurrentReelChanged(currentIndex, item)
            onAnalyticsEvent(ReelsAnalyticsEvent.ReelImpression(item, currentIndex))
        }
        preloader.preload(items, currentIndex)
        if (items.lastIndex - currentIndex <= config.loadMoreThreshold && lastLoadMoreIndex != currentIndex) {
            lastLoadMoreIndex = currentIndex
            onLoadMore()
        }
    }

    LaunchedEffect(pagerState, config.pauseWhenDragging, config.playWhenPageSettled) {
        snapshotFlow { pagerState.isScrollInProgress }.collectLatest { scrolling ->
            if (scrolling && config.pauseWhenDragging) {
                pausedByDragging = true
                controller.updateState(
                    controller.state.value.copy(
                        isDragging = true,
                        isPausedByDragging = true
                    )
                )
                manager.pause()
            } else if (!scrolling && config.playWhenPageSettled && config.autoplay) {
                controller.updateState(
                    controller.state.value.copy(
                        isDragging = false,
                        isPausedByDragging = pausedByDragging
                    )
                )
                manager.play()
            } else if (!scrolling) {
                pausedByDragging = false
                controller.updateState(
                    controller.state.value.copy(
                        isDragging = false,
                        isPausedByDragging = false
                    )
                )
            }
        }
    }

    LaunchedEffect(state.currentItem?.id, state.isPlaying) {
        var lastTick = System.currentTimeMillis()
        while (true) {
            delay(250L)
            manager.updateProgress()
            val now = System.currentTimeMillis()
            val current = controller.state.value
            val item = current.currentItem
            if (current.isPlaying && item != null) {
                val watchedMs = tracker.addWatchTime(now - lastTick)
                onAnalyticsEvent(ReelsAnalyticsEvent.WatchTimeUpdated(item, current.currentIndex, watchedMs))
                tracker.collectNewThresholds(current.durationMs).forEach { threshold ->
                    onAnalyticsEvent(ReelsAnalyticsEvent.WatchThresholdReached(item, current.currentIndex, threshold))
                }
            }
            lastTick = now
        }
    }

    LaunchedEffect(config.repeatMode, items) {
        manager.onStarted = {
            controller.state.value.currentItem?.let {
                onAnalyticsEvent(ReelsAnalyticsEvent.VideoStarted(it, controller.state.value.currentIndex))
            }
        }
        manager.onPaused = {
            controller.state.value.currentItem?.let {
                onAnalyticsEvent(
                    ReelsAnalyticsEvent.VideoPaused(
                        item = it,
                        index = controller.state.value.currentIndex,
                        positionMs = controller.state.value.currentPositionMs
                    )
                )
            }
        }
        manager.onError = { error ->
            controller.state.value.currentItem?.let {
                onAnalyticsEvent(ReelsAnalyticsEvent.PlaybackError(it, controller.state.value.currentIndex, error))
            }
        }
        manager.onBufferingChanged = { buffering ->
            val current = controller.state.value
            current.currentItem?.let { item ->
                if (buffering) {
                    bufferingStartedAt = System.currentTimeMillis()
                    onAnalyticsEvent(ReelsAnalyticsEvent.BufferingStarted(item, current.currentIndex))
                } else if (bufferingStartedAt > 0L) {
                    onAnalyticsEvent(
                        ReelsAnalyticsEvent.BufferingEnded(
                            item,
                            current.currentIndex,
                            System.currentTimeMillis() - bufferingStartedAt
                        )
                    )
                    bufferingStartedAt = 0L
                }
            }
        }
        manager.onEnded = {
            val current = controller.state.value
            current.currentItem?.let {
                onAnalyticsEvent(ReelsAnalyticsEvent.VideoCompleted(it, current.currentIndex))
            }
            if (config.repeatMode == ReelsRepeatMode.All) {
                val next = if (current.currentIndex >= items.lastIndex) 0 else current.currentIndex + 1
                scope.launch { pagerState.animateScrollToPage(next) }
            }
        }
    }

    DisposableEffect(lifecycleOwner, manager) {
        val observer = ReelsLifecycleObserver(
            onPause = {
                shouldResumeAfterLifecyclePause = controller.state.value.isPlaying
                manager.pause()
            },
            onStop = { manager.pause() },
            onResume = {
                if (shouldResumeAfterLifecyclePause && latestAutoplay) {
                    manager.play()
                }
                shouldResumeAfterLifecyclePause = false
            },
            onDestroy = {
                manager.pause()
            }
        )
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            preloader.releaseAll()
            manager.release()
        }
    }

    LaunchedEffect(controller, items.size) {
        controller.bind(
            play = manager::play,
            pause = manager::pause,
            togglePlayPause = manager::togglePlayPause,
            mute = {
                manager.mute()
                controller.state.value.currentItem?.let {
                    onAnalyticsEvent(ReelsAnalyticsEvent.Muted(it, controller.state.value.currentIndex))
                }
            },
            unmute = {
                manager.unmute()
                controller.state.value.currentItem?.let {
                    onAnalyticsEvent(ReelsAnalyticsEvent.Unmuted(it, controller.state.value.currentIndex))
                }
            },
            toggleMute = {
                val wasMuted = controller.state.value.isMuted
                manager.toggleMute()
                controller.state.value.currentItem?.let {
                    val event = if (wasMuted) {
                        ReelsAnalyticsEvent.Unmuted(it, controller.state.value.currentIndex)
                    } else {
                        ReelsAnalyticsEvent.Muted(it, controller.state.value.currentIndex)
                    }
                    onAnalyticsEvent(event)
                }
            },
            seekTo = manager::seekTo,
            replay = {
                tracker.reset()
                manager.replay()
                controller.state.value.currentItem?.let {
                    onAnalyticsEvent(ReelsAnalyticsEvent.VideoReplayed(it, controller.state.value.currentIndex))
                }
            },
            scrollTo = { index ->
                if (index in items.indices) scope.launch { pagerState.scrollToPage(index) }
            },
            animateScrollTo = { index ->
                if (index in items.indices) scope.launch { pagerState.animateScrollToPage(index) }
            },
            next = {
                val next = (pagerState.currentPage + 1).coerceAtMost(items.lastIndex)
                if (next in items.indices) scope.launch { pagerState.animateScrollToPage(next) }
            },
            previous = {
                val previous = (pagerState.currentPage - 1).coerceAtLeast(0)
                if (previous in items.indices) scope.launch { pagerState.animateScrollToPage(previous) }
            },
            setPlaybackSpeed = manager::setPlaybackSpeed,
            retry = manager::retry
        )
    }

    if (items.isEmpty()) {
        Box(modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            Text(text = "No reels", color = Color.White)
        }
        return
    }

    VerticalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        beyondViewportPageCount = 1,
        key = { page -> "${items[page].id}#$page" }
    ) { page ->
        val item = items[page]
        val isActivePage = page == state.currentIndex
        ReelsPage(
            item = item,
            state = state,
            isActivePage = isActivePage,
            player = if (isActivePage) manager.player else null,
            actions = actions,
            config = config,
            modifier = Modifier.reelsGestures(item, actions, config),
            overlay = overlay,
            loadingContent = loadingContent,
            errorContent = errorContent
        )
    }
}

@Composable
private fun Modifier.reelsGestures(
    item: ReelItem,
    actions: ReelsPlayerActions,
    config: ReelsPlayerConfig
): Modifier {
    val gestureConfig = config.gestureConfig
    var longPressActive by remember(item.id, gestureConfig) { mutableStateOf(false) }
    return this
        .pointerInput(item.id, gestureConfig) {
            detectTapGestures(
                onTap = {
                    if (!gestureConfig.singleTapEnabled) return@detectTapGestures
                    gestureConfig.onSingleTap?.invoke(item, actions)
                        ?: if (gestureConfig.defaultSingleTapBehavior) actions.togglePlayPause() else Unit
                },
                onDoubleTap = { offset ->
                    if (gestureConfig.doubleTapEnabled) {
                        gestureConfig.onDoubleTap?.invoke(item, offset, actions)
                    }
                },
                onLongPress = {
                    if (!gestureConfig.longPressEnabled) return@detectTapGestures
                    if (gestureConfig.defaultLongPressSpeedBehavior) {
                        actions.setPlaybackSpeed(gestureConfig.longPressPlaybackSpeed)
                    }
                    longPressActive = true
                    gestureConfig.onLongPressStart?.invoke(item, actions)
                }
            )
        }
        .pointerInput(item.id, gestureConfig.longPressPlaybackSpeed) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                waitForUpOrCancellation()
                if (gestureConfig.longPressEnabled && longPressActive) {
                    longPressActive = false
                    if (gestureConfig.defaultLongPressSpeedBehavior) {
                        actions.setPlaybackSpeed(1f)
                    }
                    gestureConfig.onLongPressEnd?.invoke(item, actions)
                }
            }
        }
}
