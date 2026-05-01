package com.glennmathew.reelsplayer.model

sealed interface ReelsAnalyticsEvent {
    data class ReelImpression(val item: ReelItem, val index: Int) : ReelsAnalyticsEvent
    data class VideoStarted(val item: ReelItem, val index: Int) : ReelsAnalyticsEvent
    data class VideoPaused(val item: ReelItem, val index: Int, val positionMs: Long) : ReelsAnalyticsEvent
    data class VideoCompleted(val item: ReelItem, val index: Int) : ReelsAnalyticsEvent
    data class VideoReplayed(val item: ReelItem, val index: Int) : ReelsAnalyticsEvent
    data class VideoSkipped(val item: ReelItem, val fromIndex: Int, val toIndex: Int) : ReelsAnalyticsEvent
    data class WatchTimeUpdated(val item: ReelItem, val index: Int, val watchedMs: Long) : ReelsAnalyticsEvent
    data class WatchThresholdReached(val item: ReelItem, val index: Int, val threshold: WatchThreshold) : ReelsAnalyticsEvent
    data class BufferingStarted(val item: ReelItem, val index: Int) : ReelsAnalyticsEvent
    data class BufferingEnded(val item: ReelItem, val index: Int, val durationMs: Long) : ReelsAnalyticsEvent
    data class Muted(val item: ReelItem, val index: Int) : ReelsAnalyticsEvent
    data class Unmuted(val item: ReelItem, val index: Int) : ReelsAnalyticsEvent
    data class PlaybackError(val item: ReelItem, val index: Int, val error: Throwable?) : ReelsAnalyticsEvent
}

enum class WatchThreshold {
    ThreeSeconds,
    TwentyFivePercent,
    FiftyPercent,
    SeventyFivePercent,
    OneHundredPercent
}
