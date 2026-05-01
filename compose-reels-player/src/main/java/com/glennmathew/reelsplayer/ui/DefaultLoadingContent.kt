package com.glennmathew.reelsplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.glennmathew.reelsplayer.model.ReelItem

@Composable
fun BoxScope.DefaultLoadingContent(item: ReelItem) {
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        item.thumbnailUrl?.let { thumbnail ->
            AsyncImage(
                model = thumbnail,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
    }
}
