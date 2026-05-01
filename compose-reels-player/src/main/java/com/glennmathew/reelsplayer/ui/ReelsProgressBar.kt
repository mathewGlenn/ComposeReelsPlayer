package com.glennmathew.reelsplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp

@Composable
fun ReelsProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    trackColor: Color = Color.White.copy(alpha = 0.25f),
    progressColor: Color = Color.White
) {
    Layout(
        modifier = modifier.fillMaxWidth().height(2.dp).background(trackColor),
        content = {
            Box(Modifier.height(2.dp).background(progressColor))
        }
    ) { measurables, constraints ->
        val width = (constraints.maxWidth * progress.coerceIn(0f, 1f)).toInt()
        val placeable = measurables.first().measure(constraints.copy(minWidth = width, maxWidth = width))
        layout(constraints.maxWidth, placeable.height) {
            placeable.place(0, 0)
        }
    }
}
