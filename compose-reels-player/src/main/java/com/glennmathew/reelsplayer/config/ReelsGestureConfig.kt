package com.glennmathew.reelsplayer.config

import androidx.compose.ui.geometry.Offset
import com.glennmathew.reelsplayer.ReelsPlayerActions
import com.glennmathew.reelsplayer.model.ReelItem

data class ReelsGestureConfig(
    val singleTapEnabled: Boolean = true,
    val doubleTapEnabled: Boolean = true,
    val longPressEnabled: Boolean = true,
    val defaultSingleTapBehavior: Boolean = true,
    val defaultLongPressSpeedBehavior: Boolean = true,
    val longPressPlaybackSpeed: Float = 2f,
    val onSingleTap: ((item: ReelItem, actions: ReelsPlayerActions) -> Unit)? = null,
    val onDoubleTap: ((item: ReelItem, offset: Offset, actions: ReelsPlayerActions) -> Unit)? = null,
    val onLongPressStart: ((item: ReelItem, actions: ReelsPlayerActions) -> Unit)? = null,
    val onLongPressEnd: ((item: ReelItem, actions: ReelsPlayerActions) -> Unit)? = null
)
