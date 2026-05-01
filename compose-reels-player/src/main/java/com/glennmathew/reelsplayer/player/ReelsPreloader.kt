package com.glennmathew.reelsplayer.player

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.glennmathew.reelsplayer.config.ReelsPlayerConfig
import com.glennmathew.reelsplayer.model.ReelItem
import com.glennmathew.reelsplayer.util.NetworkUtil

@UnstableApi
internal class ReelsPreloader(
    context: Context,
    private var config: ReelsPlayerConfig
) {
    private val appContext = context.applicationContext
    private var mediaSourceFactory = ReelsMediaSourceFactory(appContext, config.cacheConfig)
    private val preparedPlayers = linkedMapOf<String, ExoPlayer>()

    fun updateConfig(newConfig: ReelsPlayerConfig) {
        config = newConfig
        mediaSourceFactory = ReelsMediaSourceFactory(appContext, config.cacheConfig)
    }

    fun preload(items: List<ReelItem>, currentIndex: Int) {
        val preloadConfig = config.preloadConfig
        if (!preloadConfig.enabled || !NetworkUtil.canPreload(appContext, preloadConfig)) {
            releaseAll()
            return
        }
        val first = (currentIndex - preloadConfig.behindCount).coerceAtLeast(0)
        val last = (currentIndex + preloadConfig.aheadCount).coerceAtMost(items.lastIndex)
        val wanted = (first..last)
            .filter { it != currentIndex }
            .mapNotNull { items.getOrNull(it) }
            .filter { it.videoUrl.isNotBlank() }
            .associateBy { config.cacheConfig.cacheKeyProvider(it) }

        preparedPlayers.keys.filterNot { it in wanted.keys }.forEach { key ->
            preparedPlayers.remove(key)?.release()
        }

        wanted.forEach { (key, item) ->
            if (preparedPlayers.containsKey(key)) return@forEach
            val player = ExoPlayer.Builder(appContext).build().apply {
                volume = 0f
                playWhenReady = false
                setMediaSource(mediaSourceFactory.createMediaSource(item))
                prepare()
            }
            preparedPlayers[key] = player
        }
    }

    fun releaseAll() {
        preparedPlayers.values.forEach { it.release() }
        preparedPlayers.clear()
    }
}
