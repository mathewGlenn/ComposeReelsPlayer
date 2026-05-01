# Compose Reels Player

`compose-reels-player` is a reusable Android library for vertical short-form video feeds built with Kotlin, Jetpack Compose, and Media3 ExoPlayer. It provides a full-screen vertical pager, one active player, thumbnails while loading, retryable error UI, custom overlays, lifecycle-aware pause/resume, mute state, gestures, watch-time analytics, bounded preloading, and Media3 cache support.

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
    implementation("com.github.mathewGlenn:ComposeReelsPlayer:v0.1.0")
}
```

## Permissions

Declare internet access in your app for remote videos:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Basic Usage

```kotlin
@Composable
fun SampleReelsScreen() {
    val controller = rememberReelsPlayerController()
    val reels = listOf(
        ReelItem(
            id = "1",
            videoUrl = "https://example.com/video1.mp4",
            thumbnailUrl = "https://example.com/thumb1.jpg"
        ),
        ReelItem(
            id = "2",
            videoUrl = "https://example.com/video2.m3u8"
        )
    )

    ReelsPlayer(
        items = reels,
        controller = controller,
        onAnalyticsEvent = { event -> println(event) },
        onLoadMore = { println("Load more") }
    )
}
```

## Custom Overlay

```kotlin
ReelsPlayer(
    items = reels,
    overlay = { item, state, actions ->
        Box(Modifier.fillMaxSize()) {
            Text(
                text = item.title.orEmpty(),
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
            )
            Button(
                onClick = actions::toggleMute,
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
            ) {
                Text(if (state.isMuted) "Unmute" else "Mute")
            }
        }
    }
)
```

The library never owns like, comment, follow, profile, backend, or database logic. Put app-specific UI in the overlay.

## Gesture Customization

```kotlin
ReelsPlayer(
    items = reels,
    config = ReelsPlayerConfig(
        gestureConfig = ReelsGestureConfig(
            longPressPlaybackSpeed = 2.5f,
            onDoubleTap = { item, offset, actions ->
                // Trigger host like animation at offset.
            }
        )
    )
)
```

Single tap toggles play/pause by default. Long press temporarily changes speed to `longPressPlaybackSpeed` and restores `1f` on release unless disabled.

## Mute And Controller

```kotlin
val controller = rememberReelsPlayerController()

ReelsPlayer(
    items = reels,
    controller = controller,
    config = ReelsPlayerConfig(
        muteConfig = ReelsMuteConfig(initiallyMuted = true)
    )
)

// Pause before opening comments, dialogs, or navigating away.
controller.pause()
controller.toggleMute()
controller.animateScrollTo(3)
```

Use `forceMuted = true` when audio must stay off.

## Preload And Cache

```kotlin
ReelsPlayer(
    items = reels,
    config = ReelsPlayerConfig(
        preloadConfig = ReelsPreloadConfig(
            enabled = true,
            aheadCount = 2,
            behindCount = 1,
            preloadOnWifiOnly = false
        ),
        cacheConfig = ReelsCacheConfig(
            enabled = true,
            maxCacheSizeMb = 300,
            cacheKeyProvider = { it.id }
        )
    )
)
```

The implementation keeps one active player and a small bounded set of muted prepared players for nearby reels. Media requests use each `ReelItem.headers` map and cache keys default to stable item IDs.

To clear the cache:

```kotlin
ReelsPlayerCache.clear(context)
```

## Loading And Error UI

```kotlin
ReelsPlayer(
    items = reels,
    loadingContent = { item -> MyLoading(item) },
    errorContent = { item, error, retry ->
        MyError(item = item, error = error, onRetry = retry)
    }
)
```

The default loading UI shows the thumbnail if provided. The default error UI is retryable and playback errors are reported through analytics.

## Lifecycle And Performance Notes

Playback pauses on lifecycle pause/stop and resumes only when it was playing before the lifecycle pause and autoplay is enabled. Resources are released when the composable leaves composition.

The player uses a single active ExoPlayer for the visible page. Preloading is bounded by `aheadCount` and `behindCount`; do not set those values too high in memory-constrained feeds.

## Supported Formats

Media3-supported sources are supported, including MP4, HLS `.m3u8`, and DASH `.mpd`.

## Limitations

Captions are modeled through `ReelSubtitle`, but styling and caption selection UI are left to the host app. The default overlay is intentionally minimal and should be replaced for app-specific social actions.
