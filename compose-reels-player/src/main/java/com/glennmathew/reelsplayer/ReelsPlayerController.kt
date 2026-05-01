package com.glennmathew.reelsplayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun rememberReelsPlayerController(): ReelsPlayerController = remember { ReelsPlayerController() }

class ReelsPlayerController internal constructor() {
    private val _state = MutableStateFlow(ReelsPlayerState())
    val state: StateFlow<ReelsPlayerState> = _state.asStateFlow()

    private var playAction: () -> Unit = {}
    private var pauseAction: () -> Unit = {}
    private var togglePlayPauseAction: () -> Unit = {}
    private var muteAction: () -> Unit = {}
    private var unmuteAction: () -> Unit = {}
    private var toggleMuteAction: () -> Unit = {}
    private var seekToAction: (Long) -> Unit = {}
    private var replayAction: () -> Unit = {}
    private var scrollToAction: (Int) -> Unit = {}
    private var animateScrollToAction: (Int) -> Unit = {}
    private var nextAction: () -> Unit = {}
    private var previousAction: () -> Unit = {}
    private var setPlaybackSpeedAction: (Float) -> Unit = {}
    private var retryAction: () -> Unit = {}

    fun play() = playAction()
    fun pause() = pauseAction()
    fun togglePlayPause() = togglePlayPauseAction()
    fun mute() = muteAction()
    fun unmute() = unmuteAction()
    fun toggleMute() = toggleMuteAction()
    fun seekTo(positionMs: Long) = seekToAction(positionMs)
    fun replay() = replayAction()
    fun scrollTo(index: Int) = scrollToAction(index)
    fun animateScrollTo(index: Int) = animateScrollToAction(index)
    fun next() = nextAction()
    fun previous() = previousAction()
    fun setPlaybackSpeed(speed: Float) = setPlaybackSpeedAction(speed)
    fun retry() = retryAction()

    internal fun updateState(state: ReelsPlayerState) {
        _state.value = state
    }

    internal fun bind(
        play: () -> Unit,
        pause: () -> Unit,
        togglePlayPause: () -> Unit,
        mute: () -> Unit,
        unmute: () -> Unit,
        toggleMute: () -> Unit,
        seekTo: (Long) -> Unit,
        replay: () -> Unit,
        scrollTo: (Int) -> Unit,
        animateScrollTo: (Int) -> Unit,
        next: () -> Unit,
        previous: () -> Unit,
        setPlaybackSpeed: (Float) -> Unit,
        retry: () -> Unit
    ) {
        playAction = play
        pauseAction = pause
        togglePlayPauseAction = togglePlayPause
        muteAction = mute
        unmuteAction = unmute
        toggleMuteAction = toggleMute
        seekToAction = seekTo
        replayAction = replay
        scrollToAction = scrollTo
        animateScrollToAction = animateScrollTo
        nextAction = next
        previousAction = previous
        setPlaybackSpeedAction = setPlaybackSpeed
        retryAction = retry
    }
}
