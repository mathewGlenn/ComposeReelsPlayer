plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
}

android {
    namespace = "com.glennmathew.reelsplayer"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui)

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.media3.datasource)
    implementation(libs.androidx.media3.datasource.okhttp)
    implementation(libs.androidx.media3.database)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.exoplayer.hls)
    implementation(libs.androidx.media3.ui)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = providers.gradleProperty("GROUP").get()
            artifactId = providers.gradleProperty("POM_ARTIFACT_ID").get()
            version = providers.gradleProperty("VERSION_NAME").get()

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set("Compose Reels Player")
                description.set("A Jetpack Compose reels-style video player built with Media3 ExoPlayer.")
                url.set("https://github.com/mathewGlenn/ComposeReelsPlayer")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("mathewGlenn")
                        name.set("Glenn Mathew")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/mathewGlenn/ComposeReelsPlayer.git")
                    developerConnection.set("scm:git:ssh://github.com/mathewGlenn/ComposeReelsPlayer.git")
                    url.set("https://github.com/mathewGlenn/ComposeReelsPlayer")
                }
            }
        }
    }
}
