package com.glennmathew.composereelsplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.glennmathew.reelsplayer.ReelsPlayer
import com.glennmathew.reelsplayer.config.ReelsMuteConfig
import com.glennmathew.reelsplayer.config.ReelsPlayerConfig
import com.glennmathew.reelsplayer.config.ReelsPreloadConfig
import com.glennmathew.reelsplayer.model.ReelItem
import com.glennmathew.reelsplayer.rememberReelsPlayerController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SampleReelsScreen()
            }
        }
    }
}

@Composable
private fun SampleReelsScreen() {
    val controller = rememberReelsPlayerController()

    ReelsPlayer(
        items = sampleReels,
        controller = controller,
        config = ReelsPlayerConfig(
            muteConfig = ReelsMuteConfig(initiallyMuted = false),
            preloadConfig = ReelsPreloadConfig(
                enabled = true,
                aheadCount = 3,
                behindCount = 3
            )
        ),
        onCurrentReelChanged = { index, item ->
            println("Current reel: $index ${item.id}")
        },
        onAnalyticsEvent = { event ->
            println("Reels analytics: $event")
        },
        onLoadMore = {
            println("Sample reached load-more threshold")
        }
    )
}

private val sampleReels = listOf(
    ReelItem(
        id = "1",
        videoUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/videos/1777106552150-464c58e9-50c3-49ce-a43c-cc3ec735d5bd-trimmed_video_2026_3_25_16_42_12_compressed.mp4",
        thumbnailUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/video-screenshots/1777106552152-ff9fabbb-038e-4014-96ac-7f65cdbdca09-trimmed_video_2026_3_25_16_42_12_compressed.jpg",
        title = "The forest 🫈"
    ),
    ReelItem(
        id = "2",
        videoUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/videos/1777110350308-75ad3f3f-f3a1-44fd-936a-2c2376eed6d4-trimmed_video_2026_3_25_17_45_44_compressed.mp4",
        thumbnailUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/video-screenshots/1777110350315-3daee19e-a61e-40a4-94be-5b312fb43789-trimmed_video_2026_3_25_17_45_44_compressed.jpg"
    ),
    ReelItem(
        id = "3",
        videoUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/videos/1777129958195-b4ca51c6-6e1d-4c54-8119-b1e365ccf508-trimmed_video_2026_3_25_23_12_21_compressed.mp4",
        thumbnailUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/video-screenshots/1777129958197-43cc9d9d-a6e1-41ff-bfaf-cec11d5f14ba-trimmed_video_2026_3_25_23_12_21_compressed.jpg"
    ),
    ReelItem(
        id = "4",
        videoUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/videos/1777130224432-64e40adb-6bfc-4963-9258-55720b84659b-trimmed_video_2026_3_25_23_16_57_compressed.mp4",
        thumbnailUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/video-screenshots/1777130224435-df0b9ec6-dbb8-40e6-883f-2fdbf3e8b5f7-trimmed_video_2026_3_25_23_16_57_compressed.jpg"
    ),
    ReelItem(
        id = "5",
        videoUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/videos/1777130475880-c8ddeec3-d8ff-4a78-b71f-31035f8f306b-trimmed_video_2026_3_25_23_20_56_compressed.mp4",
        thumbnailUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/video-screenshots/1777130475881-6c1b98a2-7a2b-42ff-8e41-4d7f6c42201e-trimmed_video_2026_3_25_23_20_56_compressed.jpg"
    ),
    ReelItem(
        id = "6",
        videoUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/videos/1777130526900-5c884cac-a7e5-4ef4-a8b3-d511cb3f1604-trimmed_video_2026_3_25_23_21_58_compressed.mp4",
        thumbnailUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/video-screenshots/1777130526914-a400e4b6-7e48-4fb3-ac92-143eec6fd1f3-trimmed_video_2026_3_25_23_21_58_compressed.jpg"
    ),
    ReelItem(
        id = "7",
        videoUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/videos/1777130797469-44c9d065-5f5c-406f-85c7-0f0f162198fd-trimmed_video_2026_3_25_23_26_7_compressed.mp4",
        thumbnailUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/video-screenshots/1777130797470-ffea6ad6-430c-4b2a-98fb-9660bd95a145-trimmed_video_2026_3_25_23_26_7_compressed.jpg",
        title = "Ipinapakilala ang ChatGPT Go. Mas mabilis mag-generate ng mas maraming larawan. ₱300/month lang."
    ),
    ReelItem(
        id = "8",
        videoUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/videos/1777187030937-b1753411-b090-4543-a5a2-64a72e8ead67-trimmed_video_2026_3_26_15_3_39_compressed.mp4",
        thumbnailUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/video-screenshots/1777187030939-ba3cf083-8019-490b-b3db-c86615abeeaf-trimmed_video_2026_3_26_15_3_39_compressed.jpg"
    ),
    ReelItem(
        id = "9",
        videoUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/videos/1777187989049-3e530797-e2a5-4fe8-a1a3-e201684d4f51-trimmed_video_2026_3_26_15_19_39_compressed.mp4",
        thumbnailUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/video-screenshots/1777187989051-9fa2e636-e4fc-4a0a-980b-bfc348d57026-trimmed_video_2026_3_26_15_19_39_compressed.jpg"
    ),
    ReelItem(
        id = "10",
        videoUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/videos/1777190748416-ead1414f-fee4-4f00-af39-8dd719b76434-636_trimmed_1777190741443_compressed.mp4",
        thumbnailUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/video-screenshots/1777190748417-dabacb74-30f4-48d4-b9b6-9adbba3994ad-636_trimmed_1777190741443_compressed.jpg"
    ),
    ReelItem(
        id = "11",
        videoUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/videos/1777190886391-94f907e1-3f28-4cf7-879a-a1847e15f8fd-562_trimmed_1777190848351_compressed.mp4",
        thumbnailUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/video-screenshots/1777190886392-4b74c0bf-a5a1-4bbb-9aa4-9484df7acaf1-562_trimmed_1777190848351_compressed.jpg"
    ),
    ReelItem(
        id = "12",
        videoUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/videos/1777195778202-434989dd-3b70-4f3d-8bc0-b7164800839b-647_trimmed_1777195741757_compressed.mp4",
        thumbnailUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/video-screenshots/1777195778204-400612ee-a62c-485a-9e47-cc4ddad45f47-647_trimmed_1777195741757_compressed.jpg",
        title = "testtrr"
    ),
    ReelItem(
        id = "13",
        videoUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/videos/1777195891501-a1fbc31d-cd7c-4e31-a2c5-bc50397bb2d4-548_trimmed_1777195846965_compressed.mp4",
        thumbnailUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/video-screenshots/1777195891502-b55fda4f-3a2e-411b-88f9-f82f622e6e1f-548_trimmed_1777195846965_compressed.jpg"
    ),
    ReelItem(
        id = "14",
        videoUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/videos/1777203026780-074f6db5-ea4b-4102-aab9-f0461d55d5e2-8fcd312c-d49f-4453-98d8-c4fc1e90ed94-1_all_17482_trimmed_1777203015079_compressed.mp4",
        thumbnailUrl = "https://traycit.sgp1.cdn.digitaloceanspaces.com/terycit/clips/video-screenshots/1777203026782-515def6a-2508-4d21-bdba-973a6be80877-8fcd312c-d49f-4453-98d8-c4fc1e90ed94-1_all_17482_trimmed_1777203015079_compressed.jpg"
    )
)
