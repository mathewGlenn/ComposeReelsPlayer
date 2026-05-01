package com.glennmathew.reelsplayer.config

import com.glennmathew.reelsplayer.model.ReelsRepeatMode
import com.glennmathew.reelsplayer.model.ReelsResizeMode

data class ReelsPlayerConfig(
    val gestureConfig: ReelsGestureConfig = ReelsGestureConfig(),
    val preloadConfig: ReelsPreloadConfig = ReelsPreloadConfig(),
    val cacheConfig: ReelsCacheConfig = ReelsCacheConfig(),
    val muteConfig: ReelsMuteConfig = ReelsMuteConfig(),
    val resizeMode: ReelsResizeMode = ReelsResizeMode.Crop,
    val repeatMode: ReelsRepeatMode = ReelsRepeatMode.One,
    val showProgressBar: Boolean = true,
    val autoplay: Boolean = true,
    val pauseWhenDragging: Boolean = true,
    val playWhenPageSettled: Boolean = true,
    val loadMoreThreshold: Int = 3
)
