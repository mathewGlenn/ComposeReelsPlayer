package com.glennmathew.reelsplayer.util

internal fun Long.elapsedSince(startMs: Long): Long = (this - startMs).coerceAtLeast(0L)
