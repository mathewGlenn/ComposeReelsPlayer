package com.glennmathew.reelsplayer.model

sealed interface ReelsPlaybackState {
    data object Idle : ReelsPlaybackState
    data object Loading : ReelsPlaybackState
    data object Ready : ReelsPlaybackState
    data object Playing : ReelsPlaybackState
    data object Paused : ReelsPlaybackState
    data object Buffering : ReelsPlaybackState
    data object Ended : ReelsPlaybackState
    data class Error(val throwable: Throwable?) : ReelsPlaybackState
}
