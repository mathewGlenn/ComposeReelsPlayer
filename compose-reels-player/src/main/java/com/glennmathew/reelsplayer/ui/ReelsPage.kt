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
internal fun <T> ReelsPage(
    item: T,
    reelItem: ReelItem,
    state: ReelsPlayerState,
    isActivePage: Boolean,
    showOverlay: Boolean,
    player: Player?,
    actions: ReelsPlayerActions,
    config: ReelsPlayerConfig,
    modifier: Modifier = Modifier,
    overlay: @Composable BoxScope.(T, ReelsPlayerState, ReelsPlayerActions) -> Unit,
    loadingContent: @Composable BoxScope.(T) -> Unit,
    errorContent: @Composable BoxScope.(T, Throwable?, () -> Unit) -> Unit
) {
    Box(modifier.fillMaxSize().background(Color.Black)) {
        if (isActivePage) {
            ReelsVideoSurface(
                player = player,
                resizeMode = config.resizeMode,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            ReelsThumbnail(item = reelItem)
        }

        if (isActivePage && (!state.isFirstFrameRendered || state.isLoading)) {
            loadingContent(item)
        }

        if (isActivePage && state.playbackState is ReelsPlaybackState.Error) {
            errorContent(item, state.error, actions::retry)
        }

        if (showOverlay) {
            overlay(item, state, actions)
        }

        if (isActivePage && config.showPlaybackControls) {
            DefaultReelsOverlay(
                item = reelItem,
                state = state,
                actions = actions
            )
        }

        if (isActivePage && config.showProgressBar) {
            ReelsProgressBar(
                progress = state.progress,
                modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding()
            )
        }
    }
}
