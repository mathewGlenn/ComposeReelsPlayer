package com.glennmathew.reelsplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.glennmathew.reelsplayer.model.ReelItem

@Composable
internal fun ReelsThumbnail(
    item: ReelItem,
    modifier: Modifier = Modifier
) {
    Box(modifier.fillMaxSize().background(Color.Black)) {
        item.thumbnailUrl?.let { thumbnail ->
            AsyncImage(
                model = thumbnail,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
