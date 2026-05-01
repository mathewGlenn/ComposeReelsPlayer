package com.glennmathew.reelsplayer.config

import com.glennmathew.reelsplayer.model.ReelItem

data class ReelsCacheConfig(
    val enabled: Boolean = true,
    val maxCacheSizeMb: Long = 300L,
    val cacheDirectoryName: String = "reels_player_cache",
    val cacheKeyProvider: (ReelItem) -> String = { it.id }
)
