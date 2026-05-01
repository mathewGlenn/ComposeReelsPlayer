package com.glennmathew.reelsplayer.model

sealed interface ReelsMediaSource {
    val id: String
    val videoUrl: String
    val thumbnailUrl: String?
    val headers: Map<String, String>
    val subtitles: List<ReelSubtitle>

    data class Video(
        override val videoUrl: String,
        override val thumbnailUrl: String? = null,
        override val id: String = videoUrl,
        override val headers: Map<String, String> = emptyMap(),
        override val subtitles: List<ReelSubtitle> = emptyList()
    ) : ReelsMediaSource
}

internal fun ReelsMediaSource.toReelItem(): ReelItem {
    return ReelItem(
        id = id,
        videoUrl = videoUrl,
        thumbnailUrl = thumbnailUrl,
        headers = headers,
        subtitles = subtitles
    )
}

internal fun ReelItem.toReelsMediaSource(): ReelsMediaSource {
    return ReelsMediaSource.Video(
        id = id,
        videoUrl = videoUrl,
        thumbnailUrl = thumbnailUrl,
        headers = headers,
        subtitles = subtitles
    )
}
