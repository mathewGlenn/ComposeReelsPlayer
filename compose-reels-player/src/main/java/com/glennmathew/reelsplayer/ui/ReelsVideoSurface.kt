package com.glennmathew.reelsplayer.ui

import androidx.annotation.OptIn
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.glennmathew.reelsplayer.model.ReelsResizeMode

@Composable
@OptIn(UnstableApi::class)
internal fun ReelsVideoSurface(
    player: Player?,
    resizeMode: ReelsResizeMode,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            PlayerView(context).apply {
                useController = false
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                this.resizeMode = resizeMode.toPlayerViewMode()
                this.player = player
            }
        },
        update = { view ->
            view.resizeMode = resizeMode.toPlayerViewMode()
            if (view.player !== player) view.player = player
        }
    )
}

@OptIn(UnstableApi::class)
private fun ReelsResizeMode.toPlayerViewMode(): Int = when (this) {
    ReelsResizeMode.Crop -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
    ReelsResizeMode.Fit -> AspectRatioFrameLayout.RESIZE_MODE_FIT
    ReelsResizeMode.Fill -> AspectRatioFrameLayout.RESIZE_MODE_FILL
    ReelsResizeMode.Zoom -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
}
