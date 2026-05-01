package com.glennmathew.reelsplayer.model

data class ReelItem(
    val id: String,
    val videoUrl: String,
    val thumbnailUrl: String? = null,
    val headers: Map<String, String> = emptyMap(),
    val title: String? = null,
    val metadata: Any? = null,
    val subtitles: List<ReelSubtitle> = emptyList()
)

data class ReelSubtitle(
    val url: String,
    val mimeType: String,
    val language: String? = null,
    val label: String? = null
)
