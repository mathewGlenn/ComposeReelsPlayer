package com.glennmathew.reelsplayer.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.glennmathew.reelsplayer.config.ReelsPreloadConfig

internal object NetworkUtil {
    fun canPreload(context: Context, config: ReelsPreloadConfig): Boolean {
        if (!config.preloadOnWifiOnly && config.preloadOnMobileData) return true
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return !config.preloadOnWifiOnly
        val network = manager.activeNetwork ?: return false
        val capabilities = manager.getNetworkCapabilities(network) ?: return false
        val wifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val cellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        return when {
            config.preloadOnWifiOnly -> wifi
            cellular -> config.preloadOnMobileData
            else -> true
        }
    }
}
