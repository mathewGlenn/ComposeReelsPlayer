# Compose Reels Player

A reusable Android library for building vertical short-form video feeds with Kotlin, Jetpack Compose, and Media3 ExoPlayer.

`compose-reels-player` gives you a full-screen reels-style pager with one active player, loading thumbnails, retryable errors, custom overlays, lifecycle-aware pause/resume, mute controls, gesture hooks, watch-time analytics, bounded preloading, and Media3 cache support.

## Features

- Vertical Compose pager for reels, shorts, stories, and clip feeds
- Media3 ExoPlayer playback with MP4, HLS, and DASH support
- One active player for the visible reel, plus bounded nearby preloading
- Thumbnail loading UI powered by Coil
- Retryable default error UI with custom error content support
- Custom overlay slot for app-specific actions such as like, comment, share, follow, or profile UI
- Controller API for play, pause, mute, seek, replay, next, previous, and page navigation
- Single-tap, double-tap, and long-press gesture customization
- Lifecycle-aware playback pause/resume
- Analytics callbacks for impressions, starts, pauses, completions, skips, buffering, watch time, mute events, and errors
- Optional Media3 cache with configurable cache size and cache keys

## Requirements

- Android min SDK 24+
- Jetpack Compose
- Kotlin
- Media3

The library brings its Compose, Lifecycle, Coil, and Media3 dependencies transitively.

## Installation

Add JitPack to your project's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

Add the library dependency to your app module:

```kotlin
dependencies {
    implementation("com.github.mathewGlenn:ComposeReelsPlayer:v0.1.2")
}
```

## Permissions

Declare internet access in your app for remote videos:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Basic Usage

```kotlin
import androidx.compose.runtime.Composable
import com.glennmathew.reelsplayer.ReelsPlayer
import com.glennmathew.reelsplayer.model.ReelItem
import com.glennmathew.reelsplayer.rememberReelsPlayerController

@Composable
fun ReelsScreen() {
    val controller = rememberReelsPlayerController()

    val reels = listOf(
        ReelItem(
            id = "reel-1",
            videoUrl = "https://example.com/video-1.mp4",
            thumbnailUrl = "https://example.com/video-1.jpg",
            title = "First reel"
        ),
        ReelItem(
            id = "reel-2",
            videoUrl = "https://example.com/video-2.m3u8",
            thumbnailUrl = "https://example.com/video-2.jpg"
        )
    )

    ReelsPlayer(
        items = reels,
        controller = controller,
        onCurrentReelChanged = { index, item ->
            println("Current reel: $index ${item.id}")
        },
        onAnalyticsEvent = { event ->
            println("Reels analytics: $event")
        },
        onLoadMore = {
            println("Load more reels")
        }
    )
}
```

## Custom Overlay

Use the `overlay` slot for app-specific UI. The library intentionally does not own social actions, profile navigation, comments, backend writes, or database logic. Playback affordances such as the paused play button, mute button, fast-forward indicator, and progress bar stay library-owned by default, even when you provide a custom overlay.

```kotlin
ReelsPlayer(
    items = reels,
    overlay = { item, state, actions ->
        Box(Modifier.fillMaxSize()) {
            Text(
                text = item.title.orEmpty(),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )

            Button(
                onClick = actions::toggleMute,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Text(if (state.isMuted) "Unmute" else "Mute")
            }
        }
    }
)
```

You can also keep your own item model and map only the media fields the player needs. The overlay receives your original type.

```kotlin
import com.glennmathew.reelsplayer.model.ReelsMediaSource

data class FeedPost(
    val postId: String,
    val playbackUrl: String,
    val thumbnailUrl: String?,
    val authorName: String,
    val caption: String,
    val likes: Int
)

ReelsPlayer(
    items = posts,
    mediaSource = { post ->
        ReelsMediaSource.Video(
            id = post.postId,
            videoUrl = post.playbackUrl,
            thumbnailUrl = post.thumbnailUrl
        )
    },
    overlay = { post, state, actions ->
        Text(
            text = "${post.authorName}: ${post.caption}",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
    }
)
```

## Configuration

```kotlin
import com.glennmathew.reelsplayer.config.ReelsCacheConfig
import com.glennmathew.reelsplayer.config.ReelsGestureConfig
import com.glennmathew.reelsplayer.config.ReelsMuteConfig
import com.glennmathew.reelsplayer.config.ReelsPlayerConfig
import com.glennmathew.reelsplayer.config.ReelsPreloadConfig

ReelsPlayer(
    items = reels,
    config = ReelsPlayerConfig(
        autoplay = true,
        showProgressBar = true,
        showPlaybackControls = true,
        loadMoreThreshold = 3,
        muteConfig = ReelsMuteConfig(initiallyMuted = true),
        preloadConfig = ReelsPreloadConfig(
            enabled = true,
            aheadCount = 2,
            behindCount = 1,
            preloadOnWifiOnly = false
        ),
        cacheConfig = ReelsCacheConfig(
            enabled = true,
            maxCacheSizeMb = 300,
            cacheKeyProvider = { item -> item.id }
        ),
        gestureConfig = ReelsGestureConfig(
            longPressPlaybackSpeed = 2f,
            onDoubleTap = { item, offset, actions ->
                // Trigger your like animation at offset.
            }
        )
    )
)
```

## Controller

`rememberReelsPlayerController()` exposes playback and navigation commands:

```kotlin
val controller = rememberReelsPlayerController()

ReelsPlayer(
    items = reels,
    controller = controller
)

controller.pause()
controller.play()
controller.toggleMute()
controller.seekTo(10_000L)
controller.replay()
controller.next()
controller.previous()
controller.animateScrollTo(3)
```

This is useful before opening comment sheets, navigating away, showing dialogs, or syncing playback with host UI.

## Loading And Error UI

```kotlin
ReelsPlayer(
    items = reels,
    loadingContent = { item ->
        MyLoadingContent(item)
    },
    errorContent = { item, error, retry ->
        MyErrorContent(
            item = item,
            error = error,
            onRetry = retry
        )
    }
)
```

## Cache

Caching is enabled by default through `ReelsCacheConfig`. You can clear the shared player cache with:

```kotlin
ReelsPlayerCache.clear(context)
```

Use stable `ReelItem.id` values, or stable `ReelsMediaSource.Video.id` values when using a custom item model. Provide a custom `cacheKeyProvider` when video URLs contain expiring query parameters.

## Analytics

Handle `onAnalyticsEvent` to collect feed and playback signals:

```kotlin
ReelsPlayer(
    items = reels,
    onAnalyticsEvent = { event ->
        when (event) {
            is ReelsAnalyticsEvent.ReelImpression -> Unit
            is ReelsAnalyticsEvent.VideoStarted -> Unit
            is ReelsAnalyticsEvent.VideoCompleted -> Unit
            is ReelsAnalyticsEvent.PlaybackError -> Unit
            else -> Unit
        }
    }
)
```

Events include impressions, playback starts, pauses, completions, replays, skips, watch time updates, watch thresholds, buffering, mute changes, and playback errors.

## Supported Formats

Media3-supported sources are supported, including MP4, HLS `.m3u8`, and DASH `.mpd`.

## Notes

- The custom overlay slot is intentionally for product UI such as likes, comments, sharing, captions, profile actions, and moderation states. Set `showPlaybackControls = false` only if the host app wants to replace the library-owned paused, mute, and fast-forward indicators too.
- Preloading keeps a bounded set of muted prepared players nearby. Avoid large `aheadCount` or `behindCount` values on memory-constrained devices.
- `ReelItem.headers` and `ReelsMediaSource.Video.headers` are passed into media requests, which is useful for authenticated or signed media endpoints.
- Subtitle metadata is modeled with `ReelSubtitle`, but subtitle styling and selection UI are left to the host app.
