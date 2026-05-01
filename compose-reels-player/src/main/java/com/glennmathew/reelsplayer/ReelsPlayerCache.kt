package com.glennmathew.reelsplayer

import android.content.Context
import androidx.media3.common.util.UnstableApi
import com.glennmathew.reelsplayer.config.ReelsCacheConfig
import com.glennmathew.reelsplayer.player.ReelsCacheManager

object ReelsPlayerCache {
    @UnstableApi
    fun clear(context: Context, config: ReelsCacheConfig = ReelsCacheConfig()) {
        ReelsCacheManager.clear(context, config)
    }
}
