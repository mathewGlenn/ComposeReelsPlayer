package com.glennmathew.reelsplayer.player

import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.glennmathew.reelsplayer.ReelsPlayerState
import com.glennmathew.reelsplayer.config.ReelsPlayerConfig
import com.glennmathew.reelsplayer.model.ReelItem
import com.glennmathew.reelsplayer.model.ReelsPlaybackState
import com.glennmathew.reelsplayer.model.ReelsRepeatMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.max

@UnstableApi
internal class ReelsPlayerManager(
    context: Context,
    private var config: ReelsPlayerConfig
) {
    private val appContext = context.applicationContext
    private var mediaSourceFactory = ReelsMediaSourceFactory(appContext, config.cacheConfig)
    private var currentItem: ReelItem? = null
    private var currentIndex: Int = 0
    private var userMuted = config.muteConfig.initiallyMuted || config.muteConfig.forceMuted

    val player: ExoPlayer = ExoPlayer.Builder(appContext).build()
    private val _state = MutableStateFlow(ReelsPlayerState(isMuted = userMuted))
    val state: StateFlow<ReelsPlayerState> = _state

    var onEnded: (() -> Unit)? = null
    var onError: ((Throwable?) -> Unit)? = null
    var onBufferingChanged: ((Boolean) -> Unit)? = null
    var onStarted: (() -> Unit)? = null
    var onPaused: (() -> Unit)? = null

    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val mapped = when (playbackState) {
                Player.STATE_BUFFERING -> ReelsPlaybackState.Buffering
                Player.STATE_READY -> if (player.playWhenReady) ReelsPlaybackState.Playing else ReelsPlaybackState.Ready
                Player.STATE_ENDED -> ReelsPlaybackState.Ended
                else -> ReelsPlaybackState.Idle
            }
            val buffering = playbackState == Player.STATE_BUFFERING
            _state.update {
                it.copy(
                    playbackState = mapped,
                    isBuffering = buffering,
                    isLoading = playbackState == Player.STATE_BUFFERING && !it.isFirstFrameRendered,
                    durationMs = player.duration.safeDuration(),
                    error = null
                )
            }
            if (playbackState == Player.STATE_ENDED) onEnded?.invoke()
            onBufferingChanged?.invoke(buffering)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _state.update {
                it.copy(
                    isPlaying = isPlaying,
                    playbackState = if (isPlaying) ReelsPlaybackState.Playing else {
                        if (it.playbackState == ReelsPlaybackState.Ended) ReelsPlaybackState.Ended else ReelsPlaybackState.Paused
                    }
                )
            }
            if (isPlaying) onStarted?.invoke() else onPaused?.invoke()
        }

        override fun onRenderedFirstFrame() {
            _state.update { it.copy(isFirstFrameRendered = true, isLoading = false) }
        }

        override fun onPlayerError(error: PlaybackException) {
            _state.update {
                it.copy(
                    playbackState = ReelsPlaybackState.Error(error),
                    isPlaying = false,
                    isBuffering = false,
                    isLoading = false,
                    error = error
                )
            }
            onError?.invoke(error)
        }
    }

    init {
        player.addListener(listener)
        applyConfig(config)
    }

    fun applyConfig(newConfig: ReelsPlayerConfig) {
        config = newConfig
        mediaSourceFactory = ReelsMediaSourceFactory(appContext, config.cacheConfig)
        player.repeatMode = when (config.repeatMode) {
            ReelsRepeatMode.None -> Player.REPEAT_MODE_OFF
            ReelsRepeatMode.One -> Player.REPEAT_MODE_ONE
            ReelsRepeatMode.All -> Player.REPEAT_MODE_OFF
        }
        applyMute(userMuted)
    }

    fun load(index: Int, item: ReelItem, autoplay: Boolean) {
        currentIndex = index
        currentItem = item
        if (item.videoUrl.isBlank()) {
            val error = IllegalArgumentException("Reel videoUrl cannot be blank.")
            _state.value = ReelsPlayerState(
                currentIndex = index,
                currentItem = item,
                playbackState = ReelsPlaybackState.Error(error),
                isMuted = userMuted,
                error = error
            )
            onError?.invoke(error)
            return
        }
        _state.value = ReelsPlayerState(
            currentIndex = index,
            currentItem = item,
            playbackState = ReelsPlaybackState.Loading,
            isMuted = userMuted,
            isLoading = true
        )
        player.playWhenReady = autoplay
        player.setMediaSource(mediaSourceFactory.createMediaSource(item))
        player.prepare()
    }

    fun play() {
        player.playWhenReady = true
        player.play()
    }

    fun pause() {
        player.pause()
    }

    fun togglePlayPause() {
        if (player.isPlaying) pause() else play()
    }

    fun mute() = setMuted(true)

    fun unmute() {
        if (!config.muteConfig.forceMuted) setMuted(false)
    }

    fun toggleMute() {
        if (_state.value.isMuted) unmute() else mute()
    }

    private fun setMuted(muted: Boolean) {
        val nextMuted = muted || config.muteConfig.forceMuted
        if (config.muteConfig.rememberUserChoice) userMuted = nextMuted
        applyMute(nextMuted)
        config.muteConfig.onMuteChanged(nextMuted)
    }

    private fun applyMute(muted: Boolean) {
        val nextMuted = muted || config.muteConfig.forceMuted
        player.volume = if (nextMuted) 0f else 1f
        _state.update { it.copy(isMuted = nextMuted) }
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(max(0L, positionMs))
    }

    fun replay() {
        player.seekTo(0L)
        play()
    }

    fun setPlaybackSpeed(speed: Float) {
        val safeSpeed = speed.takeIf { it.isFinite() && it > 0f } ?: 1f
        player.playbackParameters = PlaybackParameters(safeSpeed)
        _state.update { it.copy(playbackSpeed = safeSpeed) }
    }

    fun retry() {
        val item = currentItem ?: return
        load(currentIndex, item, config.autoplay)
    }

    fun updateProgress() {
        val duration = player.duration.safeDuration()
        val position = player.currentPosition.coerceAtLeast(0L)
        _state.update {
            it.copy(
                currentPositionMs = position,
                durationMs = duration,
                progress = if (duration > 0L) (position.toFloat() / duration).coerceIn(0f, 1f) else 0f
            )
        }
    }

    fun release() {
        player.removeListener(listener)
        player.release()
    }

    private fun Long.safeDuration(): Long = if (this == C.TIME_UNSET || this < 0L) 0L else this
}
