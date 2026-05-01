package com.glennmathew.reelsplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.glennmathew.reelsplayer.ReelsPlayerActions
import com.glennmathew.reelsplayer.ReelsPlayerState
import com.glennmathew.reelsplayer.config.ReelsPlayerConfig
import com.glennmathew.reelsplayer.model.ReelItem
import com.glennmathew.reelsplayer.model.ReelsPlaybackState

@Composable
@UnstableApi
internal fun ReelsPage(
    item: ReelItem,
    state: ReelsPlayerState,
    player: Player?,
    actions: ReelsPlayerActions,
    config: ReelsPlayerConfig,
    modifier: Modifier = Modifier,
    overlay: @Composable BoxScope.(ReelItem, ReelsPlayerState, ReelsPlayerActions) -> Unit,
    loadingContent: @Composable BoxScope.(ReelItem) -> Unit,
    errorContent: @Composable BoxScope.(ReelItem, Throwable?, () -> Unit) -> Unit
) {
    Box(modifier.fillMaxSize().background(Color.Black)) {
        ReelsVideoSurface(
            player = player,
            resizeMode = config.resizeMode,
            modifier = Modifier.fillMaxSize()
        )

        if (state.currentItem?.id == item.id && (!state.isFirstFrameRendered || state.isLoading)) {
            loadingContent(item)
        }

        if (state.currentItem?.id == item.id && state.playbackState is ReelsPlaybackState.Error) {
            errorContent(item, state.error, actions::retry)
        }

        overlay(item, state, actions)

        if (config.showProgressBar) {
            ReelsProgressBar(
                progress = state.progress,
                modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding()
            )
        }
    }
}
