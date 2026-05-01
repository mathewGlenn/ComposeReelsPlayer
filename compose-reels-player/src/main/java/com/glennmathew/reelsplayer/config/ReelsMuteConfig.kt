package com.glennmathew.reelsplayer.config

data class ReelsMuteConfig(
    val initiallyMuted: Boolean = true,
    val rememberUserChoice: Boolean = true,
    val forceMuted: Boolean = false,
    val onMuteChanged: (Boolean) -> Unit = {}
)
