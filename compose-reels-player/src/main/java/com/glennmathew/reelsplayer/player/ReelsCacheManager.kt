package com.glennmathew.reelsplayer.player

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import com.glennmathew.reelsplayer.config.ReelsCacheConfig
import java.io.File

@UnstableApi
internal object ReelsCacheManager {
    private val lock = Any()
    private var cache: SimpleCache? = null
    private var cacheKey: String? = null

    fun get(context: Context, config: ReelsCacheConfig): SimpleCache? {
        if (!config.enabled) return null
        val appContext = context.applicationContext
        val requestedKey = "${config.cacheDirectoryName}:${config.maxCacheSizeMb}"
        synchronized(lock) {
            cache?.let { existing ->
                if (cacheKey == requestedKey) return existing
            }
            val directory = File(appContext.cacheDir, config.cacheDirectoryName)
            val evictor = LeastRecentlyUsedCacheEvictor(config.maxCacheSizeMb * 1024L * 1024L)
            val databaseProvider = StandaloneDatabaseProvider(appContext)
            return SimpleCache(directory, evictor, databaseProvider).also {
                cache = it
                cacheKey = requestedKey
            }
        }
    }

    fun clear(context: Context, config: ReelsCacheConfig = ReelsCacheConfig()) {
        synchronized(lock) {
            cache?.release()
            cache = null
            cacheKey = null
        }
        File(context.applicationContext.cacheDir, config.cacheDirectoryName).deleteRecursively()
    }
}
