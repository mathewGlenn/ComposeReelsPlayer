package com.glennmathew.reelsplayer.player

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import com.glennmathew.reelsplayer.config.ReelsCacheConfig
import com.glennmathew.reelsplayer.model.ReelItem

@UnstableApi
internal class ReelsMediaSourceFactory(
    context: Context,
    private val cacheConfig: ReelsCacheConfig
) {
    private val appContext = context.applicationContext

    fun createMediaSource(item: ReelItem): MediaSource {
        val httpFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setDefaultRequestProperties(item.headers)
        val upstreamFactory = DefaultDataSource.Factory(appContext, httpFactory)
        val dataSourceFactory = ReelsCacheManager.get(appContext, cacheConfig)?.let { cache ->
            CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(upstreamFactory)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        } ?: upstreamFactory

        return DefaultMediaSourceFactory(dataSourceFactory).createMediaSource(createMediaItem(item))
    }

    private fun createMediaItem(item: ReelItem): MediaItem {
        val subtitles = item.subtitles.map { subtitle ->
            MediaItem.SubtitleConfiguration.Builder(subtitle.url.toUri())
                .setMimeType(subtitle.mimeType.ifBlank { MimeTypes.TEXT_VTT })
                .setLanguage(subtitle.language)
                .setLabel(subtitle.label)
                .build()
        }
        return MediaItem.Builder()
            .setUri(item.videoUrl)
            .setCustomCacheKey(cacheConfig.cacheKeyProvider(item))
            .setSubtitleConfigurations(subtitles)
            .build()
    }
}
