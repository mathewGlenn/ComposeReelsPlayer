package com.glennmathew.reelsplayer

interface ReelsPlayerActions {
    fun play()
    fun pause()
    fun togglePlayPause()
    fun mute()
    fun unmute()
    fun toggleMute()
    fun seekTo(positionMs: Long)
    fun replay()
    fun next()
    fun previous()
    fun setPlaybackSpeed(speed: Float)
    fun retry()
}
