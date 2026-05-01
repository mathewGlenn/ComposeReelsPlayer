package com.glennmathew.reelsplayer.config

data class ReelsPreloadConfig(
    val enabled: Boolean = true,
    val aheadCount: Int = 2,
    val behindCount: Int = 1,
    val preloadDurationMs: Long = 5_000L,
    val preloadOnMobileData: Boolean = true,
    val preloadOnWifiOnly: Boolean = false
)
