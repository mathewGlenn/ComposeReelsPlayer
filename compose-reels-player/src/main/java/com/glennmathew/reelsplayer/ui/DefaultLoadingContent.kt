package com.glennmathew.reelsplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.glennmathew.reelsplayer.model.ReelItem
import kotlinx.coroutines.delay

@Composable
fun BoxScope.DefaultLoadingContent(item: ReelItem) {
    var showSpinner by remember(item.id) { mutableStateOf(false) }

    LaunchedEffect(item.id) {
        delay(450L)
        showSpinner = true
    }

    Box(Modifier.fillMaxSize()) {
        ReelsThumbnail(item = item)
        if (showSpinner) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        }
    }
}
