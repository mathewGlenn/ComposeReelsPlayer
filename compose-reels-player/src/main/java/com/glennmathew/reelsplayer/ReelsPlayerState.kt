package com.glennmathew.reelsplayer

import com.glennmathew.reelsplayer.model.ReelItem
import com.glennmathew.reelsplayer.model.ReelsPlaybackState

data class ReelsPlayerState(
    val currentIndex: Int = 0,
    val currentItem: ReelItem? = null,
    val playbackState: ReelsPlaybackState = ReelsPlaybackState.Idle,
    val isPlaying: Boolean = false,
    val isMuted: Boolean = true,
    val isBuffering: Boolean = false,
    val isDragging: Boolean = false,
    val isPausedByDragging: Boolean = false,
    val isLoading: Boolean = false,
    val isFirstFrameRendered: Boolean = false,
    val playbackSpeed: Float = 1f,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val progress: Float = 0f,
    val error: Throwable? = null
)
