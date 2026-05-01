package com.glennmathew.reelsplayer.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.glennmathew.reelsplayer.ReelsPlayerActions
import com.glennmathew.reelsplayer.ReelsPlayerState
import com.glennmathew.reelsplayer.model.ReelItem

@Composable
fun BoxScope.DefaultReelsOverlay(
    item: ReelItem,
    state: ReelsPlayerState,
    actions: ReelsPlayerActions
) {
    if (!state.isPlaying) {
        Text(
            text = "Play",
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
    IconButton(
        onClick = actions::toggleMute,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp)
            .size(48.dp)
            .semantics {
                contentDescription = if (state.isMuted) "Unmute video" else "Mute video"
                role = Role.Button
            }
    ) {
        Surface(color = Color.Black.copy(alpha = 0.45f), shape = androidx.compose.foundation.shape.CircleShape) {
            Text(
                text = if (state.isMuted) "Mute" else "Sound",
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}
