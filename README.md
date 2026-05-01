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

## Project Structure

```text
ComposeReelsPlayer/
├── app/                    # Sample Android app
└── compose-reels-player/   # Reusable library module
```

## Requirements

- Android min SDK 24+
- Jetpack Compose
- Kotlin 2.2.21
- Android Gradle Plugin 9.0.1
- Media3 1.5.1

The library declares its Compose, Lifecycle, Coil, and Media3 dependencies through the Gradle version catalog in this repository.

## Installation

### From JitPack

After this repository is pushed to GitHub and tagged, other projects can install the library through JitPack.

Add JitPack to the consuming project's `settings.gradle.kts`:

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

Then add the dependency:

```kotlin
dependencies {
    implementation("com.github.mathewGlenn.ComposeReelsPlayer:compose-reels-player:0.1.0")
}
```

For a Git tag named `v0.1.0`, use:

```kotlin
dependencies {
    implementation("com.github.mathewGlenn.ComposeReelsPlayer:compose-reels-player:v0.1.0")
}
```

### From This Repository

For local development in this repository, add the module to your app:

```kotlin
dependencies {
    implementation(project(":compose-reels-player"))
}
```

If you copy the module into another project, include it in `settings.gradle.kts`:

```kotlin
include(":compose-reels-player")
```

Then add the dependency from your app module.

## Permissions

Your app should declare internet access for remote videos:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

The library module declares `ACCESS_NETWORK_STATE` so it can support network-aware preloading behavior.

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

Use the `overlay` slot for app-specific UI. The library intentionally does not own social actions, profile navigation, comments, backend writes, or database logic.

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

Use stable `ReelItem.id` values or provide a custom `cacheKeyProvider` when video URLs contain expiring query parameters.

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

## Run The Sample

From the repository root:

```powershell
.\gradlew.bat :app:installDebug
```

Or open the project in Android Studio and run the `app` configuration.

## Build And Test

```powershell
.\gradlew.bat :compose-reels-player:assemble
.\gradlew.bat :compose-reels-player:test
```

## Publish

This project is configured with Gradle `maven-publish` for the `compose-reels-player` release variant.

Publish to your local Maven cache:

```powershell
.\gradlew.bat :compose-reels-player:publishToMavenLocal
```

Create and push a GitHub release tag for JitPack:

```powershell
git tag v0.1.0
git push origin v0.1.0
```

Then open [JitPack for ComposeReelsPlayer](https://jitpack.io/#mathewGlenn/ComposeReelsPlayer) and request the `v0.1.0` build.

## Notes

- The default overlay is intentionally minimal. Replace it with your product UI for likes, comments, sharing, captions, profile actions, and moderation states.
- Preloading keeps a bounded set of muted prepared players nearby. Avoid large `aheadCount` or `behindCount` values on memory-constrained devices.
- `ReelItem.headers` is passed into media requests, which is useful for authenticated or signed media endpoints.
- Subtitle metadata is modeled with `ReelSubtitle`, but subtitle styling and selection UI are left to the host app.
