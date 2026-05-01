package com.glennmathew.reelsplayer.player

import com.glennmathew.reelsplayer.model.WatchThreshold

class ReelsPlaybackTracker {
    private val emitted = mutableSetOf<WatchThreshold>()
    private var watchedMs: Long = 0L

    fun reset() {
        emitted.clear()
        watchedMs = 0L
    }

    fun addWatchTime(deltaMs: Long): Long {
        if (deltaMs > 0) watchedMs += deltaMs
        return watchedMs
    }

    fun collectNewThresholds(durationMs: Long): List<WatchThreshold> {
        val thresholds = buildList {
            if (watchedMs >= 3_000L) add(WatchThreshold.ThreeSeconds)
            if (durationMs > 0L) {
                val ratio = watchedMs.toFloat() / durationMs.toFloat()
                if (ratio >= 0.25f) add(WatchThreshold.TwentyFivePercent)
                if (ratio >= 0.50f) add(WatchThreshold.FiftyPercent)
                if (ratio >= 0.75f) add(WatchThreshold.SeventyFivePercent)
                if (ratio >= 1f) add(WatchThreshold.OneHundredPercent)
            }
        }
        return thresholds.filter { emitted.add(it) }
    }
}
