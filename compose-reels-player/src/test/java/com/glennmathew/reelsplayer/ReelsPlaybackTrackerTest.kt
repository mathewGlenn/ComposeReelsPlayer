package com.glennmathew.reelsplayer

import com.glennmathew.reelsplayer.config.ReelsCacheConfig
import com.glennmathew.reelsplayer.config.ReelsMuteConfig
import com.glennmathew.reelsplayer.model.ReelItem
import com.glennmathew.reelsplayer.model.WatchThreshold
import com.glennmathew.reelsplayer.player.ReelsPlaybackTracker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReelsPlaybackTrackerTest {
    @Test
    fun trackerEmitsThresholdsOnlyOnce() {
        val tracker = ReelsPlaybackTracker()

        tracker.addWatchTime(5_000L)

        val first = tracker.collectNewThresholds(durationMs = 10_000L)
        val second = tracker.collectNewThresholds(durationMs = 10_000L)

        assertTrue(WatchThreshold.ThreeSeconds in first)
        assertTrue(WatchThreshold.TwentyFivePercent in first)
        assertTrue(WatchThreshold.FiftyPercent in first)
        assertTrue(second.isEmpty())
    }

    @Test
    fun trackerResetAllowsThresholdsForNewSession() {
        val tracker = ReelsPlaybackTracker()

        tracker.addWatchTime(3_000L)
        tracker.collectNewThresholds(durationMs = 20_000L)
        tracker.reset()
        tracker.addWatchTime(3_000L)

        assertEquals(listOf(WatchThreshold.ThreeSeconds), tracker.collectNewThresholds(durationMs = 20_000L))
    }

    @Test
    fun cacheKeyDefaultsToStableItemId() {
        val item = ReelItem(id = "reel-42", videoUrl = "https://example.com/video.m3u8")

        assertEquals("reel-42", ReelsCacheConfig().cacheKeyProvider(item))
    }

    @Test
    fun forceMutedMeansInitiallyMutedSession() {
        val forceMuted = ReelsMuteConfig(initiallyMuted = false, forceMuted = true)

        assertFalse(forceMuted.initiallyMuted)
        assertTrue(forceMuted.forceMuted)
    }
}
