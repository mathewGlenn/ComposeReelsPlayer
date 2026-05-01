package com.glennmathew.reelsplayer.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.glennmathew.reelsplayer.ReelsPlayerActions
import com.glennmathew.reelsplayer.ReelsPlayerState
import com.glennmathew.reelsplayer.model.ReelItem
import com.glennmathew.reelsplayer.model.ReelsPlaybackState
import kotlin.math.abs

@Composable
fun BoxScope.DefaultReelsOverlay(
    item: ReelItem,
    state: ReelsPlayerState,
    actions: ReelsPlayerActions
) {
    if (state.playbackSpeed.isFastForwarding()) {
        FastForwardIndicator(
            speed = state.playbackSpeed,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 16.dp)
        )
    }

    if (state.shouldShowPausedControls()) {
        PausedControls(
            state = state,
            actions = actions,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

private fun ReelsPlayerState.shouldShowPausedControls(): Boolean {
    val pauseLikeState = playbackState == ReelsPlaybackState.Paused ||
        playbackState == ReelsPlaybackState.Ready ||
        playbackState == ReelsPlaybackState.Ended

    return pauseLikeState &&
        !isPlaying &&
        !isDragging &&
        !isPausedByDragging &&
        !isLoading &&
        !isBuffering &&
        isFirstFrameRendered
}

@Composable
private fun PausedControls(
    state: ReelsPlayerState,
    actions: ReelsPlayerActions,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(width = 96.dp, height = 148.dp), contentAlignment = Alignment.Center) {
        IconButton(
            onClick = actions::toggleMute,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(44.dp)
                .background(Color.Black.copy(alpha = 0.42f), CircleShape)
                .semantics {
                    contentDescription = if (state.isMuted) "Unmute video" else "Mute video"
                    role = Role.Button
                }
        ) {
            MuteIcon(
                isMuted = state.isMuted,
                modifier = Modifier.size(24.dp),
                color = Color.White
            )
        }

        IconButton(
            onClick = actions::play,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(84.dp)
                .background(Color.Black.copy(alpha = 0.46f), CircleShape)
                .semantics {
                    contentDescription = "Play video"
                    role = Role.Button
                }
        ) {
            PlayIcon(
                modifier = Modifier.size(46.dp),
                color = Color.White
            )
        }
    }
}

@Composable
private fun FastForwardIndicator(
    speed: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.52f), RoundedCornerShape(999.dp))
            .padding(horizontal = 14.dp, vertical = 9.dp)
            .semantics {
                contentDescription = "${speed.formatSpeed()} speed"
            },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FastForwardIcon(modifier = Modifier.size(22.dp), color = Color.White)
        Text(
            text = "${speed.formatSpeed()} speed",
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PlayIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(size.width * 0.32f, size.height * 0.22f)
            lineTo(size.width * 0.32f, size.height * 0.78f)
            lineTo(size.width * 0.78f, size.height * 0.50f)
            close()
        }
        drawPath(path = path, color = color)
    }
}

@Composable
private fun FastForwardIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val first = Path().apply {
            moveTo(size.width * 0.12f, size.height * 0.20f)
            lineTo(size.width * 0.12f, size.height * 0.80f)
            lineTo(size.width * 0.48f, size.height * 0.50f)
            close()
        }
        val second = Path().apply {
            moveTo(size.width * 0.48f, size.height * 0.20f)
            lineTo(size.width * 0.48f, size.height * 0.80f)
            lineTo(size.width * 0.84f, size.height * 0.50f)
            close()
        }
        drawPath(first, color)
        drawPath(second, color)
    }
}

@Composable
private fun MuteIcon(
    isMuted: Boolean,
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val speaker = Path().apply {
            moveTo(size.width * 0.14f, size.height * 0.40f)
            lineTo(size.width * 0.34f, size.height * 0.40f)
            lineTo(size.width * 0.58f, size.height * 0.20f)
            lineTo(size.width * 0.58f, size.height * 0.80f)
            lineTo(size.width * 0.34f, size.height * 0.60f)
            lineTo(size.width * 0.14f, size.height * 0.60f)
            close()
        }
        drawPath(speaker, color)

        if (isMuted) {
            drawLine(
                color = color,
                start = Offset(size.width * 0.70f, size.height * 0.36f),
                end = Offset(size.width * 0.92f, size.height * 0.64f),
                strokeWidth = size.minDimension * 0.08f
            )
            drawLine(
                color = color,
                start = Offset(size.width * 0.92f, size.height * 0.36f),
                end = Offset(size.width * 0.70f, size.height * 0.64f),
                strokeWidth = size.minDimension * 0.08f
            )
        } else {
            drawArc(
                color = color,
                startAngle = -38f,
                sweepAngle = 76f,
                useCenter = false,
                topLeft = Offset(size.width * 0.58f, size.height * 0.30f),
                size = Size(size.width * 0.26f, size.height * 0.40f),
                style = Stroke(width = size.minDimension * 0.06f)
            )
            drawArc(
                color = color,
                startAngle = -42f,
                sweepAngle = 84f,
                useCenter = false,
                topLeft = Offset(size.width * 0.54f, size.height * 0.20f),
                size = Size(size.width * 0.42f, size.height * 0.60f),
                style = Stroke(width = size.minDimension * 0.05f)
            )
        }
    }
}

private fun Float.isFastForwarding(): Boolean = abs(this - 1f) > 0.01f

private fun Float.formatSpeed(): String {
    val rounded = (this * 10f).toInt() / 10f
    return if (abs(rounded - rounded.toInt()) < 0.01f) {
        "${rounded.toInt()}x"
    } else {
        "${rounded}x"
    }
}
