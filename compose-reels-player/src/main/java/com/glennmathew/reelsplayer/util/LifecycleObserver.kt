package com.glennmathew.reelsplayer.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

internal class ReelsLifecycleObserver(
    private val onPause: () -> Unit,
    private val onStop: () -> Unit,
    private val onResume: () -> Unit,
    private val onDestroy: () -> Unit
) : DefaultLifecycleObserver {
    override fun onPause(owner: LifecycleOwner) = onPause()
    override fun onStop(owner: LifecycleOwner) = onStop()
    override fun onResume(owner: LifecycleOwner) = onResume()
    override fun onDestroy(owner: LifecycleOwner) = onDestroy()
}
