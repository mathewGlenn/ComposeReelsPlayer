package com.glennmathew.reelsplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.glennmathew.reelsplayer.model.ReelItem

@Composable
fun BoxScope.DefaultErrorContent(
    item: ReelItem,
    error: Throwable?,
    onRetry: () -> Unit
) {
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        item.thumbnailUrl?.let { thumbnail ->
            AsyncImage(
                model = thumbnail,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Column(
            modifier = Modifier.align(Alignment.Center).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Video could not be played", color = Color.White)
            error?.message?.takeIf { it.isNotBlank() }?.let {
                Text(text = it, color = Color.White.copy(alpha = 0.75f))
            }
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}
